package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 密码修改页面对象类
 * 封装了密码修改页面的所有元素定位和操作方法
 */
public class PasswordPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 新密码输入框
     */
    @FindBy(id = "newPassword")
    private WebElement newPasswordInput;

    /**
     * 确认新密码输入框
     */
    @FindBy(id = "confirmPassword")
    private WebElement confirmPasswordInput;

    /**
     * 修改密码按钮
     */
    @FindBy(xpath = "//button[contains(text(),'修改密码')]")
    private WebElement changePasswordButton;

    /**
     * 成功提示信息div
     */
    @FindBy(xpath = "//div[contains(@class,'alert-success')]")
    private WebElement successMessage;

    /**
     * 错误提示信息div
     */
    @FindBy(xpath = "//div[contains(@class,'alert-danger')]")
    private WebElement errorMessage;

    /**
     * 构造函数
     * @param driver WebDriver实例
     */
    public PasswordPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入新密码
     * @param password 新密码
     */
    public void enterNewPassword(String password) {
        wait.until(ExpectedConditions.visibilityOf(newPasswordInput));
        newPasswordInput.clear();
        newPasswordInput.sendKeys(password);
    }

    /**
     * 输入确认新密码
     * @param password 确认新密码
     */
    public void enterConfirmPassword(String password) {
        confirmPasswordInput.clear();
        confirmPasswordInput.sendKeys(password);
    }

    /**
     * 点击修改密码按钮
     */
    public void clickChangePasswordButton() {
        changePasswordButton.click();
    }

    /**
     * 执行密码修改操作
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     */
    public void changePassword(String newPassword, String confirmPassword) {
        enterNewPassword(newPassword);
        enterConfirmPassword(confirmPassword);
        clickChangePasswordButton();
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

    /**
     * 判断是否在密码修改页面
     * @return 当前位置是否在密码修改页面
     */
    public boolean isPasswordPageDisplayed() {
        return driver.getCurrentUrl().contains("/user/profile");
    }
}
