package com.userfront.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.userfront.dao.RoleDao;
import com.userfront.dao.UserDao;
import com.userfront.domain.User;
import com.userfront.domain.security.Role;
import com.userfront.domain.security.UserRole;
import com.userfront.service.UserServiceImpl.UserServiceImpl;
import com.userfront.service.AccountService;

/**
 * UserService单元测试类
 *
 * 测试目标：UserServiceImpl 业务逻辑层的核心功能
 *
 * 测试策略：
 * 1. 使用Mockito框架模拟DAO层依赖，避免真实数据库操作
 * 2. 使用@InjectMocks注入被测试的Service实现类
 * 3. 使用Given-When-Then模式组织测试代码，提高可读性
 * 4. 每个测试方法专注于测试一个业务场景
 *
 * 覆盖的测试场景：
 * - 用户查询（按用户名/邮箱）
 * - 用户存在性校验（用户名/邮箱重复检查）
 * - 用户创建（新建用户、用户已存在场景）
 * - 用户状态管理（启用/禁用）
 * - 密码更新
 * - 用户列表查询
 */
@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    /**
     * 模拟UserDao数据访问层
     * 用于验证Service层对数据库的操作是否正确
     */
    @Mock
    private UserDao userDao;

    /**
     * 模拟RoleDao角色数据访问层
     * 用于验证用户角色关联关系的保存
     */
    @Mock
    private RoleDao roleDao;

    /**
     * 模拟BCryptPasswordEncoder密码加密器
     * 避免真实加密操作，确保测试确定性
     */
    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    /**
     * 模拟AccountService账户服务
     * 用于验证创建用户时自动创建账户的逻辑
     */
    @Mock
    private AccountService accountService;

    /**
     * 被测试的Service实现类
     * Mockito会自动将上述Mock对象注入到此实例中
     */
    @InjectMocks
    private UserServiceImpl userService;

    /**
     * 测试用户数据
     * 在每个测试方法执行前初始化，确保测试隔离性
     */
    private User testUser;

    /**
     * 测试前置准备
     * 创建通用的测试用户数据，供各测试方法使用
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setEnabled(true);
    }

    // ==================== 用户查询功能测试 ====================

    /**
     * 测试findByUsername方法 - 用户名查询成功
     *
     * 测试场景：当用户名存在于数据库时，应返回对应的User对象
     *
     * 测试步骤：
     * 1. 准备测试数据：设置mock返回testUser
     * 2. 执行测试：调用findByUsername方法
     * 3. 验证结果：确认返回非空且用户名匹配
     *
     * 验证要点：DAO层方法被正确调用，返回值正确传递
     */
    @Test
    @DisplayName("测试findByUsername - 成功找到用户")
    void testFindByUsername_Success() {
        // Given: 准备测试数据 - 模拟数据库返回用户
        String username = "testuser";
        when(userDao.findByUsername(username)).thenReturn(testUser);

        // When: 执行被测试方法
        User result = userService.findByUsername(username);

        // Then: 验证返回结果
        assertNotNull(result, "查询结果不应为空");
        assertEquals(username, result.getUsername(), "用户名应该匹配");
        verify(userDao, times(1)).findByUsername(username);
    }

    /**
     * 测试findByUsername方法 - 用户不存在
     *
     * 测试场景：当用户名不存在时，应返回null
     *
     * 验证要点：确认null被正确传递，未触发异常
     */
    @Test
    @DisplayName("测试findByUsername - 用户不存在返回null")
    void testFindByUsername_NotFound() {
        // Given: 模拟数据库无此用户
        String username = "nonexistent";
        when(userDao.findByUsername(username)).thenReturn(null);

        // When: 执行查询
        User result = userService.findByUsername(username);

        // Then: 验证返回null
        assertNull(result, "不存在的用户应返回null");
        verify(userDao, times(1)).findByUsername(username);
    }

    /**
     * 测试findByEmail方法 - 邮箱查询成功
     *
     * 测试场景：根据邮箱查询用户，返回匹配的User对象
     */
    @Test
    @DisplayName("测试findByEmail - 成功找到用户")
    void testFindByEmail_Success() {
        // Given
        String email = "test@example.com";
        when(userDao.findByEmail(email)).thenReturn(testUser);

        // When
        User result = userService.findByEmail(email);

        // Then
        assertNotNull(result, "邮箱查询结果不应为空");
        assertEquals(email, result.getEmail(), "邮箱应该匹配");
    }

    /**
     * 测试findByEmail方法 - 邮箱不存在
     */
    @Test
    @DisplayName("测试findByEmail - 邮箱不存在返回null")
    void testFindByEmail_NotFound() {
        // Given
        String email = "nonexistent@example.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // When
        User result = userService.findByEmail(email);

        // Then
        assertNull(result, "不存在的邮箱应返回null");
    }

    // ==================== 用户存在性检查测试 ====================

    /**
     * 测试checkUsernameExists方法 - 用户名已存在
     *
     * 测试场景：用户名在数据库中存在，返回true
     *
     * 业务意义：用于注册时的重复用户名检查
     */
    @Test
    @DisplayName("测试checkUsernameExists - 用户名已存在")
    void testCheckUsernameExists_True() {
        // Given: 用户名已存在
        String username = "testuser";
        when(userDao.findByUsername(username)).thenReturn(testUser);

        // When: 检查用户名是否存在
        boolean result = userService.checkUsernameExists(username);

        // Then: 验证返回true
        assertTrue(result, "已存在的用户名应返回true");
    }

    /**
     * 测试checkUsernameExists方法 - 用户名不存在
     */
    @Test
    @DisplayName("测试checkUsernameExists - 用户名不存在")
    void testCheckUsernameExists_False() {
        // Given: 用户名不存在
        String username = "newuser";
        when(userDao.findByUsername(username)).thenReturn(null);

        // When
        boolean result = userService.checkUsernameExists(username);

        // Then
        assertFalse(result, "不存在的用户名应返回false");
    }

    /**
     * 测试checkEmailExists方法 - 邮箱已存在
     *
     * 业务意义：用于注册时的重复邮箱检查
     */
    @Test
    @DisplayName("测试checkEmailExists - 邮箱已存在")
    void testCheckEmailExists_True() {
        // Given: 邮箱已存在
        String email = "test@example.com";
        when(userDao.findByEmail(email)).thenReturn(testUser);

        // When
        boolean result = userService.checkEmailExists(email);

        // Then
        assertTrue(result, "已存在的邮箱应返回true");
    }

    /**
     * 测试checkEmailExists方法 - 邮箱不存在
     */
    @Test
    @DisplayName("测试checkEmailExists - 邮箱不存在")
    void testCheckEmailExists_False() {
        // Given: 邮箱不存在
        String email = "new@example.com";
        when(userDao.findByEmail(email)).thenReturn(null);

        // When
        boolean result = userService.checkEmailExists(email);

        // Then
        assertFalse(result, "不存在的邮箱应返回false");
    }

    /**
     * 测试checkUserExists方法 - 用户名或邮箱已存在
     *
     * 测试场景：用户名或邮箱任一存在即返回true
     *
     * 业务意义：综合检查用户是否已注册
     */
    @Test
    @DisplayName("测试checkUserExists - 用户名或邮箱已存在")
    void testCheckUserExists_True() {
        // Given: 用户名存在
        String username = "testuser";
        String email = "test@example.com";
        when(userDao.findByUsername(username)).thenReturn(testUser);

        // When: 综合检查
        boolean result = userService.checkUserExists(username, email);

        // Then
        assertTrue(result, "用户名或邮箱存在应返回true");
    }

    /**
     * 测试checkUserExists方法 - 用户名和邮箱都不存在
     */
    @Test
    @DisplayName("测试checkUserExists - 用户名和邮箱都不存在")
    void testCheckUserExists_False() {
        // Given: 都不存在
        String username = "newuser";
        String email = "new@example.com";
        when(userDao.findByUsername(username)).thenReturn(null);
        when(userDao.findByEmail(email)).thenReturn(null);

        // When
        boolean result = userService.checkUserExists(username, email);

        // Then
        assertFalse(result, "都不存在应返回false");
    }

    /**
     * 测试chechUserIsEnabled方法 - 用户已启用
     *
     * 业务意义：检查用户账号是否可登录
     */
    @Test
    @DisplayName("测试chechUserIsEnabled - 用户已启用")
    void testCheckUserIsEnabled_True() {
        // Given: 用户已启用
        testUser.setEnabled(true);
        when(userDao.findByUsername(anyString())).thenReturn(testUser);

        // When
        boolean result = userService.chechUserIsEnabled("testuser");

        // Then
        assertTrue(result, "已启用用户应返回true");
    }

    /**
     * 测试chechUserIsEnabled方法 - 用户未启用
     */
    @Test
    @DisplayName("测试chechUserIsEnabled - 用户未启用")
    void testCheckUserIsEnabled_False() {
        // Given: 用户未启用
        testUser.setEnabled(false);
        when(userDao.findByUsername(anyString())).thenReturn(testUser);

        // When
        boolean result = userService.chechUserIsEnabled("testuser");

        // Then
        assertFalse(result, "未启用用户应返回false");
    }

    // ==================== 用户保存功能测试 ====================

    /**
     * 测试save方法 - 保存新用户
     *
     * 业务意义：验证用户数据正确持久化到数据库
     */
    @Test
    @DisplayName("测试save - 保存用户")
    void testSave() {
        // Given
        User user = new User();
        user.setUsername("newuser");
        user.setPassword("password");

        // When: 执行保存
        userService.save(user);

        // Then: 验证保存操作被调用
        verify(userDao, times(1)).save(user);
    }

    /**
     * 测试saveUser方法 - 更新用户信息
     *
     * 业务意义：用户资料编辑后的保存功能
     */
    @Test
    @DisplayName("测试saveUser - 更新用户")
    void testSaveUser() {
        // Given
        when(userDao.save(any(User.class))).thenReturn(testUser);

        // When
        User result = userService.saveUser(testUser);

        // Then
        assertNotNull(result, "更新后应返回用户对象");
        verify(userDao, times(1)).save(testUser);
    }

    // ==================== 用户创建功能测试 ====================

    /**
     * 测试createUser方法 - 新用户创建成功
     *
     * 测试场景：新用户注册，系统自动完成以下操作：
     * 1. 密码加密
     * 2. 角色保存
     * 3. 创建主账户和储蓄账户
     * 4. 保存用户信息
     *
     * 验证要点：完整的用户创建流程
     */
    @Test
    @DisplayName("测试createUser - 新用户创建成功")
    void testCreateUser_Success() {
        // Given: 准备新用户数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password");
        newUser.setEmail("new@example.com");

        // 准备角色数据
        Role role = new Role();
        role.setName("ROLE_USER");

        Set<UserRole> userRoles = new HashSet<>();
        UserRole userRole = new UserRole(newUser, role);
        userRoles.add(userRole);

        // 模拟各层返回
        when(userDao.findByUsername("newuser")).thenReturn(null); // 用户不存在
        when(passwordEncoder.encode("password")).thenReturn("encodedPassword"); // 密码加密
        when(roleDao.save(any(Role.class))).thenReturn(role); // 角色保存
        when(accountService.createPrimaryAccount()).thenReturn(new com.userfront.domain.PrimaryAccount());
        when(accountService.createSavingsAccount()).thenReturn(new com.userfront.domain.SavingsAccount());
        when(userDao.save(any(User.class))).thenReturn(newUser);

        // When: 执行创建
        User result = userService.createUser(newUser, userRoles);

        // Then: 验证结果
        assertNotNull(result, "创建成功应返回用户对象");
        assertEquals("encodedPassword", newUser.getPassword(), "密码应该被加密");
        verify(userDao, times(1)).save(newUser);
    }

    /**
     * 测试createUser方法 - 用户已存在不创建
     *
     * 测试场景：当用户名已存在时，不执行创建操作，直接返回现有用户
     *
     * 业务意义：防止重复注册
     */
    @Test
    @DisplayName("测试createUser - 用户已存在不创建")
    void testCreateUser_UserAlreadyExists() {
        // Given: 用户已存在
        User existingUser = new User();
        existingUser.setUsername("testuser");

        Set<UserRole> userRoles = new HashSet<>();
        when(userDao.findByUsername("testuser")).thenReturn(existingUser);

        // When: 尝试创建
        User result = userService.createUser(existingUser, userRoles);

        // Then: 验证直接返回现有用户，未执行保存
        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userDao, never()).save(any(User.class)); // 确认未保存
    }

    // ==================== 用户状态管理测试 ====================

    /**
     * 测试enableUser方法 - 启用用户账号
     *
     * 业务意义：管理员激活用户账号
     */
    @Test
    @DisplayName("测试enableUser - 启用用户")
    void testEnableUser() {
        // Given
        when(userDao.findByUsername("testuser")).thenReturn(testUser);

        // When: 执行启用
        userService.enableUser("testuser");

        // Then: 验证状态变更
        assertTrue(testUser.isEnabled(), "用户应该被启用");
        verify(userDao, times(1)).save(testUser);
    }

    /**
     * 测试disableUser方法 - 禁用用户账号
     *
     * 业务意义：管理员禁用用户账号
     */
    @Test
    @DisplayName("测试disableUser - 禁用用户")
    void testDisableUser() {
        // Given
        when(userDao.findByUsername("testuser")).thenReturn(testUser);

        // When: 执行禁用
        userService.disableUser("testuser");

        // Then: 验证状态变更
        assertFalse(testUser.isEnabled(), "用户应该被禁用");
        verify(userDao, times(1)).save(testUser);
    }

    /**
     * 测试updatePassword方法 - 更新用户密码
     *
     * 业务意义：用户修改密码功能
     *
     * 验证要点：新密码被正确加密后保存
     */
    @Test
    @DisplayName("测试updatePassword - 更新密码")
    void testUpdatePassword() {
        // Given
        String newPassword = "newpassword123";
        when(userDao.findByUsername("testuser")).thenReturn(testUser);
        when(passwordEncoder.encode(newPassword)).thenReturn("encodedNewPassword");

        // When: 执行密码更新
        userService.updatePassword("testuser", newPassword);

        // Then: 验证密码加密和保存
        assertEquals("encodedNewPassword", testUser.getPassword(), "密码应该被加密");
        verify(userDao, times(1)).save(testUser);
    }

    // ==================== 用户列表查询测试 ====================

    /**
     * 测试findUserList方法 - 获取所有用户列表
     *
     * 业务意义：管理员查看所有用户
     */
    @Test
    @DisplayName("测试findUserList - 获取所有用户")
    void testFindUserList() {
        // Given: 模拟返回用户列表
        when(userDao.findAll()).thenReturn(Arrays.asList(testUser));

        // When: 获取列表
        List<User> result = userService.findUserList();

        // Then: 验证结果
        assertNotNull(result, "列表不应为空");
        assertEquals(1, result.size(), "应返回1个用户");
    }
}