package com.userfront.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
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

import com.userfront.dao.PrimaryAccountDao;
import com.userfront.dao.SavingsAccountDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.User;
import com.userfront.service.UserServiceImpl.AccountServiceImpl;
import com.userfront.service.TransactionService;

/**
 * AccountService单元测试类
 *
 * 测试目标：AccountServiceImpl 账户服务层的核心功能
 *
 * 测试策略：
 * 1. 使用Mockito模拟所有数据访问层和业务依赖
 * 2. 重点测试账户创建、存款、取款核心业务逻辑
 * 3. 验证余额计算正确性
 * 4. 测试异常场景（余额不足）
 *
 * 覆盖的测试场景：
 * - 主账户创建
 * - 储蓄账户创建
 * - 存款到主账户/储蓄账户
 * - 从主账户/储蓄账户取款
 * - 余额不足异常
 * - 账户类型大小写不敏感
 * - 多次存取款余额累计
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class AccountServiceTest {

    /**
     * 模拟主账户数据访问层
     * 验证主账户的创建、查询、保存操作
     */
    @Mock
    private PrimaryAccountDao primaryAccountDao;

    /**
     * 模拟储蓄账户数据访问层
     * 验证储蓄账户的创建、查询、保存操作
     */
    @Mock
    private SavingsAccountDao savingsAccountDao;

    /**
     * 模拟用户服务
     * 用于获取当前用户及其账户信息
     */
    @Mock
    private UserService userService;

    /**
     * 模拟交易服务
     * 验证存款/取款时交易记录的创建
     */
    @Mock
    private TransactionService transactionService;

    /**
     * 模拟安全主体
     * 代表当前登录用户
     */
    @Mock
    private Principal principal;

    /**
     * 被测试的账户服务实现类
     */
    @InjectMocks
    private AccountServiceImpl accountService;

    /**
     * 测试用户数据
     */
    private User testUser;

    /**
     * 测试用主账户
     * 初始余额：1000.00
     */
    private PrimaryAccount primaryAccount;

    /**
     * 测试用储蓄账户
     * 初始余额：2000.00
     */
    private SavingsAccount savingsAccount;

    /**
     * 测试前置准备
     * 初始化测试用户及其关联的账户
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

    // ==================== 账户创建测试 ====================

    /**
     * 测试createPrimaryAccount - 创建主账户
     *
     * 测试场景：新用户注册时自动创建主账户
     *
     * 验证要点：
     * - 账户余额初始为0
     * - 生成唯一账户编号
     * - 正确保存到数据库
     */
    @Test
    @DisplayName("测试createPrimaryAccount - 创建主账户")
    void testCreatePrimaryAccount() {
        // Given: 模拟数据库保存和查询返回
        PrimaryAccount newAccount = new PrimaryAccount();
        newAccount.setAccountBalance(new BigDecimal("0.0"));
        newAccount.setAccountNumber(11223146);

        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(newAccount);
        when(primaryAccountDao.findByAccountNumber(11223146)).thenReturn(newAccount);

        // When: 执行创建
        PrimaryAccount result = accountService.createPrimaryAccount();

        // Then: 验证创建结果
        assertNotNull(result, "创建的账户不应为空");
        assertEquals(new BigDecimal("0.0"), result.getAccountBalance(), "初始余额应为0");
        verify(primaryAccountDao, atLeastOnce()).save(any(PrimaryAccount.class));
    }

    /**
     * 测试createSavingsAccount - 创建储蓄账户
     *
     * 验证要点：与主账户创建类似的流程
     */
    @Test
    @DisplayName("测试createSavingsAccount - 创建储蓄账户")
    void testCreateSavingsAccount() {
        // Given
        SavingsAccount newAccount = new SavingsAccount();
        newAccount.setAccountBalance(new BigDecimal("0.0"));
        newAccount.setAccountNumber(11223147);

        when(savingsAccountDao.save(any(SavingsAccount.class))).thenReturn(newAccount);
        when(savingsAccountDao.findByAccountNumber(11223147)).thenReturn(newAccount);

        // When
        SavingsAccount result = accountService.createSavingsAccount();

        // Then
        assertNotNull(result);
        assertEquals(new BigDecimal("0.0"), result.getAccountBalance());
    }

    // ==================== 存款功能测试 ====================

    /**
     * 测试deposit - 存入主账户
     *
     * 测试场景：用户向主账户存入资金
     *
     * 业务逻辑：
     * 1. 获取当前用户
     * 2. 更新主账户余额（原余额 + 存款金额）
     * 3. 保存账户信息
     * 4. 创建存款交易记录
     *
     * 验证要点：余额正确增加，交易记录创建
     */
    @Test
    @DisplayName("测试deposit - 存入主账户")
    void testDepositToPrimaryAccount() {
        // Given: 模拟依赖返回
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 执行存款500元
        accountService.deposit("Primary", 500.0, principal);

        // Then: 验证余额从1000变为1500
        assertEquals(new BigDecimal("1500.00"), primaryAccount.getAccountBalance(),
            "存款后余额应为 1000 + 500 = 1500");
        verify(primaryAccountDao).save(primaryAccount);
        verify(transactionService).savePrimaryDepositTransaction(any());
    }

    /**
     * 测试deposit - 存入储蓄账户
     */
    @Test
    @DisplayName("测试deposit - 存入储蓄账户")
    void testDepositToSavingsAccount() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(savingsAccountDao.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        // When: 存款300元
        accountService.deposit("Savings", 300.0, principal);

        // Then: 余额从2000变为2300
        assertEquals(new BigDecimal("2300.00"), savingsAccount.getAccountBalance());
        verify(savingsAccountDao).save(savingsAccount);
        verify(transactionService).saveSavingsDepositTransaction(any());
    }

    /**
     * 测试deposit - 账户类型大小写不敏感
     *
     * 验证要点："PRIMARY"、"primary"、"Primary"都应正确处理
     */
    @Test
    @DisplayName("测试deposit - 不区分大小写")
    void testDepositCaseInsensitive() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 使用大写账户类型
        accountService.deposit("PRIMARY", 500.0, principal);

        // Then: 同样应正确处理
        assertEquals(new BigDecimal("1500.00"), primaryAccount.getAccountBalance());
    }

    // ==================== 取款功能测试 ====================

    /**
     * 测试withdraw - 从主账户取款成功
     *
     * 测试场景：用户从主账户取出资金（余额充足）
     *
     * 业务逻辑：
     * 1. 验证余额充足
     * 2. 更新账户余额（原余额 - 取款金额）
     * 3. 保存账户信息
     * 4. 创建取款交易记录
     */
    @Test
    @DisplayName("测试withdraw - 从主账户取款成功")
    void testWithdrawFromPrimaryAccount_Success() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 取款500元
        accountService.withdraw("Primary", 500.0, principal);

        // Then: 余额从1000变为500
        assertEquals(new BigDecimal("500.00"), primaryAccount.getAccountBalance());
        verify(primaryAccountDao).save(primaryAccount);
        verify(transactionService).savePrimaryWithdrawTransaction(any());
    }

    /**
     * 测试withdraw - 从储蓄账户取款成功
     */
    @Test
    @DisplayName("测试withdraw - 从储蓄账户取款成功")
    void testWithdrawFromSavingsAccount_Success() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(savingsAccountDao.save(any(SavingsAccount.class))).thenReturn(savingsAccount);

        // When: 取款1000元
        accountService.withdraw("Savings", 1000.0, principal);

        // Then: 余额从2000变为1000
        assertEquals(new BigDecimal("1000.00"), savingsAccount.getAccountBalance());
        verify(savingsAccountDao).save(savingsAccount);
    }

    /**
     * 测试withdraw - 主账户余额不足
     *
     * 测试场景：取款金额超过账户余额
     *
     * 验证要点：抛出余额不足异常，余额保持不变
     */
    @Test
    @DisplayName("测试withdraw - 主账户余额不足")
    void testWithdrawFromPrimaryAccount_InsufficientFunds() {
        // Given: 尝试取款2000元（余额仅1000元）
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then: 验证抛出异常
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.withdraw("Primary", 2000.0, principal);
        });

        assertEquals("主账户余额不足", exception.getMessage());
    }

    /**
     * 测试withdraw - 储蓄账户余额不足
     */
    @Test
    @DisplayName("测试withdraw - 储蓄账户余额不足")
    void testWithdrawFromSavingsAccount_InsufficientFunds() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When & Then
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            accountService.withdraw("Savings", 5000.0, principal);
        });

        assertEquals("储蓄账户余额不足", exception.getMessage());
    }

    /**
     * 测试withdraw - 账户类型大小写不敏感
     */
    @Test
    @DisplayName("测试withdraw - 不区分大小写")
    void testWithdrawCaseInsensitive() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 使用大写
        accountService.withdraw("PRIMARY", 500.0, principal);

        // Then
        assertEquals(new BigDecimal("500.00"), primaryAccount.getAccountBalance());
    }

    // ==================== 多次交易测试 ====================

    /**
     * 测试deposit - 多次存款累加
     *
     * 验证要点：多次存款后余额正确累加
     */
    @Test
    @DisplayName("测试deposit - 多次存款累加")
    void testMultipleDeposits() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 三次存款
        accountService.deposit("Primary", 100.0, principal);
        accountService.deposit("Primary", 200.0, principal);
        accountService.deposit("Primary", 300.0, principal);

        // Then: 余额 = 1000 + 100 + 200 + 300 = 1600
        assertEquals(new BigDecimal("1600.00"), primaryAccount.getAccountBalance());
    }

    /**
     * 测试withdraw - 多次取款累减
     */
    @Test
    @DisplayName("测试withdraw - 多次取款累减")
    void testMultipleWithdraws() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(primaryAccountDao.save(any(PrimaryAccount.class))).thenReturn(primaryAccount);

        // When: 两次取款
        accountService.withdraw("Primary", 100.0, principal);
        accountService.withdraw("Primary", 200.0, principal);

        // Then: 余额 = 1000 - 100 - 200 = 700
        assertEquals(new BigDecimal("700.00"), primaryAccount.getAccountBalance());
    }
}