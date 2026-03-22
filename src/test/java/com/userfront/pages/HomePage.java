package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 基础页面对象类
 * 封装了嵌入common/header页面的所有公共元素定位和操作方法
 * 其他页面类应继承此类以复用header元素
 */
public class HomePage {

    protected WebDriver driver;
    protected WebDriverWait wait;

    /**
     * 首页链接
     */
    @FindBy(xpath = "//a[contains(@href,'/userFront') and contains(text(),'首页')]")
    protected WebElement homeLink;

    /**
     * "账户管理"下拉菜单
     */
    @FindBy(xpath = "//a[contains(text(),'账户管理')]")
    protected WebElement accountMenu;

    /**
     * "账户管理"下的"主账户"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/account/primaryAccount')]")
    protected WebElement primaryAccountLink;

    /**
     * "账户管理"下的"储蓄账户"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/account/savingsAccount')]")
    protected WebElement savingsAccountLink;

    /**
     * "转账汇款"下拉菜单
     */
    @FindBy(xpath = "//a[contains(text(),'转账汇款')]")
    protected WebElement transferMenu;

    /**
     * "转账汇款"下的"账户间转账"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/transfer/betweenAccounts')]")
    protected WebElement transferBetweenAccountsLink;

    /**
     * "转账汇款"下的"向他人转账"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/transfer/toSomeoneElse')]")
    protected WebElement transferToSomeoneElseLink;

    /**
     * "转账汇款"下的"收款人管理"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/transfer/recipient')]")
    protected WebElement recipientLink;

    /**
     * "预约服务"下拉菜单
     */
    @FindBy(xpath = "//a[contains(text(),'预约服务')]")
    protected WebElement appointmentMenu;

    /**
     * "预约服务"下的"预约办理"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/appointment/create')]")
    protected WebElement appointmentLink;

    /**
     * "预约服务"下的"我的预约"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/appointment/list')]")
    protected WebElement myAppointmentLink;

    /**
     * "系统管理"下拉菜单（仅管理员可见）
     */
    @FindBy(xpath = "//a[contains(text(),'系统管理')]")
    protected WebElement adminMenu;

    /**
     * "系统管理"下的"用户管理"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/admin/users')]")
    protected WebElement userManagementLink;

    /**
     * "系统管理"下的"预约管理"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/admin/appointments')]")
    protected WebElement appointmentManagementLink;

    /**
     * "系统管理"下的"交易监控"链接
     */
    @FindBy(xpath = "//a[contains(@href,'/admin/transactions')]")
    protected WebElement transactionMonitorLink;

    /**
     * "我的"下拉菜单
     */
    @FindBy(xpath = "//a[contains(@class, 'dropdown-toggle') and contains(text(), '我的')]")
    protected WebElement myMenu;

    /**
     * "我的"下的"个人资料"链接
     */
    @FindBy(xpath = "//ul[@class='dropdown-menu']//a[contains(@href,'/user/profile')]")
    protected WebElement profileLink;

    /**
     * "我的"下的"退出登录"链接
     */
    @FindBy(xpath = "//ul[@class='dropdown-menu']//a[contains(@href,'/logout')]")
    protected WebElement logoutLink;

    /**
     * 成功提示信息div
     */
    @FindBy(xpath = "//div[contains(@class,'alert-success')]")
    protected WebElement successMessage;

    /**
     * 错误提示信息div
     */
    @FindBy(xpath = "//div[contains(@class,'alert-danger')]")
    protected WebElement errorMessage;

    /**
     * 构造函数
     * @param driver WebDriver实例
     */
    public HomePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 构造函数（子类调用）
     * @param driver WebDriver实例
     * @param timeout 显式等待超时时间（秒）
     */
    protected HomePage(WebDriver driver, int timeout) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, timeout);
        PageFactory.initElements(driver, this);
    }

    /**
     * 点击首页链接
     */
    public void clickHomeLink() {
        homeLink.click();
    }

    /**
     * 导航到首页
     */
    public void navigateToHome() {
        clickHomeLink();
    }

    /**
     * 点击"我的"下拉菜单
     */
    public void clickMyMenu() {
        wait.until(ExpectedConditions.elementToBeClickable(myMenu));
        myMenu.click();
    }

    /**
     * 点击"个人资料"链接
     */
    public void clickProfileLink() {
        wait.until(ExpectedConditions.elementToBeClickable(profileLink));
        profileLink.click();
    }

    /**
     * 点击"退出登录"链接
     */
    public void clickLogoutLink() {
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink));
        logoutLink.click();
    }

    /**
     * 导航到个人资料页面
     */
    public void navigateToProfile() {
        wait.until(ExpectedConditions.elementToBeClickable(myMenu)).click();
        wait.until(ExpectedConditions.visibilityOf(profileLink));
        wait.until(ExpectedConditions.elementToBeClickable(profileLink)).click();
    }

    /**
     * 导航到主账户页面
     */
    public void navigateToPrimaryAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(accountMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(primaryAccountLink)).click();
    }

    /**
     * 导航到储蓄账户页面
     */
    public void navigateToSavingsAccount() {
        wait.until(ExpectedConditions.elementToBeClickable(accountMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(savingsAccountLink)).click();
    }

    /**
     * 导航到账户间转账页面
     */
    public void navigateToTransferBetweenAccounts() {
        wait.until(ExpectedConditions.elementToBeClickable(transferMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(transferBetweenAccountsLink)).click();
    }

    /**
     * 导航到向他人转账页面
     */
    public void navigateToTransferToSomeoneElse() {
        wait.until(ExpectedConditions.elementToBeClickable(transferMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(transferToSomeoneElseLink)).click();
    }

    /**
     * 导航到收款人管理页面
     */
    public void navigateToRecipient() {
        wait.until(ExpectedConditions.elementToBeClickable(transferMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(recipientLink)).click();
    }

    /**
     * 导航到预约办理页面
     */
    public void navigateToAppointment() {
        wait.until(ExpectedConditions.elementToBeClickable(appointmentMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(appointmentLink)).click();
    }

    /**
     * 导航到我的预约页面
     */
    public void navigateToMyAppointment() {
        wait.until(ExpectedConditions.elementToBeClickable(appointmentMenu)).click();
        wait.until(ExpectedConditions.elementToBeClickable(myAppointmentLink)).click();
    }

    /**
     * 退出登录
     */
    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(myMenu)).click();
        wait.until(ExpectedConditions.visibilityOf(logoutLink));
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
    }

    /**
     * 获取成功提示信息
     * @return 成功提示文本内容
     */
    public String getSuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(successMessage));
            return successMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取错误提示信息
     * @return 错误提示文本内容
     */
    public String getErrorMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(errorMessage));
            return errorMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }
}
