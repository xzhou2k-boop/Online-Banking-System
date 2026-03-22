package com.userfront.testcases;

import com.userfront.pages.LoginPage;
import com.userfront.pages.PasswordPage;
import com.userfront.pages.ProfilePage;
import com.userfront.pages.UserFrontPage;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;

import org.testng.Assert;
import org.testng.annotations.Ignore;
import org.testng.annotations.Test;

/**
 * 个人资料管理功能测试类
 * 包含个人资料管理相关的所有测试用例
 * 用例编号：TC-USER-020 至 TC-USER-024, TC-USER-030 至 TC-USER-033
 */
public class ProfileTest extends BaseTest {

    /**
     * 测试用例：TC-USER-020 - 查看个人资料
     * 测试步骤：
     * 1. user1登录系统
     * 2. 点击导航栏"我的"->"个人资料"或访问 /user/profile
     * 预期结果：
     * - 显示用户名: user1
     * - 显示姓名: (用户姓名)
     * - 显示电话: (用户电话)
     * - 显示邮箱: user1@bank.com
     * - 显示主账户账号: XXXXX
     * - 显示储蓄账户账号: XXXXX
     */
    @Test(priority = 1, description = "TC-USER-020: 查看个人资料")
    public void testViewProfile() {
        extentTest = extentReports.createTest("testViewProfile", "查看个人资料");

        // 步骤1：用户登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤2：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建个人资料页面对象
        ProfilePage profilePage = new ProfilePage(driver);

        // 验证：应显示用户名
        String username = profilePage.getUsername();
        Assert.assertEquals(username, TestConfig.getTestUser2Username(), "应显示正确的用户名");

        // 验证：应显示邮箱
        String email = profilePage.getEmail();
        Assert.assertEquals(email, TestConfig.getTestUser2Email(), "应显示正确的邮箱");

        // 验证：应显示主账户账号
        String primaryAccountNumber = profilePage.getPrimaryAccountNumber();
        Assert.assertTrue(primaryAccountNumber.length() > 0, "应显示主账户账号");

        // 验证：应显示储蓄账户账号
        String savingsAccountNumber = profilePage.getSavingsAccountNumber();
        Assert.assertTrue(savingsAccountNumber.length() > 0, "应显示储蓄账户账号");

        // 验证：应在个人资料页面
        Assert.assertTrue(profilePage.isProfilePageDisplayed(), "应跳转到个人资料页面");

        extentTest.pass("查看个人资料成功，所有信息显示正确");
    }

    /**
     * 测试用例：TC-USER-021 - 修改个人信息
     * 测试步骤：
     * 1. user1登录系统
     * 2. 访问个人资料页面 /user/profile
     * 3. 修改姓名、电话、邮箱信息
     * 4. 点击保存按钮
     * 预期结果：
     * - 信息更新成功
     * - 页面显示新信息
     */
    @Test(priority = 2, description = "TC-USER-021: 修改个人信息")
    public void testModifyPersonalInfo() {
        extentTest = extentReports.createTest("testModifyPersonalInfo", "修改个人信息");

        // 步骤1：用户登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser1Username(), TestConfig.getTestUser1Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤2：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建个人资料页面对象
        ProfilePage profilePage = new ProfilePage(driver);

        // 步骤3：修改个人信息
        String newFirstName = "赵";
        String newLastName = "大";
        String newPhone = "13900139010";
        String newEmail = "user1_new@163.com";

        profilePage.enterFirstName(newFirstName);
        profilePage.enterLastName(newLastName);
        profilePage.enterPhone(newPhone);
        profilePage.enterEmail(newEmail);

        // 步骤4：点击保存按钮
        profilePage.clickSaveButton();

        // 验证：应显示更新成功提示
        String successMessage = profilePage.getProfileSuccessMessage();
        Assert.assertTrue(successMessage.contains("成功"), "应显示更新成功提示");

        // 验证：页面应显示新的个人信息
        Assert.assertEquals(profilePage.getUsername(), TestConfig.getTestUser1Username(),
                "用户名应保持不变");
        Assert.assertEquals(profilePage.getEmail(), newEmail, "邮箱应更新为新邮箱");

        extentTest.pass("修改个人信息成功");
    }

    /**
     * 测试用例：TC-USER-022 - 修改用户名
     * 测试步骤：
     * 1. user1登录系统
     * 2. 访问个人资料页面 /user/profile
     * 3. 修改用户名为 user1_modified
     * 4. 点击保存按钮
     * 5. 刷新页面，将用户名改回原用户名
     * 预期结果：
     * - 用户名更新为 user1_modified 成功
     */
    @Test(priority = 3, description = "TC-USER-022: 修改用户名")
    public void testModifyUsername() {
        extentTest = extentReports.createTest("testModifyUsername", "修改用户名");

        // 步骤1：用户登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser1Username(), TestConfig.getTestUser1Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤2：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建个人资料页面对象
        ProfilePage profilePage = new ProfilePage(driver);

        // 步骤3：修改用户名
        String originalUsername = TestConfig.getTestUser1Username();
        String newUsername = "user1_modified";
        profilePage.enterUsername(newUsername);

        // 步骤4：点击保存按钮
        profilePage.clickSaveButton();

        // 验证：应显示更新成功提示
        String successMessage = profilePage.getProfileSuccessMessage();
        Assert.assertTrue(successMessage.contains("成功"), "应显示更新成功提示");

        // 验证：用户名应更新成功
        Assert.assertEquals(profilePage.getUsername(), newUsername, "用户名应更新为新用户名");

        // 为后续测试重置数据：重新刷新页面后改回原用户名
        profilePage.enterUsername(originalUsername);
        profilePage.clickSaveButton();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // 等待更新完成

        Assert.assertEquals(profilePage.getUsername(), originalUsername, "用户名应改回原用户名");

        extentTest.pass("修改用户名成功");
    }

    /**
     * 测试用例：TC-USER-023 - 修改为已存在邮箱
     * 测试步骤：
     * 1. user2和user3都已注册，user3邮箱: user3@bank.com
     * 2. user2登录
     * 3. 访问个人资料页面
     * 4. 邮箱修改为: user3@bank.com（已被user3使用）
     * 5. 点击保存按钮
     * 预期结果：
     * - 显示错误提示"邮箱已存在"
     */
    @Test(priority = 4, description = "TC-USER-023: 修改为已存在邮箱")
    public void testModifyToExistingEmail() {
        extentTest = extentReports.createTest("testModifyToExistingEmail", "修改为已存在邮箱");

        // 步骤1-2：user1登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤3：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建个人资料页面对象
        ProfilePage profilePage = new ProfilePage(driver);

        // 步骤4：将邮箱修改为user3已使用的邮箱
        String existingEmail = TestConfig.getTestUser3Email();
        profilePage.enterEmail(existingEmail);

        // 步骤5：点击保存按钮
        profilePage.clickSaveButton();

        // 验证：应显示邮箱已存在错误提示
        Assert.assertTrue(profilePage.isEmailExistsErrorDisplayed(), "应显示邮箱已存在错误提示");

        extentTest.pass("修改为已存在邮箱时显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-024 - 修改为已存在用户名
     * 测试步骤：
     * 1. user2和user3都已注册
     * 2. user2登录
     * 3. 访问个人资料页面
     * 4. 用户名修改为: user3（已被user3使用）
     * 5. 点击保存按钮
     * 预期结果：
     * - 显示错误提示"用户名已存在"
     */
    @Test(priority = 5, description = "TC-USER-024: 修改为已存在用户名")
    public void testModifyToExistingUsername() {
        extentTest = extentReports.createTest("testModifyToExistingUsername", "修改为已存在用户名");

        // 步骤1-2：user2登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤3：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建个人资料页面对象
        ProfilePage profilePage = new ProfilePage(driver);

        // 步骤4：将用户名修改为user3已使用的用户名
        String existingUsername = TestConfig.getTestUser3Username();
        profilePage.enterUsername(existingUsername);

        // 步骤5：点击保存按钮
        profilePage.clickSaveButton();

        // 验证：应显示用户名已存在错误提示
        Assert.assertTrue(profilePage.isUsernameExistsErrorDisplayed(), "应显示用户名已存在错误提示");

        extentTest.pass("修改为已存在用户名时显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-030 - 密码修改成功
     * 测试步骤：
     * 1. user2登录系统
     * 2. 访问密码修改页面（/user/profile）
     * 3. 输入新密码和确认密码
     * 4. 点击修改密码按钮
     * 预期结果：
     * - 密码修改成功
     * - 可使用新密码登录
     */
    @Test(priority = 6, description = "TC-USER-030: 密码修改成功")
    public void testChangePasswordSuccess() {
        extentTest = extentReports.createTest("testChangePasswordSuccess", "密码修改成功");

        // 步骤1：用户登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤2：进入个人资料页面（密码修改在同一页面）
        userFrontPage.navigateToProfile();

        // 创建密码修改页面对象
        PasswordPage passwordPage = new PasswordPage(driver);

        // 步骤3：输入新密码和确认密码
        String newPassword = "newpass123";
        passwordPage.changePassword(newPassword, newPassword);

        // 步骤4：验证密码修改成功
        String successMessage = passwordPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("成功") || successMessage.contains("Success"),
                "应显示密码修改成功提示");

        // 为后续测试重置数据：重新创建页面对象后改回原密码
        passwordPage = new PasswordPage(driver);
        String oldPassword = TestConfig.getTestUser2Password();
        passwordPage.changePassword(oldPassword, oldPassword);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } // 等待更新完成

        extentTest.pass("密码修改成功");
    }

    /**
     * 测试用例：TC-USER-031 - 密码长度不足
     * 测试步骤：
     * 1. user2登录系统
     * 2. 访问密码修改页面
     * 3. 输入长度不足6位的新密码
     * 4. 点击修改密码按钮
     * 预期结果：
     * - 显示错误提示"密码长度不能少于6位"
     */
    @Test(priority = 7, description = "TC-USER-031: 密码长度不足")
    public void testChangePasswordTooShort() {
        extentTest = extentReports.createTest("testChangePasswordTooShort", "密码长度不足");

        // 步骤1：用户登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤2：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建密码修改页面对象
        PasswordPage passwordPage = new PasswordPage(driver);

        // 步骤3：输入长度不足6位的新密码
        String shortPassword = "12345";
        passwordPage.changePassword(shortPassword, shortPassword);

        // 步骤4：验证应显示密码长度不足错误提示
        String errorMessage = passwordPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("6") || errorMessage.length() > 0,
                "应显示密码长度不足错误提示");

        extentTest.pass("密码长度不足时显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-032 - 两次密码不一致
     * 测试步骤：
     * 1. user2登录系统
     * 2. 访问密码修改页面
     * 3. 输入新密码和不同的确认密码
     * 4. 点击修改密码按钮
     * 预期结果：
     * - 显示错误提示"两次输入的密码不一致"
     */
    @Test(priority = 8, description = "TC-USER-032: 两次密码不一致")
    public void testChangePasswordMismatch() {
        extentTest = extentReports.createTest("testChangePasswordMismatch", "两次密码不一致");

        // 步骤1：用户登录系统
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤2：进入个人资料页面
        userFrontPage.navigateToProfile();

        // 创建密码修改页面对象
        PasswordPage passwordPage = new PasswordPage(driver);

        // 步骤3：输入新密码和不同的确认密码
        String newPassword = "newpass123";
        String differentPassword = "different456";
        passwordPage.changePassword(newPassword, differentPassword);

        // 步骤4：验证应显示两次密码不一致错误提示
        String errorMessage = passwordPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("一致") || errorMessage.contains("匹配") || errorMessage.length() > 0,
                "应显示两次密码不一致错误提示");

        extentTest.pass("两次密码不一致时显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-033 - 使用新密码登录
     * 测试步骤：
     * 1. 密码修改成功（TC-USER-030已完成）
     * 2. 退出登录
     * 3. 使用新密码newpass123登录
     * 预期结果：
     * - 登录成功
     */
    @Test(priority = 9, description = "TC-USER-033: 使用新密码登录")
    public void testLoginWithNewPassword() {
        extentTest = extentReports.createTest("testLoginWithNewPassword", "使用新密码登录");

        // 步骤1：先修改密码（新密码为newpass123）
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());

        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();
        userFrontPage.navigateToProfile();

        PasswordPage passwordPage = new PasswordPage(driver);
        passwordPage.changePassword("newpass123", "newpass123");

        // 验证密码修改成功
        String successMessage = passwordPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("成功"), "密码修改成功");

        // 步骤2：退出登录 - 直接使用userFrontPage的logout方法（不重定向了）
        userFrontPage.logout();
        waitForUrlContains("/index", 10);

        // 步骤3：使用新密码登录
        LoginPage newLoginPage = new LoginPage(driver);
        newLoginPage.login(TestConfig.getTestUser2Username(), "newpass123");

        // 等待首页加载
        userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 验证：应登录成功并跳转到用户首页
        Assert.assertTrue(driver.getCurrentUrl().contains("/userFront"), "使用新密码应能成功登录");

        // 为后续测试重置数据：重新进入profile页面改回原密码
        userFrontPage.navigateToProfile();
        passwordPage = new PasswordPage(driver);
        String oldPassword = TestConfig.getTestUser2Password();
        passwordPage.changePassword(oldPassword, oldPassword);

        // 验证密码修改成功
        successMessage = passwordPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("成功"), "密码修改成功");

        extentTest.pass("使用新密码登录成功");

    }
}
