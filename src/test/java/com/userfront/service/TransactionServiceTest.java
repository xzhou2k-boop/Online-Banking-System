package com.userfront.service;

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

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.PrimaryTransactionDao;
import com.userfront.dao.RecipientDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.dao.SavingsTransactionDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.PrimaryTransaction;
import com.userfront.domain.Recipient;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.SavingsTransaction;
import com.userfront.domain.User;
import com.userfront.service.UserServiceImpl.TransactionServiceImpl;

/**
 * TransactionService单元测试类
 *
 * 测试目标：TransactionServiceImpl 交易服务层
 *
 * 测试策略：
 * 1. 使用Mockito模拟所有DAO层依赖
 * 2. 重点测试账户间转账、转账给他人、收款人管理功能
 * 3. 验证交易记录的正确创建
 * 4. 测试余额不足等异常场景
 *
 * 覆盖的测试场景：
 * - 查询主账户/储蓄账户交易记录
 * - 账户间转账（主->储、储->主）
 * - 转账给他人
 * - 收款人CRUD操作
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class TransactionServiceTest {

    /**
     * 模拟用户服务
     */
    @Mock
    private UserService userService;

    /**
     * 模拟主账户交易DAO
     */
    @Mock
    private PrimaryTransactionDao primaryTransactionDao;

    /**
     * 模拟储蓄账户交易DAO
     */
    @Mock
    private SavingsTransactionDao savingsTransactionDao;

    /**
     * 模拟主账户DAO
     */
    @Mock
    private PrimaryAccountDao primaryAccountDao;

    /**
     * 模拟储蓄账户DAO
     */
    @Mock
    private SavingsAccountDao savingsAccountDao;

    /**
     * 模拟收款人DAO
     */
    @Mock
    private RecipientDao recipientDao;

    /**
     * 模拟Principal
     */
    @Mock
    private Principal principal;

    /**
     * 被测试的服务实现
     */
    @InjectMocks
    private TransactionServiceImpl transactionService;

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
     * 收款人
     */
    private Recipient testRecipient;

    /**
     * 测试前置准备
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountNumber(123456);
        primaryAccount.setAccountBalance(new BigDecimal("1000.00"));

        savingsAccount = new SavingsAccount();
        savingsAccount.setAccountNumber(789012);
        savingsAccount.setAccountBalance(new BigDecimal("2000.00"));

        testUser.setPrimaryAccount(primaryAccount);
        testUser.setSavingsAccount(savingsAccount);

        testRecipient = new Recipient();
        testRecipient.setName("John Doe");
        testRecipient.setEmail("john@example.com");
        testRecipient.setAccountNumber("recipient123");
    }

    // ==================== 交易记录查询测试 ====================

    /**
     * 测试findPrimaryTransactionList - 查询主账户交易记录
     *
     * 测试场景：获取当前用户的主账户交易列表
     */
    @Test
    @DisplayName("测试findPrimaryTransactionList - 查询主账户交易记录")
    void testFindPrimaryTransactionList() {
        // Given: 模拟交易列表
        PrimaryTransaction transaction = new PrimaryTransaction();
        List<PrimaryTransaction> transactions = Arrays.asList(transaction);
        primaryAccount.setPrimaryTransactionList(transactions);

        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        List<PrimaryTransaction> result = transactionService.findPrimaryTransactionList("testuser");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * 测试findSavingsTransactionList - 查询储蓄账户交易记录
     */
    @Test
    @DisplayName("测试findSavingsTransactionList - 查询储蓄账户交易记录")
    void testFindSavingsTransactionList() {
        // Given
        SavingsTransaction transaction = new SavingsTransaction();
        List<SavingsTransaction> transactions = Arrays.asList(transaction);
        savingsAccount.setSavingsTransactionList(transactions);

        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        List<SavingsTransaction> result = transactionService.findSavingsTransactionList("testuser");

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    // ==================== 交易记录保存测试 ====================

    /**
     * 测试savePrimaryDepositTransaction - 保存主账户存款交易
     */
    @Test
    @DisplayName("测试savePrimaryDepositTransaction - 保存主账户存款交易")
    void testSavePrimaryDepositTransaction() {
        // Given
        PrimaryTransaction transaction = new PrimaryTransaction();

        // When
        transactionService.savePrimaryDepositTransaction(transaction);

        // Then
        verify(primaryTransactionDao, times(1)).save(transaction);
    }

    /**
     * 测试saveSavingsDepositTransaction - 保存储蓄账户存款交易
     */
    @Test
    @DisplayName("测试saveSavingsDepositTransaction - 保存储蓄账户存款交易")
    void testSaveSavingsDepositTransaction() {
        // Given
        SavingsTransaction transaction = new SavingsTransaction();

        // When
        transactionService.saveSavingsDepositTransaction(transaction);

        // Then
        verify(savingsTransactionDao, times(1)).save(transaction);
    }

    /**
     * 测试savePrimaryWithdrawTransaction - 保存主账户取款交易
     */
    @Test
    @DisplayName("测试savePrimaryWithdrawTransaction - 保存主账户取款交易")
    void testSavePrimaryWithdrawTransaction() {
        // Given
        PrimaryTransaction transaction = new PrimaryTransaction();

        // When
        transactionService.savePrimaryWithdrawTransaction(transaction);

        // Then
        verify(primaryTransactionDao, times(1)).save(transaction);
    }

    /**
     * 测试saveSavingsWithdrawTransaction - 保存储蓄账户取款交易
     */
    @Test
    @DisplayName("测试saveSavingsWithdrawTransaction - 保存储蓄账户取款交易")
    void testSaveSavingsWithdrawTransaction() {
        // Given
        SavingsTransaction transaction = new SavingsTransaction();

        // When
        transactionService.saveSavingsWithdrawTransaction(transaction);

        // Then
        verify(savingsTransactionDao, times(1)).save(transaction);
    }

    // ==================== 账户间转账测试 ====================

    /**
     * 测试betweenAccountsTransfer - 主账户转储蓄账户成功
     *
     * 测试场景：主账户余额充足，转账成功
     *
     * 业务逻辑：
     * 1. 验证转出账户余额充足
     * 2. 更新转出账户余额（减少）
     * 3. 更新转入账户余额（增加）
     * 4. 创建两笔交易记录
     */
    @Test
    @DisplayName("测试betweenAccountsTransfer - 主账户转储蓄账户成功")
    void testBetweenAccountsTransfer_PrimaryToSavings() throws Exception {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);
        when(savingsAccountDao.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        // When: 主账户转500元到储蓄账户
        transactionService.betweenAccountsTransfer("Primary", "Savings", "500", principal);

        // Then: 验证余额变化
        assertEquals(new BigDecimal("500.00"), primaryAccount.getAccountBalance());
        assertEquals(new BigDecimal("2500.00"), savingsAccount.getAccountBalance());
        verify(primaryTransactionDao, times(1)).save(any(PrimaryTransaction.class));
        verify(savingsTransactionDao, times(1)).save(any(SavingsTransaction.class));
    }

    /**
     * 测试betweenAccountsTransfer - 储蓄账户转主账户成功
     */
    @Test
    @DisplayName("测试betweenAccountsTransfer - 储蓄账户转主账户成功")
    void testBetweenAccountsTransfer_SavingsToPrimary() throws Exception {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);
        when(savingsAccountDao.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        // When: 储蓄账户转1000元到主账户
        transactionService.betweenAccountsTransfer("Savings", "Primary", "1000", principal);

        // Then
        assertEquals(new BigDecimal("2000.00"), primaryAccount.getAccountBalance());
        assertEquals(new BigDecimal("1000.00"), savingsAccount.getAccountBalance());
    }

    /**
     * 测试betweenAccountsTransfer - 主账户余额不足
     *
     * 测试场景：转账金额超过主账户余额
     *
     * 验证要点：抛出余额不足异常
     */
    @Test
    @DisplayName("测试betweenAccountsTransfer - 主账户余额不足")
    void testBetweenAccountsTransfer_InsufficientFunds_Primary() {
        // Given: 尝试转账2000元（余额仅1000元）
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then: 验证抛出异常
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.betweenAccountsTransfer("Primary", "Savings", "2000", principal);
        });

        assertEquals("主账户余额不足", exception.getMessage());
    }

    /**
     * 测试betweenAccountsTransfer - 储蓄账户余额不足
     */
    @Test
    @DisplayName("测试betweenAccountsTransfer - 储蓄账户余额不足")
    void testBetweenAccountsTransfer_InsufficientFunds_Savings() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.betweenAccountsTransfer("Savings", "Primary", "5000", principal);
        });

        assertEquals("储蓄账户余额不足", exception.getMessage());
    }

    /**
     * 测试betweenAccountsTransfer - 无效的转账操作
     *
     * 测试场景：相同账户之间转账
     */
    @Test
    @DisplayName("测试betweenAccountsTransfer - 无效转账操作")
    void testBetweenAccountsTransfer_InvalidOperation() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then
        Exception exception = assertThrows(Exception.class, () -> {
            transactionService.betweenAccountsTransfer("Primary", "Primary", "100", principal);
        });

        assertEquals("无效的转账操作", exception.getMessage());
    }

    // ==================== 收款人管理测试 ====================

    /**
     * 测试findRecipientList - 查询收款人列表
     */
    @Test
    @DisplayName("测试findRecipientList - 查询收款人列表")
    void testFindRecipientList() {
        // Given
        List<Recipient> recipients = Arrays.asList(testRecipient);
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(recipientDao.findByUser(testUser)).thenReturn(recipients);

        // When
        List<Recipient> result = transactionService.findRecipientList(principal);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * 测试saveRecipient - 保存收款人
     */
    @Test
    @DisplayName("测试saveRecipient - 保存收款人")
    void testSaveRecipient() {
        // Given
        when(recipientDao.save(testRecipient)).thenReturn(testRecipient);

        // When
        Recipient result = transactionService.saveRecipient(testRecipient);

        // Then
        assertNotNull(result);
        verify(recipientDao, times(1)).save(testRecipient);
    }

    /**
     * 测试findRecipientByName - 按名称查找收款人
     */
    @Test
    @DisplayName("测试findRecipientByName - 按名称查找收款人")
    void testFindRecipientByName() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(recipientDao.findByNameAndUser("John Doe", testUser)).thenReturn(testRecipient);

        // When
        Recipient result = transactionService.findRecipientByName("John Doe", principal);

        // Then
        assertNotNull(result);
        assertEquals("John Doe", result.getName());
    }

    /**
     * 测试deleteRecipientByName - 删除收款人
     */
    @Test
    @DisplayName("测试deleteRecipientByName - 删除收款人")
    void testDeleteRecipientByName() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        transactionService.deleteRecipientByName("John Doe", principal);

        // Then
        verify(recipientDao, times(1)).deleteByNameAndUser("John Doe", testUser);
    }

    // ==================== 转账给他人测试 ====================

    /**
     * 测试toSomeoneElseTransfer - 主账户转账成功
     *
     * 测试场景：通过主账户向他人转账
     *
     * 业务逻辑：
     * 1. 查找收款人账户
     * 2. 验证转出账户余额充足
     * 3. 更新转出账户余额
     * 4. 更新收款人账户余额
     * 5. 创建交易记录
     */
    @Test
    @DisplayName("测试toSomeoneElseTransfer - 主账户转账成功")
    void testToSomeoneElseTransfer_Primary() throws Exception {
        // Given: 收款人
        User recipientUser = new User();
        recipientUser.setUsername("recipient123");
        PrimaryAccount recipientPrimaryAccount = new PrimaryAccount();
        recipientPrimaryAccount.setAccountBalance(new BigDecimal("500.00"));
        recipientUser.setPrimaryAccount(recipientPrimaryAccount);

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("recipient123")).thenReturn(recipientUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 转账300元
        transactionService.toSomeoneElseTransfer(testRecipient, "Primary", "300", principal);

        // Then: 验证余额变化
        assertEquals(new BigDecimal("700.00"), primaryAccount.getAccountBalance());
        assertEquals(new BigDecimal("800.00"), recipientPrimaryAccount.getAccountBalance());
    }

    /**
     * 测试toSomeoneElseTransfer - 储蓄账户转账成功
     */
    @Test
    @DisplayName("测试toSomeoneElseTransfer - 储蓄账户转账成功")
    void testToSomeoneElseTransfer_Savings() throws Exception {
        // Given
        User recipientUser = new User();
        recipientUser.setUsername("recipient123");
        SavingsAccount recipientSavingsAccount = new SavingsAccount();
        recipientSavingsAccount.setAccountBalance(new BigDecimal("500.00"));
        recipientUser.setSavingsAccount(recipientSavingsAccount);

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("recipient123")).thenReturn(recipientUser);
        when(savingsAccountDao.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        // When
        transactionService.toSomeoneElseTransfer(testRecipient, "Savings", "500", principal);

        // Then
        assertEquals(new BigDecimal("1500.00"), savingsAccount.getAccountBalance());
        assertEquals(new BigDecimal("1000.00"), recipientSavingsAccount.getAccountBalance());
    }

    /**
     * 测试toSomeoneElseTransfer - 收款人账户不存在
     */
    @Test
    @DisplayName("测试toSomeoneElseTransfer - 收款人账户不存在")
    void testToSomeoneElseTransfer_RecipientNotFound() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("recipient123")).thenReturn(null);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.toSomeoneElseTransfer(testRecipient, "Primary", "100", principal);
        });

        assertEquals("收款人账户不存在，无法转账", exception.getMessage());
    }

    /**
     * 测试toSomeoneElseTransfer - 主账户余额不足
     */
    @Test
    @DisplayName("测试toSomeoneElseTransfer - 主账户余额不足")
    void testToSomeoneElseTransfer_InsufficientFunds_Primary() {
        // Given
        User recipientUser = new User();
        recipientUser.setUsername("recipient123");
        recipientUser.setPrimaryAccount(new PrimaryAccount());

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("recipient123")).thenReturn(recipientUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.toSomeoneElseTransfer(testRecipient, "Primary", "2000", principal);
        });

        assertEquals("主账户余额不足", exception.getMessage());
    }

    /**
     * 测试toSomeoneElseTransfer - 储蓄账户余额不足
     */
    @Test
    @DisplayName("测试toSomeoneElseTransfer - 储蓄账户余额不足")
    void testToSomeoneElseTransfer_InsufficientFunds_Savings() {
        // Given
        User recipientUser = new User();
        recipientUser.setUsername("recipient123");
        recipientUser.setSavingsAccount(new SavingsAccount());

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("recipient123")).thenReturn(recipientUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            transactionService.toSomeoneElseTransfer(testRecipient, "Savings", "5000", principal);
        });

        assertEquals("储蓄账户余额不足", exception.getMessage());
    }

    // ==================== 查询所有交易测试 ====================

    /**
     * 测试findAllPrimaryTransactions - 查询所有主账户交易
     */
    @Test
    @DisplayName("测试findAllPrimaryTransactions - 查询所有主账户交易")
    void testFindAllPrimaryTransactions() {
        // Given
        PrimaryTransaction transaction = new PrimaryTransaction();
        when(primaryTransactionDao.findAll()).thenReturn(Arrays.asList(transaction));

        // When
        List<PrimaryTransaction> result = transactionService.findAllPrimaryTransactions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    /**
     * 测试findAllSavingsTransactions - 查询所有储蓄账户交易
     */
    @Test
    @DisplayName("测试findAllSavingsTransactions - 查询所有储蓄账户交易")
    void testFindAllSavingsTransactions() {
        // Given
        SavingsTransaction transaction = new SavingsTransaction();
        when(savingsTransactionDao.findAll()).thenReturn(Arrays.asList(transaction));

        // When
        List<SavingsTransaction> result = transactionService.findAllSavingsTransactions();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
    }
}