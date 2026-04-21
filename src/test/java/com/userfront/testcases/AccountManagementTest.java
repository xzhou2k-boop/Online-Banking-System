package com.userfront.testcases;

import com.userfront.pages.*;
import com.userfront.testbase.BaseTest;
import com.userfront.testutil.TestConfig;

import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * 账户管理功能测试类
 * 包含账户余额查看、存款、取款相关的所有测试用例
 * 用例编号：TC-ACCT-040 至 TC-ACCT-073
 */
public class AccountManagementTest extends BaseTest {

    /**
     * 测试用例：TC-ACCT-040 - 查看主账户余额
     * 测试步骤：
     * 1. 登录系统
     * 2. 点击主账户链接
     * 预期结果：
     * - 页面跳转到主账户详情页面
     */
    @Test(priority = 1, description = "TC-ACCT-040: 查看主账户余额")
    public void testViewPrimaryAccountBalance() {
        extentTest = extentReports.createTest("testViewPrimaryAccountBalance", "查看主账户余额");

        // 登录user1账号
        loginAsUser1();
        
        // 进入主账户页面
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToPrimaryAccount();

        PrimaryAccountPage primaryAccountPage = new PrimaryAccountPage(driver);
        // 验证：应跳转到主账户页面
        Assert.assertTrue(primaryAccountPage.isPrimaryAccountPageDisplayed(),"应跳转到主账户页面");

        String primaryBalance = primaryAccountPage.getAccountBalance();
        Assert.assertNotNull(primaryBalance, "主账户页应显示主账户余额");

        extentTest.pass("主账户余额查看成功");
    }

    /**
     * 测试用例：TC-ACCT-041 - 查看储蓄账户余额
     * 测试步骤：
     * 1. 登录系统
     * 2. 点击储蓄账户链接
     * 预期结果：
     * - 页面跳转到储蓄账户详情页面
     */
    @Test(priority = 2, description = "TC-ACCT-041: 查看储蓄账户余额")
    public void testViewSavingsAccountBalance() {
        extentTest = extentReports.createTest("testViewSavingsAccountBalance", "查看储蓄账户余额");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToSavingsAccount();

        SavingsAccountPage savingsAccountPage = new SavingsAccountPage(driver);
        Assert.assertTrue(savingsAccountPage.isSavingsAccountPageDisplayed(),"应跳转到储蓄账户页面");

        String savingsBalance = savingsAccountPage.getAccountBalance();
        Assert.assertNotNull(savingsBalance,"储蓄账户页应显示储蓄账户余额");

        extentTest.pass("储蓄账户余额查看成功");
    }

    /**
     * 测试用例：TC-ACCT-044 - 首页显示账户余额
     * 测试步骤：
     * 1. 登录系统
     * 2. 查看首页显示的账户余额
     * 预期结果：
     * - 首页显示主账户余额
     * - 首页显示储蓄账户余额
     */
    @Test(priority = 3, description = "TC-ACCT-044: 首页显示账户余额")
    public void testHomePageDisplaysAccountBalances() {
        extentTest = extentReports.createTest("testHomePageDisplaysAccountBalances", "首页显示账户余额");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        
        // 获取首页显示的账户余额
        String primaryBalance = userFrontPage.getPrimaryAccountBalance();
        String savingsBalance = userFrontPage.getSavingsAccountBalance();

        // 验证：首页应显示两个账户的余额
        Assert.assertNotNull(primaryBalance, "首页应显示主账户余额");
        Assert.assertNotNull(savingsBalance, "首页应显示储蓄账户余额");

        extentTest.pass("首页正确显示账户余额");
    }

    /**
     * 测试用例：TC-ACCT-050 - 存款到主账户
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入存款页面
     * 3. 选择主账户，输入存款金额500元
     * 4. 点击存款按钮
     * 预期结果：
     * - 存款成功
     * - 显示成功提示
     */
    @Test(priority = 4, description = "TC-ACCT-050: 存款到主账户")
    public void testDepositToPrimaryAccount() {
        extentTest = extentReports.createTest("testDepositToPrimaryAccount", "存款到主账户");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        double oldBalance = userFrontPage.getPrimaryAccountBalanceValue();

        userFrontPage.navigateToDeposit();


        DepositPage depositPage = new DepositPage(driver);
        Assert.assertTrue(depositPage.isDepositPageDisplayed(),"跳转到存款页面");
        // 存款500元到主账户
        depositPage.deposit("Primary", "500");

        userFrontPage.waitForHomePage();

        double newBalance = userFrontPage.getPrimaryAccountBalanceValue();

        Assert.assertEquals(newBalance,oldBalance+500.00);

        userFrontPage.navigateToPrimaryAccount();

        PrimaryAccountPage primaryAccountPage = new PrimaryAccountPage(driver);
        Assert.assertTrue(primaryAccountPage.isPrimaryAccountPageDisplayed(),"应跳转到主账户页面");

        String amount = primaryAccountPage.getTransactionAmount(primaryAccountPage.getTransactionCount()-1);
        Assert.assertEquals(amount,"+500.0");

        extentTest.pass("存款到主账户成功");
    }

    /**
     * 测试用例：TC-ACCT-051 - 存款到储蓄账户
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入存款页面
     * 3. 选择储蓄账户，输入存款金额300元
     * 4. 点击存款按钮
     * 预期结果：
     * - 存款成功
     * - 显示成功提示
     */
    @Test(priority = 5, description = "TC-ACCT-051: 存款到储蓄账户")
    public void testDepositToSavingsAccount() {
        extentTest = extentReports.createTest("testDepositToSavingsAccount", "存款到储蓄账户");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        double oldBalance = userFrontPage.getSavingsAccountBalanceValue();
        userFrontPage.navigateToDeposit();

        DepositPage depositPage = new DepositPage(driver);
        // 存款300元到储蓄账户
        depositPage.deposit("Savings", "300");

        userFrontPage.waitForHomePage();

        double newBalance = userFrontPage.getSavingsAccountBalanceValue();
        Assert.assertEquals(newBalance,oldBalance+300.00);
        
        userFrontPage.navigateToSavingsAccount();
        SavingsAccountPage savingsAccountPage = new SavingsAccountPage(driver);
        Assert.assertTrue(savingsAccountPage.isSavingsAccountPageDisplayed(),"应跳转至储蓄账户页面");
        String amount = savingsAccountPage.getTransactionAmount(savingsAccountPage.getTransactionCount()-1);
        Assert.assertEquals(amount,"+300.0");

        extentTest.pass("存款到储蓄账户成功");
    }

    /**
     * 测试用例：TC-ACCT-052 - 存款金额为0
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入存款页面
     * 3. 输入存款金额0
     * 4. 点击存款按钮
     * 预期结果：
     * - 显示错误提示"存款金额必须大于0"
     */
    @Test(priority = 6, description = "TC-ACCT-052: 存款金额为0")
    public void testDepositWithZeroAmount() {
        extentTest = extentReports.createTest("testDepositWithZeroAmount", "存款金额为0");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToDeposit();

        DepositPage depositPage = new DepositPage(driver);
        // 存款金额为0
        depositPage.deposit("Primary", "0");

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("大于0"), "应显示存款金额必须大于0错误");
        extentTest.pass("存款金额为0时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-053 - 存款金额为负数
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入存款页面
     * 3. 输入存款金额-100
     * 4. 点击存款按钮
     * 预期结果：
     * - 显示错误提示"存款金额必须大于0"
     */
    @Test(priority = 7, description = "TC-ACCT-053: 存款金额为负数")
    public void testDepositWithNegativeAmount() {
        extentTest = extentReports.createTest("testDepositWithNegativeAmount", "存款金额为负数");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToDeposit();

        DepositPage depositPage = new DepositPage(driver);
        // 存款金额为负数
        depositPage.enterAmount("-100");
        depositPage.selectAccount("Primary");
        depositPage.clickDepositButton();

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();

        Assert.assertTrue(errorMessage.contains("大于0"), "应显示存款金额必须大于0错误");
        extentTest.pass("存款金额为负数时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-054 - 存款金额为空
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入存款页面
     * 3. 不输入存款金额
     * 4. 点击存款按钮
     * 预期结果：
     * - 显示错误提示"请输入存款金额"
     */
    @Test(priority = 8, description = "TC-ACCT-054: 存款金额为空")
    public void testDepositWithEmptyAmount() {
        extentTest = extentReports.createTest("testDepositWithEmptyAmount", "存款金额为空");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToDeposit();

        DepositPage depositPage = new DepositPage(driver);
        depositPage.selectAccount("Primary");
        depositPage.enterAmount("");
        depositPage.clickDepositButton();

        userFrontPage.waitForHomePage();
        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("请输入存款金额") , "应显示请输入存款金额错误");
        extentTest.pass("存款金额为空时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-060 - 取款从主账户
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 选择主账户，输入取款金额100元
     * 4. 点击取款按钮
     * 预期结果：
     * - 取款成功
     * - 显示成功提示
     */
    @Test(priority = 9, description = "TC-ACCT-060: 取款从主账户")
    public void testWithdrawFromPrimaryAccount() {
        extentTest = extentReports.createTest("testWithdrawFromPrimaryAccount", "取款从主账户");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        double oldBalance = userFrontPage.getPrimaryAccountBalanceValue();

        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        withdrawPage.withdraw("Primary", "100");

        userFrontPage.waitForHomePage();

        double newBalance = userFrontPage.getPrimaryAccountBalanceValue();
        Assert.assertEquals(newBalance,oldBalance-100.00);
        
        userFrontPage.navigateToPrimaryAccount();
        PrimaryAccountPage primaryAccountPage = new PrimaryAccountPage(driver);
        Assert.assertTrue(primaryAccountPage.isPrimaryAccountPageDisplayed(),"应跳转至主账户页面");
        String amount = primaryAccountPage.getTransactionAmount(primaryAccountPage.getTransactionCount()-1);
        Assert.assertEquals(amount,"-100.0");

        extentTest.pass("从主账户取款成功");
    }

    /**
     * 测试用例：TC-ACCT-061 - 取款从储蓄账户
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 选择储蓄账户，输入取款金额50元
     * 4. 点击取款按钮
     * 预期结果：
     * - 取款成功
     * - 显示成功提示
     */
    @Test(priority = 10, description = "TC-ACCT-061: 取款从储蓄账户")
    public void testWithdrawFromSavingsAccount() {
        extentTest = extentReports.createTest("testWithdrawFromSavingsAccount", "取款从储蓄账户");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        double oldBalance = userFrontPage.getSavingsAccountBalanceValue();

        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        withdrawPage.withdraw("Savings", "50");

        userFrontPage.waitForHomePage();

        double newBalance = userFrontPage.getSavingsAccountBalanceValue();
        Assert.assertEquals(newBalance,oldBalance-50.00);
        
        userFrontPage.navigateToSavingsAccount();
        SavingsAccountPage savingsAccountPage = new SavingsAccountPage(driver);
        Assert.assertTrue(savingsAccountPage.isSavingsAccountPageDisplayed(),"应跳转至储蓄账户页面");
        String amount = savingsAccountPage.getTransactionAmount(savingsAccountPage.getTransactionCount()-1);
        Assert.assertEquals(amount,"-50.0");

        extentTest.pass("从储蓄账户取款成功");
    }

    /**
     * 测试用例：TC-ACCT-062 - 取款金额为0
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 输入取款金额0
     * 4. 点击取款按钮
     * 预期结果：
     * - 显示错误提示"取款金额必须大于0"
     */
    @Test(priority = 11, description = "TC-ACCT-062: 取款金额为0")
    public void testWithdrawWithZeroAmount() {
        extentTest = extentReports.createTest("testWithdrawWithZeroAmount", "取款金额为0");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        withdrawPage.withdraw("Primary", "0");

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("大于0"), "应显示取款金额必须大于0错误");
        extentTest.pass("取款金额为0时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-063 - 取款金额为负数
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 输入取款金额-50
     * 4. 点击取款按钮
     * 预期结果：
     * - 显示错误提示"取款金额必须大于0"
     */
    @Test(priority = 12, description = "TC-ACCT-063: 取款金额为负数")
    public void testWithdrawWithNegativeAmount() {
        extentTest = extentReports.createTest("testWithdrawWithNegativeAmount", "取款金额为负数");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        withdrawPage.enterAmount("-50");
        withdrawPage.selectAccount("Primary");
        withdrawPage.clickWithdrawButton();

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("大于0"), "应显示取款金额必须大于0错误");
        extentTest.pass("取款金额为负数时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-064 - 取款金额为空
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 不输入取款金额
     * 4. 点击取款按钮
     * 预期结果：
     * - 显示错误提示"请输入取款金额"
     */
    @Test(priority = 13, description = "TC-ACCT-064: 取款金额为空")
    public void testWithdrawWithEmptyAmount() {
        extentTest = extentReports.createTest("testWithdrawWithEmptyAmount", "取款金额为空");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        withdrawPage.selectAccount("Primary");
        withdrawPage.enterAmount("");
        withdrawPage.clickWithdrawButton();

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("请输入取款金额"), "应显示请输入取款金额错误");
        extentTest.pass("取款金额为空时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-065 - 主账户余额不足
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 从主账户取款金额远大于余额
     * 4. 点击取款按钮
     * 预期结果：
     * - 显示错误提示"主账户余额不足"
     */
    @Test(priority = 14, description = "TC-ACCT-065: 主账户余额不足")
    public void testWithdrawInsufficientBalancePrimaryAccount() {
        extentTest = extentReports.createTest("testWithdrawInsufficientBalancePrimaryAccount", "主账户余额不足");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        // 取款金额远大于账户余额
        withdrawPage.withdraw("Primary", "999999");

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("余额不足"), "应显示余额不足错误");
        extentTest.pass("主账户余额不足时显示正确错误提示");
    }

    /**
     * 测试用例：TC-ACCT-066 - 储蓄账户余额不足
     * 测试步骤：
     * 1. 登录系统
     * 2. 进入取款页面
     * 3. 从储蓄账户取款金额远大于余额
     * 4. 点击取款按钮
     * 预期结果：
     * - 显示错误提示"储蓄账户余额不足"
     */
    @Test(priority = 15, description = "TC-ACCT-066: 储蓄账户余额不足")
    public void testWithdrawInsufficientBalanceSavingsAccount() {
        extentTest = extentReports.createTest("testWithdrawInsufficientBalanceSavingsAccount", "储蓄账户余额不足");

        loginAsUser1();
        
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToWithdraw();

        WithdrawPage withdrawPage = new WithdrawPage(driver);
        withdrawPage.withdraw("Savings", "999999");

        userFrontPage.waitForHomePage();

        String errorMessage = userFrontPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("余额不足"), "应显示余额不足错误");
        extentTest.pass("储蓄账户余额不足时显示正确错误提示");
    }

    @Test(priority = 16,description = "TC-ACCT-042: 查看主账户交易记录")
    public void testViewPrimaryAccountTransactionTable() {
        extentTest = extentReports.createTest("testViewPrimaryAccountTransactionTable", "查看主账户余额");

        // 登录user1账号
        loginAsUser1();

        // 进入主账户页面
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToPrimaryAccount();

        PrimaryAccountPage primaryAccountPage = new PrimaryAccountPage(driver);
        Assert.assertTrue(primaryAccountPage.isTransactionTableDisplayed(),"显示主账户交易记录");
        Assert.assertTrue(primaryAccountPage.hasTransactions(),"存在主账户交易记录");

        extentTest.pass("查看主账户交易记录成功");
    }

    @Test(priority = 17,description = "TC-ACCT-043: 查看储蓄账户交易记录")
    public void testViewSavingsAccountTransactionTable() {
        extentTest = extentReports.createTest("testViewPrimaryAccountBalance", "查看主账户余额");

        // 登录user1账号
        loginAsUser1();

        // 进入主账户页面
        UserFrontPage userFrontPage = new UserFrontPage(driver);
        userFrontPage.navigateToSavingsAccount();

        SavingsAccountPage savingsAccountPage = new SavingsAccountPage(driver);
        Assert.assertTrue(savingsAccountPage.isSavingsAccountPageDisplayed(),"显示储蓄账户交易记录");
        Assert.assertTrue(savingsAccountPage.hasTransactions(),"存在储蓄账户交易记录");

        extentTest.pass("查看储蓄账户交易记录成功");

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
