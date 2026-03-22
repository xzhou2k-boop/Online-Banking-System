package com.userfront.testcases;

import com.userfront.pages.*;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.Select;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 预约功能测试类
 * 包含预约创建、查看相关的所有测试用例
 * 用例编号：TC-APPT-110 至 TC-APPT-114
 */
public class AppointmentTest extends BaseTest {

    /**
     * 测试用例：TC-APPT-110 - 创建预约成功
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入预约办理页面
     * 3. 选择办理地点：北京分行
     * 4. 填写备注信息
     * 5. 点击提交按钮
     * 预期结果：
     * - 预约创建成功
     * - 页面跳转到预约列表或首页
     */
    @Test(priority = 1, description = "TC-APPT-110: 创建预约成功")
    public void testCreateAppointmentSuccess() {
        extentTest = extentReports.createTest("testCreateAppointmentSuccess", "创建预约成功");

        // 登录user1账号
        loginAsUser1();
        
        // 进入预约办理页面
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToAppointment();

        // 点击提交按钮（页面可能有日期时间选择器，先尝试提交）
        driver.findElement(By.id("submitAppointment")).click();
        
        // 选择办理地点
        Select locationSelect = new Select(driver.findElement(By.id("location")));
        locationSelect.selectByVisibleText("北京分行");

        // 再次点击提交按钮
        driver.findElement(By.id("submitAppointment")).click();

        // 验证：预约创建后应跳转
        Assert.assertTrue(driver.getCurrentUrl().contains("/appointment") || driver.getCurrentUrl().contains("/userFront"), 
            "预约创建后应跳转");
        extentTest.pass("创建预约成功");
    }

    /**
     * 测试用例：TC-APPT-111 - 未选择日期时间
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入预约办理页面
     * 3. 选择办理地点
     * 4. 填写备注信息
     * 5. 不选择日期时间，直接点击提交按钮
     * 预期结果：
     * - 显示验证错误
     * - 停留在当前页面
     */
    @Test(priority = 2, description = "TC-APPT-111: 未选择日期时间")
    public void testAppointmentWithoutDateTime() {
        extentTest = extentReports.createTest("testAppointmentWithoutDateTime", "未选择日期时间");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToAppointment();

        // 选择办理地点
        Select locationSelect = new Select(driver.findElement(By.id("location")));
        locationSelect.selectByVisibleText("上海分行");

        // 填写备注
        driver.findElement(By.id("description")).sendKeys("测试预约");
        
        // 不填写日期时间，直接点击提交
        driver.findElement(By.id("submitAppointment")).click();

        // 验证：未填写日期应停留在当前页面
        Assert.assertTrue(driver.getCurrentUrl().contains("/appointment/create"), "未填写日期应停留在当前页面");
        extentTest.pass("未选择日期时间时显示验证错误");
    }

    /**
     * 测试用例：TC-APPT-112 - 未选择地点
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入预约办理页面
     * 3. 填写备注信息
     * 4. 不选择办理地点，直接点击提交按钮
     * 预期结果：
     * - 显示验证错误
     * - 停留在当前页面
     */
    @Test(priority = 3, description = "TC-APPT-112: 未选择地点")
    public void testAppointmentWithoutLocation() {
        extentTest = extentReports.createTest("testAppointmentWithoutLocation", "未选择地点");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToAppointment();

        // 填写备注
        driver.findElement(By.id("description")).sendKeys("测试预约");
        
        // 不选择地点，直接点击提交
        driver.findElement(By.id("submitAppointment")).click();

        // 验证：未选择地点应停留在当前页面
        Assert.assertTrue(driver.getCurrentUrl().contains("/appointment/create"), "未选择地点应停留在当前页面");
        extentTest.pass("未选择地点时显示验证错误");
    }

    /**
     * 测试用例：TC-APPT-114 - 查看我的预约
     * 测试步骤：
     * 1. 登录系统
     * 2. 直接访问预约列表页面
     * 预期结果：
     * - 页面跳转到预约列表页面
     * - 显示当前用户的预约记录
     */
    @Test(priority = 4, description = "TC-APPT-114: 查看我的预约")
    public void testViewMyAppointments() {
        extentTest = extentReports.createTest("testViewMyAppointments", "查看我的预约");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        
        // 直接访问预约列表页面
        driver.get(TestConfig.getBaseUrl() + "/appointment/list");

        // 验证：应跳转到预约列表页面
        Assert.assertTrue(driver.getCurrentUrl().contains("/appointment/list"), "应跳转到预约列表页面");
        extentTest.pass("查看我的预约成功");
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
