package com.userfront.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.userfront.testutil.TestConfig;

/**
 * 登录页面对象类
 * 封装了登录页面的所有元素定位和操作方法
 */
public class LoginPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 用户名输入框
     */
    @FindBy(id = "username")
    private WebElement usernameInput;

    /**
     * 密码输入框
     */
    @FindBy(id = "password")
    private WebElement passwordInput;

    /**
     * "记住我"复选框
     */
    @FindBy(id = "remember-me")
    private WebElement rememberMeCheckbox;

    /**
     * 登录按钮
     */
    @FindBy(xpath = "//button[@type='submit']")
    private WebElement loginButton;

    /**
     * "注册账号"链接
     */
    @FindBy(xpath = "//a[contains(text(),'注册账号')]")
    private WebElement registerLink;

    /**
     * 错误提示信息div
     */
    @FindBy(xpath = "//div[contains(@class,'bg-danger')]")
    private WebElement errorMessage;

    /**
     * 构造函数
     * 初始化页面元素和显式等待
     * 
     * @param driver WebDriver实例
     */
    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        // 初始化页面中的@FindBy注解标记的元素
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入用户名
     * 
     * @param username 用户名
     */
    public void enterUsername(String username) {
        wait.until(ExpectedConditions.visibilityOf(usernameInput));
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    /**
     * 输入密码
     * 
     * @param password 密码
     */
    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    /**
     * 点击"记住我"复选框
     */
    public void clickRememberMe() {
        rememberMeCheckbox.click();
    }

    /**
     * 点击登录按钮
     */
    public void clickLoginButton() {
        loginButton.click();
    }

    /**
     * 点击"注册账号"链接
     */
    public void clickRegisterLink() {
        registerLink.click();
    }

    /**
     * 执行登录操作
     * 
     * @param username 用户名
     * @param password 密码
     */
    public void login(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickLoginButton();
    }

    /**
     * 执行带"记住我"功能的登录
     * 
     * @param username 用户名
     * @param password 密码
     */
    public void loginWithRememberMe(String username, String password) {
        enterUsername(username);
        enterPassword(password);
        clickRememberMe();
        clickLoginButton();
    }

    /**
     * 获取错误提示信息
     * 
     * @return 错误提示文本内容
     */
    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }

    /**
     * 判断是否在登录页面
     * 
     * @return 当前位置是否在登录页面
     */
    public boolean isLoginPageDisplayed() {
        return driver.getCurrentUrl().contains("/index")
                || driver.getCurrentUrl().equals(TestConfig.getBaseUrl() + "/");
    }

    /**
     * 等待登录成功后的跳转
     * 
     * @param expectedUrl 期望的URL包含字符串
     */
    public void waitForLoginSuccess(String expectedUrl) {
        wait.until(ExpectedConditions.urlContains(expectedUrl));
    }
}
