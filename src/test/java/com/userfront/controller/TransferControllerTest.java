package com.userfront.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.ui.Model;

import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.Recipient;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.User;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

/**
 * TransferController单元测试类
 *
 * 测试目标：TransferController 转账控制器
 *
 * 测试策略：
 * 1. 模拟TransactionService验证业务逻辑调用
 * 2. 验证输入参数校验（账户类型、金额、收款人）
 * 3. 验证视图返回值和Model数据
 *
 * 覆盖的测试场景：
 * - 账户间转账页面（GET/POST）
 * - 收款人管理页面（列表/新增/编辑/删除）
 * - 转账给他人页面（GET/POST）
 * - 输入验证（金额为空/0/负数、账户类型为空、收款人未选）
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransferControllerTest {

    /**
     * 模拟TransactionService
     * 验证转账业务逻辑
     */
    @Mock
    private TransactionService transactionService;

    /**
     * 模拟UserService
     * 验证用户操作
     */
    @Mock
    private UserService userService;

    /**
     * 模拟Model
     */
    @Mock
    private Model model;

    /**
     * 模拟Principal
     */
    @Mock
    private Principal principal;

    /**
     * 被测试的Controller
     */
    @InjectMocks
    private TransferController transferController;

    /**
     * 测试用户
     */
    private User testUser;

    /**
     * 测试收款人
     */
    private Recipient testRecipient;

    /**
     * 测试前置准备
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");

        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountNumber(123456);
        primaryAccount.setAccountBalance(new BigDecimal("1000.00"));
        testUser.setPrimaryAccount(primaryAccount);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountNumber(789012);
        savingsAccount.setAccountBalance(new BigDecimal("2000.00"));
        testUser.setSavingsAccount(savingsAccount);

        testRecipient = new Recipient();
        testRecipient.setName("John Doe");
        testRecipient.setEmail("john@example.com");
    }

    // ==================== 账户间转账测试 ====================

    /**
     * 测试betweenAccounts (GET) - 获取账户间转账页面
     */
    @Test
    @DisplayName("测试betweenAccountsGet - 获取转账页面")
    void testBetweenAccountsGet() {
        // When
        String result = transferController.betweenAccounts(model);

        // Then: 验证初始化空值
        assertEquals("betweenAccounts", result);
        verify(model).addAttribute("transferFrom", "");
        verify(model).addAttribute("transferTo", "");
        verify(model).addAttribute("amount", "");
    }

    /**
     * 测试betweenAccountsPost - 转出账户未选择
     */
    @Test
    @DisplayName("测试betweenAccountsPost - 转出账户未选择")
    void testBetweenAccountsPost_FromAccountEmpty() {
        // When & Then: 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.betweenAccountsPost("", "Savings", "100", principal);
        });

        assertEquals("请选择转出/转入账户类型", exception.getMessage());
    }

    /**
     * 测试betweenAccountsPost - 转入账户未选择
     */
    @Test
    @DisplayName("测试betweenAccountsPost - 转入账户未选择")
    void testBetweenAccountsPost_ToAccountEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.betweenAccountsPost("Primary", "", "100", principal);
        });

        assertEquals("请选择转出/转入账户类型", exception.getMessage());
    }

    /**
     * 测试betweenAccountsPost - 转账金额为空
     */
    @Test
    @DisplayName("测试betweenAccountsPost - 转账金额为空")
    void testBetweenAccountsPost_AmountEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.betweenAccountsPost("Primary", "Savings", "", principal);
        });

        assertEquals("请输入转账金额", exception.getMessage());
    }

    /**
     * 测试betweenAccountsPost - 转账金额为0
     */
    @Test
    @DisplayName("测试betweenAccountsPost - 转账金额为0")
    void testBetweenAccountsPost_AmountZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.betweenAccountsPost("Primary", "Savings", "0", principal);
        });

        assertEquals("转账金额必须大于0", exception.getMessage());
    }

    /**
     * 测试betweenAccountsPost - 转账金额为负数
     */
    @Test
    @DisplayName("测试betweenAccountsPost - 转账金额为负数")
    void testBetweenAccountsPost_AmountNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.betweenAccountsPost("Primary", "Savings", "-100", principal);
        });

        assertEquals("转账金额必须大于0", exception.getMessage());
    }

    /**
     * 测试betweenAccountsPost - 转账成功
     */
    @Test
    @DisplayName("测试betweenAccountsPost - 转账成功")
    void testBetweenAccountsPost_Success() throws Exception {
        // Given
        when(principal.getName()).thenReturn("testuser");

        // When
        String result = transferController.betweenAccountsPost("Primary", "Savings", "500", principal);

        // Then: 验证重定向并调用服务
        assertEquals("redirect:/userFront", result);
        verify(transactionService).betweenAccountsTransfer("Primary", "Savings", "500", principal);
    }

    // ==================== 收款人管理测试 ====================

    /**
     * 测试recipient (GET) - 获取收款人列表页面
     */
    @Test
    @DisplayName("测试recipient - 获取收款人列表页面")
    void testRecipientGet() {
        // Given
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findRecipientList(principal)).thenReturn(recipients);

        // When
        String result = transferController.recipient(model, principal);

        // Then
        assertEquals("recipient", result);
        verify(model).addAttribute("recipientList", recipients);
        verify(model).addAttribute(eq("recipient"), any(Recipient.class));
    }

    /**
     * 测试recipientPost - 保存收款人成功
     */
    @Test
    @DisplayName("测试recipientPost - 保存收款人成功")
    void testRecipientPost_Success() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        String result = transferController.recipientPost(testRecipient, principal);

        // Then: 验证保存并重定向
        assertEquals("redirect:/transfer/recipient", result);
        verify(transactionService).saveRecipient(testRecipient);
    }

    /**
     * 测试recipientEdit (GET) - 编辑收款人页面
     */
    @Test
    @DisplayName("测试recipientEdit - 编辑收款人页面")
    void testRecipientEdit() {
        // Given
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findRecipientByName("John Doe", principal)).thenReturn(testRecipient);
        when(transactionService.findRecipientList(principal)).thenReturn(recipients);

        // When
        String result = transferController.recipientEdit("John Doe", model, principal);

        // Then
        assertEquals("recipient", result);
        verify(model).addAttribute("recipientList", recipients);
        verify(model).addAttribute("recipient", testRecipient);
    }

    /**
     * 测试recipientDelete (GET) - 删除收款人
     */
    @Test
    @DisplayName("测试recipientDelete - 删除收款人")
    void testRecipientDelete() {
        // Given
        List<Recipient> recipients = Arrays.asList();
        when(principal.getName()).thenReturn("testuser");
        doNothing().when(transactionService).deleteRecipientByName("John Doe", principal);
        when(transactionService.findRecipientList(principal)).thenReturn(recipients);

        // When
        String result = transferController.recipientDelete("John Doe", model, principal);

        // Then
        assertEquals("recipient", result);
        verify(transactionService).deleteRecipientByName("John Doe", principal);
        verify(model).addAttribute("recipientList", recipients);
        verify(model).addAttribute(eq("recipient"), any(Recipient.class));
    }

    // ==================== 转账给他人测试 ====================

    /**
     * 测试toSomeoneElse (GET) - 获取转账给他人页面
     */
    @Test
    @DisplayName("测试toSomeoneElse - 获取转账页面")
    void testToSomeoneElseGet() {
        // Given
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findRecipientList(principal)).thenReturn(recipients);

        // When
        String result = transferController.toSomeoneElse(model, principal);

        // Then
        assertEquals("toSomeoneElse", result);
        verify(model).addAttribute("recipientList", recipients);
        verify(model).addAttribute("accountType", "");
    }

    /**
     * 测试toSomeoneElsePost - 收款人未选择
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 收款人未选择")
    void testToSomeoneElsePost_RecipientEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.toSomeoneElsePost("", "Primary", "100", principal);
        });

        assertEquals("请选择收款人", exception.getMessage());
    }

    /**
     * 测试toSomeoneElsePost - 账户类型未选择
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 账户类型未选择")
    void testToSomeoneElsePost_AccountTypeEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.toSomeoneElsePost("John Doe", "", "100", principal);
        });

        assertEquals("请选择转出账号类型", exception.getMessage());
    }

    /**
     * 测试toSomeoneElsePost - 转账金额为空
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 转账金额为空")
    void testToSomeoneElsePost_AmountEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.toSomeoneElsePost("John Doe", "Primary", "", principal);
        });

        assertEquals("请输入转账金额", exception.getMessage());
    }

    /**
     * 测试toSomeoneElsePost - 转账金额为0
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 转账金额为0")
    void testToSomeoneElsePost_AmountZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.toSomeoneElsePost("John Doe", "Primary", "0", principal);
        });

        assertEquals("转账金额必须大于0", exception.getMessage());
    }

    /**
     * 测试toSomeoneElsePost - 转账金额为负数
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 转账金额为负数")
    void testToSomeoneElsePost_AmountNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            transferController.toSomeoneElsePost("John Doe", "Primary", "-100", principal);
        });

        assertEquals("转账金额必须大于0", exception.getMessage());
    }

    /**
     * 测试toSomeoneElsePost - 转账成功
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 转账成功")
    void testToSomeoneElsePost_Success() throws Exception {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findRecipientByName("John Doe", principal)).thenReturn(testRecipient);

        // When
        String result = transferController.toSomeoneElsePost("John Doe", "Primary", "300", principal);

        // Then
        assertEquals("redirect:/userFront", result);
        verify(transactionService).toSomeoneElseTransfer(testRecipient, "Primary", "300", principal);
    }

    /**
     * 测试toSomeoneElsePost - 储蓄账户转账成功
     */
    @Test
    @DisplayName("测试toSomeoneElsePost - 储蓄账户转账成功")
    void testToSomeoneElsePost_SavingsSuccess() throws Exception {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findRecipientByName("John Doe", principal)).thenReturn(testRecipient);

        // When
        String result = transferController.toSomeoneElsePost("John Doe", "Savings", "500", principal);

        // Then
        assertEquals("redirect:/userFront", result);
        verify(transactionService).toSomeoneElseTransfer(testRecipient, "Savings", "500", principal);
    }
}