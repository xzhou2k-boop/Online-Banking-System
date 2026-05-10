package com.userfront.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

import com.userfront.domain.User;
import com.userfront.service.UserService;

/**
 * UserController单元测试类
 *
 * 测试目标：UserController 用户个人资料控制器
 *
 * 测试策略：
 * 1. 模拟UserService验证业务逻辑调用
 * 2. 验证Model中传递的数据
 * 3. 测试各种异常场景（邮箱重复、用户名重复、密码错误等）
 *
 * 覆盖的测试场景：
 * - 个人资料页面访问（GET）
 * - 个人资料更新（POST）- 成功/邮箱重复/用户名重复
 * - 密码修改 - 成功/密码不匹配/密码太短
 * - 用户名变更
 */
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class UserControllerTest {

    /**
     * 模拟UserService用户服务
     * 验证用户资料和密码操作
     */
    @Mock
    private UserService userService;

    /**
     * 模拟Model
     * 验证视图数据传递
     */
    @Mock
    private Model model;

    /**
     * 模拟Principal
     * 代表当前登录用户
     */
    @Mock
    private Principal principal;

    /**
     * 被测试的Controller
     */
    @InjectMocks
    private UserController userController;

    /**
     * 测试用户数据
     */
    private User testUser;

    /**
     * 测试前置准备
     */
    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setUsername("testuser");
        testUser.setPassword("password123");
        testUser.setEmail("test@example.com");
        testUser.setFirstName("Test");
        testUser.setLastName("User");
        testUser.setPhone("1234567890");
    }

    // ==================== 个人资料页面测试 ====================

    /**
     * 测试profile (GET) - 获取用户资料页
     *
     * 测试场景：用户访问个人资料页面
     *
     * 业务逻辑：
     * 1. 获取当前登录用户
     * 2. 查询用户详细信息
     * 3. 将用户信息传递给视图
     */
    @Test
    @DisplayName("测试profileGet - 获取用户资料页")
    void testProfileGet() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 访问资料页
        String result = userController.profile(principal, model);

        // Then: 验证返回profile视图并传递用户信息
        assertEquals("profile", result);
        verify(model).addAttribute("user", testUser);
    }

    // ==================== 个人资料更新测试 ====================

    /**
     * 测试profilePost - 更新资料成功
     *
     * 测试场景：用户更新个人资料（邮箱未变更）
     *
     * 业务逻辑：
     * 1. 验证邮箱是否与其他用户重复
     * 2. 验证用户名是否与其他用户重复
     * 3. 更新用户信息
     * 4. 返回成功消息
     */
    @Test
    @DisplayName("测试profilePost - 更新资料成功")
    void testProfilePost_Success() {
        // Given: 更新的资料
        User updatedUser = new User();
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");
        updatedUser.setPhone("0987654321");

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByEmail("test@example.com")).thenReturn(testUser);

        // When: 提交更新
        String result = userController.profilePost(updatedUser, model, principal);

        // Then: 验证成功保存
        assertEquals("profile", result);
        verify(userService).saveUser(testUser);
        verify(model).addAttribute("user", testUser);
        verify(model).addAttribute("profileSuccess", "个人资料更新成功");
    }

    /**
     * 测试profilePost - 邮箱已被其他用户使用
     *
     * 测试场景：用户更新的邮箱已被他人使用
     *
     * 验证要点：返回错误提示，不执行保存
     */
    @Test
    @DisplayName("测试profilePost - 邮箱已被其他用户使用")
    void testProfilePost_EmailExists() {
        // Given: 邮箱被他人使用
        User existingUser = new User();
        existingUser.setUsername("otheruser");
        existingUser.setEmail("newemail@example.com");

        User updatedUser = new User();
        updatedUser.setUsername("testuser");
        updatedUser.setEmail("newemail@example.com");

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByEmail("newemail@example.com")).thenReturn(existingUser);

        // When
        String result = userController.profilePost(updatedUser, model, principal);

        // Then: 验证错误提示
        assertEquals("profile", result);
        verify(model).addAttribute("emailExists", true);
        verify(model).addAttribute("user", testUser);
    }

    /**
     * 测试profilePost - 用户名已被其他用户使用
     */
    @Test
    @DisplayName("测试profilePost - 用户名已被其他用户使用")
    void testProfilePost_UsernameExists() {
        // Given: 用户名被他人使用
        User existingUser = new User();
        existingUser.setUsername("existinguser");

        User updatedUser = new User();
        updatedUser.setUsername("existinguser");
        updatedUser.setEmail("test@example.com");

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(userService.findByUsername("existinguser")).thenReturn(existingUser);

        // When
        String result = userController.profilePost(updatedUser, model, principal);

        // Then
        assertEquals("profile", result);
        verify(model).addAttribute("usernameExists", true);
        verify(model).addAttribute("user", testUser);
    }

    /**
     * 测试profilePost - 用户名变更
     *
     * 测试场景：用户更改用户名（新用户名可用）
     */
    @Test
    @DisplayName("测试profilePost - 用户名变更")
    void testProfilePost_UsernameChanged() {
        // Given: 变更用户名
        User updatedUser = new User();
        updatedUser.setUsername("newusername");
        updatedUser.setEmail("test@example.com");
        updatedUser.setFirstName("Updated");
        updatedUser.setLastName("Name");

        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByEmail("test@example.com")).thenReturn(testUser);
        when(userService.findByUsername("newusername")).thenReturn(null);

        // When
        String result = userController.profilePost(updatedUser, model, principal);

        // Then: 验证用户名更新
        assertEquals("profile", result);
        assertEquals("newusername", testUser.getUsername());
    }

    // ==================== 密码修改测试 ====================

    /**
     * 测试updatePassword - 密码不匹配
     *
     * 测试场景：两次输入的密码不一致
     *
     * 验证要点：返回错误提示，不调用密码更新服务
     */
    @Test
    @DisplayName("测试updatePassword - 密码不匹配")
    void testUpdatePassword_PasswordMismatch() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 密码不匹配
        String result = userController.updatePassword(principal, "password1", "password2", model);

        // Then: 验证错误提示
        assertEquals("profile", result);
        verify(model).addAttribute("passwordError", "两次输入的密码不一致");
        verify(model).addAttribute("user", testUser);
    }

    /**
     * 测试updatePassword - 密码太短
     *
     * 测试场景：密码长度少于6位
     *
     * 验证要点：密码最小长度验证
     */
    @Test
    @DisplayName("测试updatePassword - 密码太短")
    void testUpdatePassword_PasswordTooShort() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 密码5位
        String result = userController.updatePassword(principal, "12345", "12345", model);

        // Then
        assertEquals("profile", result);
        verify(model).addAttribute("passwordError", "密码长度不能少于6位");
    }

    /**
     * 测试updatePassword - 密码为空
     */
    @Test
    @DisplayName("测试updatePassword - 密码为空")
    void testUpdatePassword_PasswordEmpty() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When
        String result = userController.updatePassword(principal, "", "", model);

        // Then
        assertEquals("profile", result);
        verify(model).addAttribute("passwordError", "密码长度不能少于6位");
    }

    /**
     * 测试updatePassword - 更新成功
     *
     * 测试场景：密码验证通过，更新成功
     */
    @Test
    @DisplayName("测试updatePassword - 更新成功")
    void testUpdatePassword_Success() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 新密码符合要求
        String result = userController.updatePassword(principal, "newpassword123", "newpassword123", model);

        // Then: 验证密码更新
        assertEquals("profile", result);
        verify(userService).updatePassword("testuser", "newpassword123");
        verify(model).addAttribute("passwordSuccess", "密码修改成功");
    }

    /**
     * 测试updatePassword - 最小长度边界测试
     *
     * 验证要点：6位密码应为有效
     */
    @Test
    @DisplayName("测试updatePassword - 最小长度边界测试")
    void testUpdatePassword_MinLength() {
        // Given
        when(principal.getName()).thenReturn("testuser");
        when(userService.findByUsername("testuser")).thenReturn(testUser);
        when(userService.findByUsername("testuser")).thenReturn(testUser);

        // When: 恰好6位
        String result = userController.updatePassword(principal, "123456", "123456", model);

        // Then
        assertEquals("profile", result);
        verify(userService).updatePassword("testuser", "123456");
    }
}