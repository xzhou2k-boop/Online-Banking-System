package com.userfront.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.security.Principal;

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
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.AccountService;
import com.userfront.service.TransactionService;
import com.userfront.service.UserService;

/**
 * AccountController单元测试类
 *
 * 测试目标：AccountController 账户管理控制器
 *
 * 测试策略：
 * 1. 模拟Service层验证业务调用
 * 2. 验证输入参数校验（金额、账户类型）
 * 3. 验证视图返回值和Model数据
 *
 * 覆盖的测试场景：
 * - 主账户页面访问
 * - 储蓄账户页面访问
 * - 存款页面（GET/POST）
 * - 取款页面（GET/POST）
 * - 输入验证（金额为空/0/负数、账户类型为空）
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountControllerTest {

    /**
     * 模拟UserService
     * 用于获取用户信息
     */
    @Mock
    private UserService userService;

    /**
     * 模拟AccountService
     * 验证存取款业务逻辑
     */
    @Mock
    private AccountService accountService;

    /**
     * 模拟TransactionService
     * 验证交易记录查询
     */
    @Mock
    private TransactionService transactionService;

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
    private AccountController accountController;

    /**
     * 测试用户
     */
    private User testUser;

    /**
     * 主账户
     */
    private PrimaryAccount primaryAccount;

    /**
     * 储蓄账户
     */
    private SavingsAccount savingsAccount;

    /**
     * 测试前置准备
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");

        primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountNumber(123456);
        primaryAccount.setAccountBalance(new BigDecimal("1000.00"));

        savingsAccount = new SavingsAccount();
        savingsAccount.setAccountNumber(789012);
        savingsAccount.setAccountBalance(new BigDecimal("2000.00"));

        testUser.setPrimaryAccount(primaryAccount);
        testUser.setSavingsAccount(savingsAccount);
    }

    // ==================== 账户页面测试 ====================

    /**
     * 测试primaryAccount - 获取主账户页面
     *
     * 测试场景：用户查看主账户及交易记录
     *
     * 业务逻辑：
     * 1. 获取当前用户
     * 2. 查询主账户交易列表
     * 3. 传递账户和交易记录到视图
     */
    @Test
    @DisplayName("测试primaryAccount - 获取主账户页面")
    void testPrimaryAccount() {
        // Given: 有交易记录
        List<PrimaryTransaction> transactions = Arrays.asList(new PrimaryTransaction());
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findPrimaryTransactionList("testuser")).thenReturn(transactions);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 访问主账户页
        String result = accountController.primaryAccount(model, principal);

        // Then: 验证返回视图和数据
        assertEquals("primaryAccount", result);
        verify(model).addAttribute("primaryAccount", primaryAccount);
        verify(model).addAttribute("primaryTransactionList", transactions);
    }

    /**
     * 测试savingsAccount - 获取储蓄账户页面
     */
    @Test
    @DisplayName("测试savingsAccount - 获取储蓄账户页面")
    void testSavingsAccount() {
        // Given
        List<SavingsTransaction> transactions = Arrays.asList(new SavingsTransaction());
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findSavingsTransactionList("testuser")).thenReturn(transactions);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        String result = accountController.savingsAccount(model, principal);

        // Then
        assertEquals("savingsAccount", result);
        verify(model).addAttribute("savingsAccount", savingsAccount);
        verify(model).addAttribute("savingsTransactionList", transactions);
    }

    /**
     * 测试primaryAccount - 无交易记录
     */
    @Test
    @DisplayName("测试primaryAccount - 无交易记录")
    void testPrimaryAccount_NoTransactions() {
        // Given: 无交易记录
        when(principal.getName()).thenReturn("testuser");
        when(transactionService.findPrimaryTransactionList("testuser")).thenReturn(Arrays.asList());
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        String result = accountController.primaryAccount(model, principal);

        // Then
        assertEquals("primaryAccount", result);
        verify(model).addAttribute("primaryTransactionList", Arrays.asList());
    }

    // ==================== 存款功能测试 ====================

    /**
     * 测试deposit (GET) - 获取存款页面
     */
    @Test
    @DisplayName("测试depositGet - 获取存款页面")
    void testDepositGet() {
        // When
        String result = accountController.deposit(model);

        // Then: 验证初始化空值
        assertEquals("deposit", result);
        verify(model).addAttribute("accountType", "");
        verify(model).addAttribute("amount", "");
    }

    /**
     * 测试depositPOST - 存款金额为空
     *
     * 验证要点：Controller层参数校验
     */
    @Test
    @DisplayName("测试depositPOST - 存款金额为空")
    void testDepositPOST_AmountEmpty() {
        // When & Then: 验证抛出异常
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.depositPOST("", "Primary", principal);
        });
        assertEquals("请输入存款金额", exception.getMessage());
    }

    /**
     * 测试depositPOST - 存款金额为0
     */
    @Test
    @DisplayName("测试depositPOST - 存款金额为0")
    void testDepositPOST_AmountZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.depositPOST("0", "Primary", principal);
        });
        assertEquals("存款金额必须大于0", exception.getMessage());
    }

    /**
     * 测试depositPOST - 存款金额为负数
     */
    @Test
    @DisplayName("测试depositPOST - 存款金额为负数")
    void testDepositPOST_AmountNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.depositPOST("-100", "Primary", principal);
        });
        assertEquals("存款金额必须大于0", exception.getMessage());
    }

    /**
     * 测试depositPOST - 账户类型为空
     */
    @Test
    @DisplayName("测试depositPOST - 账户类型为空")
    void testDepositPOST_AccountTypeEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.depositPOST("100", "", principal);
        });
        assertEquals("请选择账户类型", exception.getMessage());
    }

    /**
     * 测试depositPOST - 存款成功
     *
     * 测试场景：验证通过，调用存款服务
     */
    @Test
    @DisplayName("测试depositPOST - 存款成功")
    void testDepositPOST_Success() {
        // Given
        when(principal.getName()).thenReturn("testuser");

        // When: 存款500元到主账户
        String result = accountController.depositPOST("500", "Primary", principal);

        // Then: 验证重定向并调用服务
        assertEquals("redirect:/userFront", result);
        verify(accountService).deposit("Primary", 500.0, principal);
    }

    /**
     * 测试depositPOST - 储蓄账户存款
     */
    @Test
    @DisplayName("测试depositPOST - 储蓄账户存款")
    void testDepositPOST_SavingsAccount() {
        // Given
        when(principal.getName()).thenReturn("testuser");

        // When
        String result = accountController.depositPOST("1000", "Savings", principal);

        // Then
        assertEquals("redirect:/userFront", result);
        verify(accountService).deposit("Savings", 1000.0, principal);
    }

    // ==================== 取款功能测试 ====================

    /**
     * 测试withdraw (GET) - 获取取款页面
     */
    @Test
    @DisplayName("测试withdrawGet - 获取取款页面")
    void testWithdrawGet() {
        // When
        String result = accountController.withdraw(model);

        // Then
        assertEquals("withdraw", result);
        verify(model).addAttribute("accountType", "");
        verify(model).addAttribute("amount", "");
    }

    /**
     * 测试withdrawPOST - 取款金额为空
     */
    @Test
    @DisplayName("测试withdrawPOST - 取款金额为空")
    void testWithdrawPOST_AmountEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.withdrawPOST("", "Primary", principal);
        });
        assertEquals("请输入取款金额", exception.getMessage());
    }

    /**
     * 测试withdrawPOST - 取款金额为0
     */
    @Test
    @DisplayName("测试withdrawPOST - 取款金额为0")
    void testWithdrawPOST_AmountZero() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.withdrawPOST("0", "Primary", principal);
        });
        assertEquals("取款金额必须大于0", exception.getMessage());
    }

    /**
     * 测试withdrawPOST - 取款金额为负数
     */
    @Test
    @DisplayName("测试withdrawPOST - 取款金额为负数")
    void testWithdrawPOST_AmountNegative() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.withdrawPOST("-100", "Primary", principal);
        });
        assertEquals("取款金额必须大于0", exception.getMessage());
    }

    /**
     * 测试withdrawPOST - 账户类型为空
     */
    @Test
    @DisplayName("测试withdrawPOST - 账户类型为空")
    void testWithdrawPOST_AccountTypeEmpty() {
        // When & Then
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            accountController.withdrawPOST("100", "", principal);
        });
        assertEquals("请选择账户类型", exception.getMessage());
    }

    /**
     * 测试withdrawPOST - 取款成功
     */
    @Test
    @DisplayName("测试withdrawPOST - 取款成功")
    void testWithdrawPOST_Success() {
        // Given
        when(principal.getName()).thenReturn("testuser");

        // When
        String result = accountController.withdrawPOST("500", "Primary", principal);

        // Then
        assertEquals("redirect:/userFront", result);
        verify(accountService).withdraw("Primary", 500.0, principal);
    }

    /**
     * 测试withdrawPOST - 储蓄账户取款
     */
    @Test
    @DisplayName("测试withdrawPOST - 储蓄账户取款")
    void testWithdrawPOST_SavingsAccount() {
        // Given
        when(principal.getName()).thenReturn("testuser");

        // When
        String result = accountController.withdrawPOST("500", "Savings", principal);

        // Then
        assertEquals("redirect:/userFront", result);
        verify(accountService).withdraw("Savings", 500.0, principal);
    }
}