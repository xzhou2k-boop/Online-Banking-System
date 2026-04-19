package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 取款页面对象类
 * 封装了取款页面的所有元素定位和操作方法
 */
public class WithdrawPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 取款金额输入框
     */
    @FindBy(id = "amount")
    private WebElement amountInput;

    /**
     * 账户选择下拉框
     */
    @FindBy(xpath = "//select[@id='accountType']")
    private WebElement accountSelect;

    /**
     * 取款按钮
     */
    @FindBy(xpath = "//button[contains(text(),'取款')]")
    private WebElement withdrawButton;

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
    public WithdrawPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入取款金额
     * @param amount 取款金额
     */
    public void enterAmount(String amount) {
        wait.until(ExpectedConditions.visibilityOf(amountInput));
        amountInput.clear();
        amountInput.sendKeys(amount);
    }

    /**
     * 选择账户类型
     * @param accountType 账户类型（Primary Account/Savings Account）
     */
    public void selectAccount(String accountType) {
        Select select = new Select(accountSelect);
        select.selectByVisibleText(accountType);
    }

    /**
     * 点击取款按钮
     */
    public void clickWithdrawButton() {
        withdrawButton.click();
    }

    /**
     * 执行取款操作
     * @param accountType 账户类型
     * @param amount 取款金额
     */
    public void withdraw(String accountType, String amount) {
        selectAccount(accountType);
        enterAmount(amount);
        clickWithdrawButton();
    }

    /**
     * 获取成功提示信息
     * @return 成功提示文本内容
     */
    public String getSuccessMessage() {
        wait.until(ExpectedConditions.visibilityOf(successMessage));
        return successMessage.getText();
    }

    /**
     * 获取错误提示信息
     * @return 错误提示文本内容
     */
    public String getErrorMessage() {
        wait.until(ExpectedConditions.visibilityOf(errorMessage));
        return errorMessage.getText();
    }

    /**
     * 判断是否在取款页面
     * @return 当前位置是否在取款页面
     */
    public boolean isWithdrawPageDisplayed() {
        return driver.getCurrentUrl().contains("/account/withdraw");
    }
}
