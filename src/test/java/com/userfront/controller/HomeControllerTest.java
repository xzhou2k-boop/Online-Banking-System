package com.userfront.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.HashSet;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;

import com.userfront.dao.RoleDao;
import com.userfront.domain.PrimaryAccount;
import com.userfront.domain.SavingsAccount;
import com.userfront.domain.User;
import com.userfront.domain.security.Role;
import com.userfront.domain.security.UserRole;
import com.userfront.service.UserService;

/**
 * HomeController单元测试类
 *
 * 测试目标：HomeController 控制器层
 *
 * 测试策略：
 * 1. 使用Mockito模拟Service层依赖
 * 2. 直接调用Controller方法，验证返回值和Model属性
 * 3. 不启动真实Spring MVC容器，保持测试轻量
 *
 * 覆盖的测试场景：
 * - 根路径重定向
 * - 首页访问
 * - 用户注册页面（GET/POST）
 * - 用户主页访问
 */
@ExtendWith(MockitoExtension.class)
class HomeControllerTest {

    /**
     * 模拟UserService业务服务
     * 用于验证用户注册、查询等业务逻辑
     */
    @Mock
    private UserService userService;

    /**
     * 模拟RoleDao角色数据访问
     * 用于获取用户角色
     */
    @Mock
    private RoleDao roleDao;

    /**
     * 模拟Spring MVC的Model
     * 用于验证Controller向视图传递的数据
     */
    @Mock
    private Model model;

    /**
     * 模拟安全主体
     * 代表已登录用户
     */
    @Mock
    private Principal principal;

    /**
     * 模拟绑定结果
     * 用于表单验证结果（当前未使用但保留）
     */
    @Mock
    private BindingResult bindingResult;

    /**
     * 被测试的Controller
     */
    @InjectMocks
    private HomeController homeController;

    /**
     * 测试用户数据
     */
    private User testUser;

    /**
     * 测试用角色
     */
    private Role userRole;

    /**
     * 测试前置准备
     * 初始化测试用户及其账户信息
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");

        PrimaryAccount primaryAccount = new PrimaryAccount();
        primaryAccount.setAccountNumber(123456);
        primaryAccount.setAccountBalance(new BigDecimal("1000.00"));
        testUser.setPrimaryAccount(primaryAccount);

        SavingsAccount savingsAccount = new SavingsAccount();
        savingsAccount.setAccountNumber(789012);
        savingsAccount.setAccountBalance(new BigDecimal("2000.00"));
        testUser.setSavingsAccount(savingsAccount);

        userRole = new Role();
        userRole.setName("ROLE_USER");
    }

    // ==================== 页面路由测试 ====================

    /**
     * 测试home方法 - 根路径重定向
     *
     * 测试场景：访问根路径 "/" 时重定向到登录页面
     *
     * 验证要点：返回正确的重定向路径
     */
    @Test
    @DisplayName("测试home - 根路径重定向到index")
    void testHome() {
        // When: 访问根路径
        String result = homeController.home();

        // Then: 验证重定向到index
        assertEquals("redirect:/index", result);
    }

    /**
     * 测试index方法 - 首页访问
     *
     * 测试场景：访问登录页面
     *
     * 验证要点：返回index视图名
     */
    @Test
    @DisplayName("测试index - 返回index视图")
    void testIndex() {
        // When
        String result = homeController.index();

        // Then
        assertEquals("index", result);
    }

    // ==================== 用户注册功能测试 ====================

    /**
     * 测试signup (GET) - 注册页面访问
     *
     * 测试场景：用户点击注册按钮，进入注册页面
     *
     * 验证要点：
     * - 返回signup视图
     - Model中添加空的User对象供表单绑定
     */
    @Test
    @DisplayName("测试signupGet - 返回signup视图并添加空用户")
    void testSignupGet() {
        // When: 访问注册页面
        String result = homeController.signup(model);

        // Then: 验证返回signup视图并添加user属性
        assertEquals("signup", result);
        verify(model).addAttribute(eq("user"), any(User.class));
    }

    /**
     * 测试signup (POST) - 用户已存在
     *
     * 测试场景：用户提交注册表单，但用户名和邮箱都已存在
     *
     * 业务逻辑：
     * 1. 检查用户是否已存在
     * 2. 如存在，返回注册页面并显示错误信息
     * 3. 不执行用户创建
     */
    @Test
    @DisplayName("测试signupPost - 用户已存在返回signup视图")
    void testSignupPost_UserExists() {
        // Given: 模拟用户已存在
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("test@example.com");

        when(userService.checkUserExists("testuser", "test@example.com")).thenReturn(true);
        when(userService.checkEmailExists("test@example.com")).thenReturn(true);
        when(userService.checkUsernameExists("testuser")).thenReturn(true);

        // When: 提交注册
        String result = homeController.signupPost(newUser, model);

        // Then: 验证返回signup并添加错误属性
        assertEquals("signup", result);
        verify(model).addAttribute("emailExists", true);
        verify(model).addAttribute("usernameExists", true);
        verify(userService, never()).createUser(any(), any());
    }

    /**
     * 测试signup (POST) - 用户名已存在
     *
     * 测试场景：用户名重复但邮箱可用
     */
    @Test
    @DisplayName("测试signupPost - 用户名已存在")
    void testSignupPost_UsernameExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("testuser");
        newUser.setEmail("new@example.com");

        when(userService.checkUserExists("testuser", "new@example.com")).thenReturn(true);
        when(userService.checkUsernameExists("testuser")).thenReturn(true);
        when(userService.checkEmailExists("new@example.com")).thenReturn(false);

        // When
        String result = homeController.signupPost(newUser, model);

        // Then
        assertEquals("signup", result);
        verify(model).addAttribute("usernameExists", true);
    }

    /**
     * 测试signup (POST) - 邮箱已存在
     *
     * 测试场景：用户名可用但邮箱重复
     */
    @Test
    @DisplayName("测试signupPost - 邮箱已存在")
    void testSignupPost_EmailExists() {
        // Given
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setEmail("test@example.com");

        when(userService.checkUserExists("newuser", "test@example.com")).thenReturn(true);
        when(userService.checkUsernameExists("newuser")).thenReturn(false);
        when(userService.checkEmailExists("test@example.com")).thenReturn(true);

        // When
        String result = homeController.signupPost(newUser, model);

        // Then
        assertEquals("signup", result);
        verify(model).addAttribute("emailExists", true);
    }

    /**
     * 测试signup (POST) - 注册成功
     *
     * 测试场景：新用户注册成功
     *
     * 业务逻辑：
     * 1. 检查用户不存在
     * 2. 分配默认角色 ROLE_USER
     * 3. 创建用户
     * 4. 重定向到首页
     */
    @Test
    @DisplayName("测试signupPost - 新用户创建成功")
    void testSignupPost_Success() {
        // Given: 新用户数据
        User newUser = new User();
        newUser.setUsername("newuser");
        newUser.setPassword("password123");
        newUser.setEmail("new@example.com");
        newUser.setFirstName("New");
        newUser.setLastName("User");

        when(userService.checkUserExists("newuser", "new@example.com")).thenReturn(false);
        when(roleDao.findByName("ROLE_USER")).thenReturn(userRole);

        // When: 提交注册
        String result = homeController.signupPost(newUser, model);

        // Then: 验证重定向并调用创建服务
        assertEquals("redirect:/", result);
        verify(userService).createUser(eq(newUser), any(Set.class));
    }

    // ==================== 用户主页测试 ====================

    /**
     * 测试userFront - 成功加载用户主页
     *
     * 测试场景：已登录用户访问个人主页
     *
     * 业务逻辑：
     * 1. 获取当前登录用户
     * 2. 查询用户的主账户和储蓄账户
     * 3. 将账户信息传递给视图
     */
    @Test
    @DisplayName("测试userFront - 返回用户主页并加载账户信息")
    void testUserFront() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 访问用户主页
        String result = homeController.userFront(principal, model);

        // Then: 验证返回视图并传递账户信息
        assertEquals("userFront", result);
        verify(model).addAttribute("primaryAccount", testUser.getPrimaryAccount());
        verify(model).addAttribute("savingsAccount", testUser.getSavingsAccount());
    }

    /**
     * 测试userFront - 用户无账户
     *
     * 测试场景：新用户尚未创建账户
     */
    @Test
    @DisplayName("测试userFront - 用户无账户信息")
    void testUserFront_NoAccounts() {
        // Given: 无账户的用户
        User userWithoutAccounts = new User();
        userWithoutAccounts.setUsername("testuser");

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(userWithoutAccounts);

        // When
        String result = homeController.userFront(principal, model);

        // Then: 仍返回userFront视图，账户为null
        assertEquals("userFront", result);
        verify(model).addAttribute("primaryAccount", null);
        verify(model).addAttribute("savingsAccount", null);
    }
}