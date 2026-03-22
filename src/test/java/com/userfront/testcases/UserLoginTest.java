package com.userfront.testcases;

import com.userfront.pages.LoginPage;
import com.userfront.pages.UserFrontPage;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;

import org.openqa.selenium.Cookie;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Set;

/**
 * 用户登录功能测试类
 * 包含用户登录相关的所有测试用例
 * 用例编号：TC-USER-010 至 TC-USER-015
 */
public class UserLoginTest extends BaseTest {

    /**
     * 测试用例：TC-USER-010 - 正确用户名密码登录
     * 测试步骤：
     * 1. 在登录页面输入正确的用户名和密码
     * 2. 点击登录按钮
     * 预期结果：
     * - 登录成功
     * - 页面跳转到用户首页 /userFront
     */
    @Test(priority = 1, description = "TC-USER-010: 正确用户名密码登录")
    public void testLoginWithCorrectCredentials() {
        extentTest = extentReports.createTest("testLoginWithCorrectCredentials", "正确用户名密码登录");

        // 创建登录页面对象
        LoginPage loginPage = new LoginPage(driver);
        // 使用正确的用户名和密码登录
        loginPage.login(TestConfig.getTestUser1Username(), TestConfig.getTestUser1Password());

        // 等待首页加载
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 验证：登录成功后应跳转到用户首页
        Assert.assertTrue(driver.getCurrentUrl().contains("/userFront"), "登录成功应跳转到用户首页");
        extentTest.pass("正确用户名密码登录成功");
    }

    /**
     * 测试用例：TC-USER-011 - 错误密码登录
     * 测试步骤：
     * 1. 在登录页面输入正确的用户名
     * 2. 输入错误的密码
     * 3. 点击登录按钮
     * 预期结果：
     * - 显示"用户名或密码错误"提示
     */
    @Test(priority = 2, description = "TC-USER-011: 错误密码登录")
    public void testLoginWithWrongPassword() {
        extentTest = extentReports.createTest("testLoginWithWrongPassword", "错误密码登录");

        LoginPage loginPage = new LoginPage(driver);
        // 使用正确用户名+错误密码登录
        loginPage.login(TestConfig.getTestUser1Username(), "wrongpassword");

        // 获取错误提示信息
        String errorMessage = loginPage.getErrorMessage();
        // 验证：应显示用户名或密码错误
        Assert.assertTrue(errorMessage.contains("用户名或密码错误"), "应显示用户名或密码错误提示");
        extentTest.pass("错误密码登录显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-012 - 不存在用户名登录
     * 测试步骤：
     * 1. 在登录页面输入不存在的用户名
     * 2. 输入任意密码
     * 3. 点击登录按钮
     * 预期结果：
     * - 显示"用户名或密码错误"提示
     */
    @Test(priority = 3, description = "TC-USER-012: 不存在用户名登录")
    public void testLoginWithNonExistentUsername() {
        extentTest = extentReports.createTest("testLoginWithNonExistentUsername", "不存在用户名登录");

        LoginPage loginPage = new LoginPage(driver);
        // 使用不存在的用户名登录
        loginPage.login("nonexistent", "anypassword");

        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("用户名或密码错误"), "应显示用户名或密码错误提示");
        extentTest.pass("不存在用户名登录显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-013 - 禁用账号登录
     * 测试步骤：
     * 1. 使用已被禁用的账号登录
     * 预期结果：
     * - 显示"账号已被禁用"提示
     */
    @Test(priority = 4, description = "TC-USER-013: 禁用账号登录")
    public void testLoginWithDisabledAccount() {
        extentTest = extentReports.createTest("testLoginWithDisabledAccount", "禁用账号登录");

        LoginPage loginPage = new LoginPage(driver);
        // 使用禁用账号登录（user3为禁用状态）
        loginPage.login(TestConfig.getTestUser3Username(), TestConfig.getTestUser3Password());

        String errorMessage = loginPage.getErrorMessage();
        // 验证：应显示账号已被禁用提示
        Assert.assertTrue(errorMessage.contains("禁用") || errorMessage.contains("已禁用"), "应显示账号已被禁用提示");
        extentTest.pass("禁用账号登录显示正确错误提示");
    }

    /**
     * 测试用例：TC-USER-014 - 记住我功能
     * <p>
     * 测试步骤：
     * 1. 访问登录页面
     * 2. 勾选"记住我"复选框
     * 3. 输入正确的用户名和密码
     * 4. 点击登录按钮
     * 5. 记录登录成功后生成的remember-me cookie
     * 6. 关闭当前浏览器会话
     * 7. 重新初始化浏览器（模拟重新打开浏览器）
     * 8. 将之前保存的remember-me cookie添加到新浏览器会话中
     * 9. 访问用户首页，验证是否自动登录成功
     * <p>
     * 预期结果：
     * - 关闭浏览器后重新打开仍保持登录状态
     * - 页面应能正常访问用户首页 /userFront
     * <p>
     * 测试说明：
     * 由于Selenium的driver.quit()会完全关闭浏览器进程，无法保留之前的会话。
     * 因此本测试采用手动保存和恢复cookie的方式来模拟"关闭浏览器后重新打开"的场景。
     * 当用户勾选"记住我"后，服务器会生成一个加密的remember-me token并存储在cookie中。
     * 重新打开浏览器时，浏览器会自动发送该cookie，服务器验证通过后自动建立会话。
     */
    @Test(priority = 5, description = "TC-USER-014: 记住我功能")
    public void testRememberMeFunction() {
        // 创建测试报告中的测试节点
        extentTest = extentReports.createTest("testRememberMeFunction", "记住我功能");

        // 步骤1-4：创建登录页面对象，勾选"记住我"并完成登录
        LoginPage loginPage = new LoginPage(driver);
        loginPage.loginWithRememberMe(TestConfig.getTestUser1Username(), TestConfig.getTestUser1Password());

        // 等待用户首页加载完成
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 步骤5：获取登录后浏览器中的所有cookie
        // remember-me功能通过cookie实现，服务器会在用户登录成功后生成一个加密的token
        Set<Cookie> cookies = driver.manage().getCookies();
        
        // 遍历所有cookie，查找包含"remember-me"名称的cookie
        // 该cookie包含用户的登录凭证信息，用于后续自动登录
        Cookie rememberMeCookie = null;
        for (Cookie cookie : cookies) {
            if (cookie.getName().contains("remember-me")) {
                rememberMeCookie = cookie;
                break;
            }
        }

        // 步骤6：关闭当前浏览器会话
        // driver.quit()会完全关闭浏览器窗口并结束WebDriver会话
        driver.quit();
        driver = null;

        // 步骤7：重新初始化浏览器，模拟用户重新打开浏览器
        setUp();

        // 步骤8：将之前保存的remember-me cookie添加到新浏览器会话中
        // 模拟浏览器重新打开后自动发送remember-me cookie给服务器
        // 服务器验证cookie中的token有效后，会自动建立用户会话
        if (rememberMeCookie != null) {
            driver.manage().addCookie(rememberMeCookie);
        }

        // 步骤9：访问用户首页，验证是否自动登录成功
        // 由于已添加remember-me cookie，服务器应自动验证并允许访问
        driver.get(TestConfig.getBaseUrl() + "/userFront");

        // 验证：检查是否成功访问用户首页
        // 如果remember-me功能正常工作，页面应能正常显示用户首页内容
        UserFrontPage afterReopen = new UserFrontPage(driver);
        Assert.assertTrue(afterReopen.isUserFrontPageDisplayed() || driver.getCurrentUrl().contains("/userFront"), 
            "关闭浏览器后重新打开应保持登录状态");
        
        // 测试通过，记录测试结果
        extentTest.pass("记住我功能正常工作");
    }

    /**
     * 测试用例：TC-USER-015 - 退出登录
     * 测试步骤：
     * 1. 登录系统
     * 2. 点击"退出登录"按钮
     * 预期结果：
     * - Session被清除
     * - 页面跳转到登录页
     */
    @Test(priority = 6, description = "TC-USER-015: 退出登录")
    public void testLogout() {
        extentTest = extentReports.createTest("testLogout", "退出登录");

        // 先登录
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser1Username(), TestConfig.getTestUser1Password());

        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();

        // 点击退出登录
        userFrontPage.logout();

        // 等待页面跳转到登录页
        waitForUrlContains("/index", 10);

        // 验证：退出后应跳转到登录页
        Assert.assertTrue(driver.getCurrentUrl().contains("/index"), "退出登录后应跳转到登录页");
        extentTest.pass("退出登录成功");
    }
}
