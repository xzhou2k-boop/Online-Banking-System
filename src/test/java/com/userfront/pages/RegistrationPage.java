package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 注册页面对象类
 * 封装了用户注册页面的所有元素定位和操作方法
 */
public class RegistrationPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 姓输入框
     */
    @FindBy(id = "firstName")
    private WebElement firstNameInput;

    /**
     * 名输入框
     */
    @FindBy(id = "lastName")
    private WebElement lastNameInput;

    /**
     * 电话输入框
     */
    @FindBy(id = "phone")
    private WebElement phoneInput;

    /**
     * 邮箱输入框
     */
    @FindBy(id = "email")
    private WebElement emailInput;

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
     * 注册按钮
     */
    @FindBy(xpath = "//button[@type='submit']")
    private WebElement registerButton;

    /**
     * 取消链接
     */
    @FindBy(xpath = "//a[contains(text(),'取消')]")
    private WebElement cancelLink;

    /**
     * 用户名已存在错误提示
     */
    @FindBy(xpath = "//span[contains(text(),'用户名已存在')]")
    private WebElement usernameExistsError;

    /**
     * 邮箱已存在错误提示
     */
    @FindBy(xpath = "//span[contains(text(),'邮箱已存在')]")
    private WebElement emailExistsError;

    /**
     * 构造函数
     * @param driver WebDriver实例
     */
    public RegistrationPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入姓
     * @param firstName 姓
     */
    public void enterFirstName(String firstName) {
        wait.until(ExpectedConditions.visibilityOf(firstNameInput));
        firstNameInput.clear();
        firstNameInput.sendKeys(firstName);
    }

    /**
     * 输入名
     * @param lastName 名
     */
    public void enterLastName(String lastName) {
        lastNameInput.clear();
        lastNameInput.sendKeys(lastName);
    }

    /**
     * 输入电话号码
     * @param phone 电话号码
     */
    public void enterPhone(String phone) {
        phoneInput.clear();
        phoneInput.sendKeys(phone);
    }

    /**
     * 输入邮箱地址
     * @param email 邮箱地址
     */
    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    /**
     * 输入用户名
     * @param username 用户名
     */
    public void enterUsername(String username) {
        usernameInput.clear();
        usernameInput.sendKeys(username);
    }

    /**
     * 输入密码
     * @param password 密码
     */
    public void enterPassword(String password) {
        passwordInput.clear();
        passwordInput.sendKeys(password);
    }

    /**
     * 点击注册按钮
     */
    public void clickRegisterButton() {
        registerButton.click();
    }

    /**
     * 点击取消链接
     */
    public void clickCancelLink() {
        cancelLink.click();
    }

    /**
     * 执行完整的注册流程
     * @param firstName 姓
     * @param lastName 名
     * @param phone 电话号码
     * @param email 邮箱地址
     * @param username 用户名
     * @param password 密码
     */
    public void register(String firstName, String lastName, String phone, String email, String username, String password) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPhone(phone);
        enterEmail(email);
        enterUsername(username);
        enterPassword(password);
        clickRegisterButton();
    }

    /**
     * 检查用户名已存在错误提示是否显示
     * @return 错误提示显示返回true
     */
    public boolean isUsernameExistsErrorDisplayed() {
        try {
            return usernameExistsError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 检查邮箱已存在错误提示是否显示
     * @return 错误提示显示返回true
     */
    public boolean isEmailExistsErrorDisplayed() {
        try {
            return emailExistsError.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 判断是否在注册页面
     * @return 当前位置是否在注册页面
     */
    public boolean isRegistrationPageDisplayed() {
        return driver.getCurrentUrl().contains("/signup");
    }

    /**
     * 等待注册成功后的页面跳转
     * @param expectedUrl 期望的URL包含字符串
     */
    public void waitForRegistrationSuccess(String expectedUrl) {
        wait.until(ExpectedConditions.urlContains(expectedUrl));
    }
}
