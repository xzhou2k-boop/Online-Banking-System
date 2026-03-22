package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 个人资料页面对象类
 * 继承自HomePage基类，封装了个人资料页面的所有元素定位和操作方法
 */
public class ProfilePage extends HomePage {

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
     * 保存修改按钮
     */
    @FindBy(xpath = "//button[contains(text(),'保存')]")
    private WebElement saveButton;

    /**
     * 个人资料成功提示信息
     */
    @FindBy(xpath = "//div[contains(@class,'alert-success')]")
    private WebElement profileSuccessMessage;

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
     * 主账户账号单元格
     */
    @FindBy(xpath = "//table[contains(@class,'responstable')]//td[1]")
    private WebElement primaryAccountNumber;

    /**
     * 储蓄账户账号单元格
     */
    @FindBy(xpath = "//table[contains(@class,'responstable')]//td[2]")
    private WebElement savingsAccountNumber;

    /**
     * 构造函数
     * @param driver WebDriver实例
     */
    public ProfilePage(WebDriver driver) {
        super(driver);
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
     * 点击保存按钮
     */
    public void clickSaveButton() {
        saveButton.click();
    }

    /**
     * 更新个人资料
     * @param firstName 姓
     * @param lastName 名
     * @param phone 电话
     * @param email 邮箱
     */
    public void updateProfile(String firstName, String lastName, String phone, String email) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPhone(phone);
        enterEmail(email);
        clickSaveButton();
    }

    /**
     * 获取成功提示信息
     * @return 成功提示文本内容
     */
    public String getSuccessMessage() {
        try {
            wait.until(ExpectedConditions.visibilityOf(profileSuccessMessage));
            return profileSuccessMessage.getText();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取个人资料更新成功提示信息
     * @return 个人资料成功提示文本内容
     */
    public String getProfileSuccessMessage() {
        return getSuccessMessage();
    }

    /**
     * 获取错误提示信息
     * @return 错误提示文本内容
     */
    public String getErrorMessage() {
        return super.getErrorMessage();
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
     * 判断是否在个人资料页面
     * @return 当前位置是否在个人资料页面
     */
    public boolean isProfilePageDisplayed() {
        return driver.getCurrentUrl().contains("/user/profile");
    }

    /**
     * 获取当前用户名
     * @return 用户名
     */
    public String getUsername() {
        return usernameInput.getAttribute("value");
    }

    /**
     * 获取当前邮箱
     * @return 邮箱地址
     */
    public String getEmail() {
        return emailInput.getAttribute("value");
    }

    /**
     * 获取主账户账号
     * @return 主账户账号
     */
    public String getPrimaryAccountNumber() {
        wait.until(ExpectedConditions.visibilityOf(primaryAccountNumber));
        return primaryAccountNumber.getText();
    }

    /**
     * 获取储蓄账户账号
     * @return 储蓄账户账号
     */
    public String getSavingsAccountNumber() {
        wait.until(ExpectedConditions.visibilityOf(savingsAccountNumber));
        return savingsAccountNumber.getText();
    }
}
