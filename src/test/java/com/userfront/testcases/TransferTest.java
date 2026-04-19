package com.userfront.testcases;

import com.userfront.pages.*;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 转账功能测试类
 * 包含账户间转账、向他人转账、收款人管理相关的所有测试用例
 * 用例编号：TC-TRAN-080 至 TC-TRAN-104
 */
public class TransferTest extends BaseTest {

    /**
     * 测试用例：TC-TRAN-080 - 主账户转储蓄账户
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 选择转出账户：主账户
     * 4. 选择转入账户：储蓄账户
     * 5. 输入转账金额：300元
     * 6. 点击转账按钮
     * 预期结果：
     * - 转账成功
     * - 显示成功提示
     */
    @Test(priority = 1, description = "TC-TRAN-080: 主账户转储蓄账户")
    public void testTransferPrimaryToSavings() {
        extentTest = extentReports.createTest("testTransferPrimaryToSavings", "主账户转储蓄账户");

        // 登录user1账号
        loginAsUser1();
        
        // 进入账户间转账页面
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        double primaryAccountBalance = userFrontPage.getPrimaryAccountBalanceValue();
        double savingsAccountBalance = userFrontPage.getSavingsAccountBalanceValue();

        userFrontPage.navigateToTransferBetweenAccounts();

        // 执行转账操作：主账户转储蓄账户100元
        TransferPage transferPage = new TransferPage(driver);
        transferPage.transferBetweenAccounts("Primary", "Savings", "300");

        // 等待首页加载
        userFrontPage.waitForHomePage();

        double newPrimaryAccountBalance = userFrontPage.getPrimaryAccountBalanceValue();
        double newSavingsAccountBalance = userFrontPage.getSavingsAccountBalanceValue();

        Assert.assertEquals(primaryAccountBalance, newPrimaryAccountBalance+300, "主账户转出成功");
        Assert.assertEquals(savingsAccountBalance,newSavingsAccountBalance-300,"储蓄账户转入成功");

        extentTest.pass("主账户转储蓄账户成功");
    }

    /**
     * 测试用例：TC-TRAN-081 - 储蓄账户转主账户
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 选择转出账户：储蓄账户
     * 4. 选择转入账户：主账户
     * 5. 输入转账金额：200
     * 6. 点击转账按钮
     * 预期结果：
     * - 转账成功
     * - 显示成功提示
     */
    @Test(priority = 2, description = "TC-TRAN-081: 储蓄账户转主账户")
    public void testTransferSavingsToPrimary() {
        extentTest = extentReports.createTest("testTransferSavingsToPrimary", "储蓄账户转主账户");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        double primaryAccountBalance = userFrontPage.getPrimaryAccountBalanceValue();
        double savingsAccountBalance = userFrontPage.getSavingsAccountBalanceValue();

        userFrontPage.navigateToTransferBetweenAccounts();

        TransferPage transferPage = new TransferPage(driver);
        // 储蓄账户转主账户50元
        transferPage.transferBetweenAccounts("Savings", "Primary", "200");

        userFrontPage.waitForHomePage();

        double newPrimaryAccountBalance = userFrontPage.getPrimaryAccountBalanceValue();
        double newSavingsAccountBalance = userFrontPage.getSavingsAccountBalanceValue();

        Assert.assertEquals(primaryAccountBalance,newPrimaryAccountBalance-200,"主账户转入成功");
        Assert.assertEquals(savingsAccountBalance,newSavingsAccountBalance+200,"储蓄账户转出成功");
        
        extentTest.pass("储蓄账户转主账户成功");
    }

    /**
     * 测试用例：TC-TRAN-082 - 转账金额为0
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 输入转账金额：0
     * 4. 点击转账按钮
     * 预期结果：
     * - 显示错误提示"转账金额必须大于0"
     */
    @Test(priority = 3, description = "TC-TRAN-082: 转账金额为0")
    public void testTransferWithZeroAmount() {
        extentTest = extentReports.createTest("testTransferWithZeroAmount", "转账金额为0");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToTransferBetweenAccounts();

        TransferPage transferPage = new TransferPage(driver);
        // 转账金额为0
        transferPage.transferBetweenAccounts("Primary", "Savings", "0");

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("大于0"), "应显示转账金额必须大于0错误");
        extentTest.pass("转账金额为0时显示正确错误提示");
    }

    /**
     * 测试用例：TC-TRAN-083 - 转账金额为负数
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 输入转账金额：-100
     * 4. 点击转账按钮
     * 预期结果：
     * - 显示错误提示"转账金额必须大于0"
     */
    @Test(priority = 4, description = "TC-TRAN-083: 转账金额为负数")
    public void testTransferWithNegativeAmount() {
        extentTest = extentReports.createTest("testTransferWithNegativeAmount", "转账金额为负数");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToTransferBetweenAccounts();

        TransferPage transferPage = new TransferPage(driver);
        // 转账金额为负数
        transferPage.selectFromAccount("Primary");
        transferPage.selectToAccount("Savings");
        transferPage.enterAmount("-100");
        transferPage.clickTransferButton();

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("大于0"), "应显示转账金额必须大于0错误");
        extentTest.pass("转账金额为负数时显示正确错误提示");
    }

    /**
     * 测试用例：TC-TRAN-084 - 转账金额为空
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 不输入转账金额
     * 4. 点击转账按钮
     * 预期结果：
     * - 显示错误提示"请输入转账金额"
     */
    @Test(priority = 5, description = "TC-TRAN-084: 转账金额为空")
    public void testTransferWithEmptyAmount() {
        extentTest = extentReports.createTest("testTransferWithEmptyAmount", "转账金额为空");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToTransferBetweenAccounts();

        TransferPage transferPage = new TransferPage(driver);
        transferPage.selectFromAccount("Primary");
        transferPage.selectToAccount("Savings");
        transferPage.enterAmount("");
        transferPage.clickTransferButton();

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("empty"), "应显示请输入转账金额错误");
        extentTest.pass("转账金额为空时显示正确错误提示");
    }

    /**
     * 测试用例：TC-TRAN-085 - 同一账户转账
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 转出账户和转入账户都选择主账户
     * 4. 输入转账金额：100元
     * 5. 点击转账按钮
     * 预期结果：
     * - 显示错误提示"无效的转账操作"
     */
    @Test(priority = 6, description = "TC-TRAN-085: 同一账户转账")
    public void testTransferToSameAccount() {
        extentTest = extentReports.createTest("testTransferToSameAccount", "同一账户转账");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToTransferBetweenAccounts();

        TransferPage transferPage = new TransferPage(driver);
        // 同一账户之间转账
        transferPage.selectFromAccount("Primary");
        String fromAccount = transferPage.getFromAccount();
        String toAccount = transferPage.getToAccount();
        Assert.assertEquals(fromAccount,"Primary");
        Assert.assertEquals(toAccount,"Savings");

        transferPage.selectToAccount("Primary");
        fromAccount = transferPage.getFromAccount();
        toAccount = transferPage.getToAccount();
        Assert.assertEquals(fromAccount,"Savings");
        Assert.assertEquals(toAccount,"Primary");

        extentTest.pass("不允许同一账户转账");
    }

    /**
     * 测试用例：TC-TRAN-086 - 余额不足转账
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入账户间转账页面
     * 3. 转账金额远大于账户余额
     * 4. 点击转账按钮
     * 预期结果：
     * - 显示错误提示"余额不足"
     */
    @Test(priority = 7, description = "TC-TRAN-086: 余额不足转账")
    public void testTransferWithInsufficientBalance() {
        extentTest = extentReports.createTest("testTransferWithInsufficientBalance", "余额不足转账");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToTransferBetweenAccounts();

        TransferPage transferPage = new TransferPage(driver);
        // 转账金额远大于账户余额
        transferPage.transferBetweenAccounts("Primary", "Savings", "999999");

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("余额不足"), "应显示余额不足错误");
        extentTest.pass("余额不足转账显示正确错误提示");
    }

    /**
     * 测试用例：TC-TRAN-100 - 添加收款人成功
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入收款人管理页面
     * 3. 填写收款人信息（姓名、邮箱、电话、账号、备注）
     * 4. 点击添加按钮
     * 预期结果：
     * - 收款人添加成功
     * - 显示成功提示
     */
    @Test(priority = 8, description = "TC-TRAN-100: 添加收款人成功")
    public void testAddRecipientSuccess() {
        extentTest = extentReports.createTest("testAddRecipientSuccess", "添加收款人成功");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToRecipient();

        RecipientPage recipientPage = new RecipientPage(driver);
        // 生成唯一的收款人名称
        String recipientName = "李四" + System.currentTimeMillis();
        // 添加收款人
        recipientPage.addRecipient(recipientName, "lisihappy@163.com", "13800138010", "6222021234567890", "朋友");

        String successMessage = recipientPage.getSuccessMessage();
        Assert.assertTrue(successMessage.contains("成功"), "应显示添加成功提示");
        extentTest.pass("添加收款人成功");
    }

    /**
     * 测试用例：TC-TRAN-101 - 必填字段验证
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入收款人管理页面
     * 3. 不填写任何信息，直接点击添加按钮
     * 预期结果：
     * - 表单验证失败
     * - 停留在当前页面
     */
    @Test(priority = 9, description = "TC-TRAN-101: 必填字段验证")
    public void testRecipientRequiredFields() {
        extentTest = extentReports.createTest("testRecipientRequiredFields", "必填字段验证");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToRecipient();

        RecipientPage recipientPage = new RecipientPage(driver);
        // 不填写任何信息，直接点击添加按钮
        recipientPage.clickAddButton();

        // 验证：应显示表单验证错误，停留在当前页面
        Assert.assertTrue(recipientPage.isRecipientPageDisplayed(), "应显示表单验证错误");
        extentTest.pass("必填字段验证正常工作");
    }

    /**
     * 测试用例：TC-TRAN-104 - 收款人隔离
     * 测试步骤：
     * 1. user1登录，查看收款人列表，记录页面内容
     * 2. 退出登录
     * 3. user2登录，查看收款人列表
     * 预期结果：
     * - user1只能看到自己的收款人
     * - user2只能看到自己的收款人
     * - 两人收款人列表相互隔离
     */
    @Test(priority = 10, description = "TC-TRAN-104: 收款人隔离")
    public void testRecipientIsolation() {
        extentTest = extentReports.createTest("testRecipientIsolation", "收款人隔离");

        // user1登录，查看收款人列表
        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToRecipient();

        RecipientPage recipientPage = new RecipientPage(driver);
        
        // 记录user1的收款人列表页面内容
        String user1PageSource = driver.getPageSource();
        
        // 退出登录
        logout();
        
        // user2登录，查看收款人列表
        LoginPage loginPage = new LoginPage(driver);
        loginPage.login(TestConfig.getTestUser2Username(), TestConfig.getTestUser2Password());
        
        userFrontPage.navigateToRecipient();
        
        // 记录user2的收款人列表页面内容
        String user2PageSource = driver.getPageSource();
        
        // 验证：不同用户的收款人列表应相互隔离
        Assert.assertNotEquals(user1PageSource, user2PageSource, "不同用户的收款人列表应相互隔离");
        extentTest.pass("收款人隔离正常工作");
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

    /**
     * 辅助方法：退出登录
     */
    private void logout() {
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.logout();
    }
}
