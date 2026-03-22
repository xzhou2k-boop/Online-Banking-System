package com.userfront.testcases;

import com.userfront.pages.LoginPage;
import com.userfront.pages.ProfilePage;
import com.userfront.pages.RegistrationPage;
import com.userfront.pages.UserFrontPage;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 用户注册功能测试类
 * 包含用户注册相关的所有测试用例
 * 用例编号：TC-USER-001 至 TC-USER-006
 */
public class UserRegistrationTest extends BaseTest {

    /**
     * 测试用例：TC-USER-001 - 新用户注册成功
     * 测试步骤：
     * 1. 访问注册页面
     * 2. 填写注册信息（使用唯一用户名避免冲突）
     * 3. 点击注册按钮
     * 预期结果：
     * - 注册成功
     * - 页面跳转到登录页
     */
    @Test(priority = 1, description = "TC-USER-001: 新用户注册成功")
    public void testUserRegistrationSuccess() {
        // 创建测试报告节点
        extentTest = extentReports.createTest("testUserRegistrationSuccess", "新用户注册成功");

        // 访问注册页面
        driver.get(TestConfig.getBaseUrl() + "/signup");
        RegistrationPage registrationPage = new RegistrationPage(driver);

        // 生成唯一用户名（避免与已存在用户冲突）
        String uniqueUsername = "testuser" + System.currentTimeMillis();
        // 填写注册信息并提交
        registrationPage.register("测试", "用户", "13800138004", uniqueUsername + "@bank.com", uniqueUsername, "123456");

        // 等待页面跳转到登录页
        registrationPage.waitForRegistrationSuccess("/index");

        // 验证：注册成功后应跳转到登录页
        Assert.assertTrue(driver.getCurrentUrl().contains("/index"), "注册后应跳转到登录页");
        // 记录测试通过
        extentTest.pass("用户注册成功并跳转到登录页");
    }

    /**
     * 测试用例：TC-USER-002 - 用户名重复注册
     * 测试步骤：
     * 1. 访问注册页面
     * 2. 使用已存在的用户名（user1）进行注册
     * 3. 点击注册按钮
     * 预期结果：
     * - 显示"用户名已存在"错误提示
     */
    @Test(priority = 2, description = "TC-USER-002: 用户名重复注册")
    public void testDuplicateUsernameRegistration() {
        extentTest = extentReports.createTest("testDuplicateUsernameRegistration", "用户名重复注册");

        // 访问注册页面
        driver.get(TestConfig.getBaseUrl() + "/signup");
        RegistrationPage registrationPage = new RegistrationPage(driver);

        // 使用已存在的用户名user1进行注册
        registrationPage.register("测试", "用户", "13800138002", "newemail@163.com", "user1", "test123456");

        // 验证：应显示用户名已存在错误
        Assert.assertTrue(registrationPage.isUsernameExistsErrorDisplayed(), "应显示用户名已存在错误");
        extentTest.pass("用户名重复时显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-003 - 邮箱重复注册
     * 测试步骤：
     * 1. 访问注册页面
     * 2. 使用已存在的邮箱进行注册
     * 3. 点击注册按钮
     * 预期结果：
     * - 显示"邮箱已存在"错误提示
     */
    @Test(priority = 3, description = "TC-USER-003: 邮箱重复注册")
    public void testDuplicateEmailRegistration() {
        extentTest = extentReports.createTest("testDuplicateEmailRegistration", "邮箱重复注册");

        driver.get(TestConfig.getBaseUrl() + "/signup");
        RegistrationPage registrationPage = new RegistrationPage(driver);

        // 使用已存在的邮箱user1@bank.com进行注册
        registrationPage.register("测试", "用户", "13800138003", "user1@bank.com", "newuser" + System.currentTimeMillis(),
                "test123456");

        // 验证：应显示邮箱已存在错误
        Assert.assertTrue(registrationPage.isEmailExistsErrorDisplayed(), "应显示邮箱已存在错误");
        extentTest.pass("邮箱重复时显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-004 - 用户名和邮箱同时重复
     * 测试步骤：
     * 1. 访问注册页面
     * 2. 使用已存在的用户名和邮箱进行注册
     * 3. 点击注册按钮
     * 预期结果：
     * - 同时显示"用户名已存在"和"邮箱已存在"两个错误提示
     */
    @Test(priority = 4, description = "TC-USER-004: 用户名和邮箱同时重复")
    public void testDuplicateUsernameAndEmailRegistration() {
        extentTest = extentReports.createTest("testDuplicateUsernameAndEmailRegistration", "用户名和邮箱同时重复");

        driver.get(TestConfig.getBaseUrl() + "/signup");
        RegistrationPage registrationPage = new RegistrationPage(driver);

        // 同时使用已存在的用户名和邮箱
        registrationPage.register("测试", "用户", "13800138004", "user1@bank.com", "user1", "test123456");

        // 验证：应同时显示两个错误提示
        Assert.assertTrue(registrationPage.isUsernameExistsErrorDisplayed(), "应显示用户名已存在错误");
        Assert.assertTrue(registrationPage.isEmailExistsErrorDisplayed(), "应显示邮箱已存在错误");
        extentTest.pass("用户名和邮箱同时重复时显示两个错误提示");
    }

    /**
     * 测试用例：TC-USER-005 - 必填字段为空
     * 测试步骤：
     * 1. 访问注册页面
     * 2. 不填写任何信息，直接点击注册按钮
     * 预期结果：
     * - 表单验证失败，提示必填字段不能为空
     */
    @Test(priority = 5, description = "TC-USER-005: 必填字段为空")
    public void testRequiredFieldsEmpty() {
        extentTest = extentReports.createTest("testRequiredFieldsEmpty", "必填字段为空");

        driver.get(TestConfig.getBaseUrl() + "/signup");
        RegistrationPage registrationPage = new RegistrationPage(driver);

        // 不填写任何信息，直接点击注册按钮
        registrationPage.clickRegisterButton();

        // 验证：必填字段应有required属性（HTML5表单验证）
        Assert.assertTrue(driver.findElement(org.openqa.selenium.By.xpath("//input[@id='firstName' and @required]"))
                .isDisplayed());
        Assert.assertTrue(driver.findElement(org.openqa.selenium.By.xpath("//input[@id='password' and @required]"))
                .isDisplayed());
        extentTest.pass("必填字段为空时表单验证失败");
    }

    /**
     * 测试用例：TC-USER-006 - 注册后自动创建账户
     * 测试步骤：
     * 1. 注册一个新用户
     * 2. 使用新账号登录系统
     * 3. 进入个人资料页面
     * 4. 查看主账户和储蓄账户信息
     * 预期结果：
     * - 主账户账号已创建且不为空
     * - 储蓄账户账号已创建且不为空
     */
    @Test(priority = 6, description = "TC-USER-006: 注册后自动创建账户")
    public void testAutoCreateAccountsAfterRegistration() {
        extentTest = extentReports.createTest("testAutoCreateAccountsAfterRegistration", "注册后自动创建账户");

        // 生成唯一用户名
        String uniqueUsername = "testuserauto" + System.currentTimeMillis();

        // 1. 访问注册页面并注册新用户
        driver.get(TestConfig.getBaseUrl() + "/signup");
        RegistrationPage registrationPage = new RegistrationPage(driver);
        registrationPage.register("测试", "用户", "13800138006", uniqueUsername + "@163.com", uniqueUsername, "test123456");

        // 2. 使用新注册的账号登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(uniqueUsername, "test123456");

        // 3. 等待首页加载完成
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 4. 进入个人资料页面查看账户信息
        userFrontPage.navigateToProfile();

        // 5. 获取账户信息
        ProfilePage profilePage = new ProfilePage(driver);
        String primaryAccountNumber = profilePage.getPrimaryAccountNumber();
        String savingsAccountNumber = profilePage.getSavingsAccountNumber();

        // 验证：主账户和储蓄账户应自动创建
        Assert.assertNotNull(primaryAccountNumber, "主账户应自动创建");
        Assert.assertTrue(primaryAccountNumber.length() > 0, "主账户账号不应为空");
        Assert.assertNotNull(savingsAccountNumber, "储蓄账户应自动创建");
        Assert.assertTrue(savingsAccountNumber.length() > 0, "储蓄账户账号不应为空");
        extentTest.pass("注册后自动创建主账户和储蓄账户");
    }
}
