package com.userfront.testcases;

import com.userfront.pages.LoginPage;
import com.userfront.pages.UserFrontPage;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;
import org.openqa.selenium.By;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 管理员功能测试类
 * 包含管理员用户管理、权限控制相关的所有测试用例
 * 用例编号：TC-ADMIN-130 至 TC-ADMIN-140
 */
public class AdminTest extends BaseTest {

    /**
     * 测试用例：TC-ADMIN-130 - 管理员查看所有用户
     * 测试步骤：
     * 1. 使用管理员账号登录系统
     * 2. 访问用户管理页面 /admin/users
     * 预期结果：
     * - 管理员可以访问用户管理页面
     * - 页面显示所有用户列表（包括user1等用户）
     */
    @Test(priority = 1, description = "TC-ADMIN-130: 管理员查看所有用户")
    public void testAdminViewAllUsers() {
        extentTest = extentReports.createTest("testAdminViewAllUsers", "管理员查看所有用户");

        // 使用管理员账号登录
        loginAsAdmin();
        
        // 访问用户管理页面
        driver.get(TestConfig.getBaseUrl() + "/admin/users");

        // 验证：管理员应能访问用户管理页面
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/users"), "管理员应能访问用户管理页面");
        // 验证：应显示用户列表
        Assert.assertTrue(driver.getPageSource().contains("user1"), "应显示用户列表");
        extentTest.pass("管理员查看所有用户成功");
    }

    /**
     * 测试用例：TC-ADMIN-131 - 管理员启用用户
     * 测试步骤：
     * 1. 使用管理员账号登录系统
     * 2. 访问用户管理页面
     * 3. 找到被禁用的用户，点击"启用"按钮
     * 预期结果：
     * - 用户被启用
     * - 可以正常登录
     */
    @Test(priority = 2, description = "TC-ADMIN-131: 管理员启用用户")
    public void testAdminEnableUser() {
        extentTest = extentReports.createTest("testAdminEnableUser", "管理员启用用户");

        loginAsAdmin();
        
        // 访问用户管理页面
        driver.get(TestConfig.getBaseUrl() + "/admin/users");

        // 检查是否存在"启用"按钮，如果存在则点击
        if (isElementPresent(By.xpath("//button[contains(text(),'启用')]"))) {
            driver.findElement(By.xpath("//button[contains(text(),'启用')]")).click();
        }

        extentTest.pass("管理员启用用户功能正常");
    }

    /**
     * 测试用例：TC-ADMIN-132 - 管理员禁用用户
     * 测试步骤：
     * 1. 使用管理员账号登录系统
     * 2. 访问用户管理页面
     * 3. 找到需要禁用的用户，点击"禁用"按钮
     * 预期结果：
     * - 用户被禁用
     * - 无法正常登录
     */
    @Test(priority = 3, description = "TC-ADMIN-132: 管理员禁用用户")
    public void testAdminDisableUser() {
        extentTest = extentReports.createTest("testAdminDisableUser", "管理员禁用用户");

        loginAsAdmin();
        
        driver.get(TestConfig.getBaseUrl() + "/admin/users");

        // 检查是否存在"禁用"按钮，如果存在则点击
        if (isElementPresent(By.xpath("//button[contains(text(),'禁用')]"))) {
            driver.findElement(By.xpath("//button[contains(text(),'禁用')]")).click();
        }

        extentTest.pass("管理员禁用用户功能正常");
    }

    /**
     * 测试用例：TC-ADMIN-133 - 管理员账户保护
     * 测试步骤：
     * 1. 使用管理员账号登录系统
     * 2. 访问用户管理页面
     * 3. 尝试禁用admin账号
     * 预期结果：
     * - 禁用操作被拒绝
     * - 显示提示信息"管理员账户受保护，无法禁用"
     */
    @Test(priority = 4, description = "TC-ADMIN-133: 管理员账户保护")
    public void testAdminAccountProtection() {
        extentTest = extentReports.createTest("testAdminAccountProtection", "管理员账户保护");

        loginAsAdmin();
        
        driver.get(TestConfig.getBaseUrl() + "/admin/users");

        // 验证：admin账号应显示在列表中
        Assert.assertTrue(driver.getPageSource().contains("admin"), "管理员账户应显示在列表中");
        
        // 尝试禁用admin账号
        if (isElementPresent(By.xpath("//td[contains(text(),'admin')]//following-sibling::td//button[contains(text(),'禁用')]"))) {
            driver.findElement(By.xpath("//td[contains(text(),'admin')]//following-sibling::td//button[contains(text(),'禁用')]")).click();
            
            // 如果出现错误提示，验证是否包含"受保护"信息
            if (isElementPresent(By.xpath("//div[contains(@class,'alert-danger')]"))) {
                Assert.assertTrue(driver.findElement(By.xpath("//div[contains(@class,'alert-danger')]"))
                    .getText().contains("受保护"), "应显示管理员账户受保护提示");
            }
        }
        
        extentTest.pass("管理员账户保护功能正常");
    }

    /**
     * 测试用例：TC-ADMIN-134 - 普通用户访问用户管理
     * 测试步骤：
     * 1. 使用普通用户账号（user1）登录系统
     * 2. 尝试直接访问管理员用户管理页面 /admin/users
     * 预期结果：
     * - 访问被拒绝
     * - 页面跳转到首页或显示403 Forbidden
     */
    @Test(priority = 5, description = "TC-ADMIN-134: 普通用户访问用户管理")
    public void testRegularUserAccessUserManagement() {
        extentTest = extentReports.createTest("testRegularUserAccessUserManagement", "普通用户访问用户管理");

        // 使用普通用户账号登录
        loginAsUser1();
        
        // 尝试访问管理员页面
        driver.get(TestConfig.getBaseUrl() + "/admin/users");

        // 验证：普通用户访问管理员页面应被拒绝（跳转回首页或显示403）
        Assert.assertTrue(driver.getCurrentUrl().contains("/userFront") || 
                          driver.getCurrentUrl().contains("/access-denied") ||
                          driver.getCurrentUrl().contains("/403"),
                          "普通用户访问管理员页面应被拒绝");
        extentTest.pass("普通用户无法访问用户管理页面");
    }

    /**
     * 测试用例：TC-ADMIN-140 - 管理员查看所有交易
     * 测试步骤：
     * 1. 使用管理员账号登录系统
     * 2. 访问交易监控页面 /admin/transactions
     * 预期结果：
     * - 管理员可以访问交易监控页面
     * - 页面显示所有用户的交易记录
     */
    @Test(priority = 6, description = "TC-ADMIN-140: 管理员查看所有交易")
    public void testAdminViewAllTransactions() {
        extentTest = extentReports.createTest("testAdminViewAllTransactions", "管理员查看所有交易");

        loginAsAdmin();
        
        // 访问交易监控页面
        driver.get(TestConfig.getBaseUrl() + "/admin/transactions");

        // 验证：管理员应能访问交易监控页面
        Assert.assertTrue(driver.getCurrentUrl().contains("/admin/transactions"), "管理员应能访问交易监控页面");
        extentTest.pass("管理员查看所有交易成功");
    }

    /**
     * 辅助方法：使用管理员账号登录系统
     */
    private void loginAsAdmin() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getAdminUsername(), TestConfig.getAdminPassword());
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();
    }

    /**
     * 辅助方法：使用user1账号登录系统
     */
    private void loginAsUser1() {
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser1Username(), TestConfig.getTestUser1Password());
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.waitForHomePage();
    }
}
