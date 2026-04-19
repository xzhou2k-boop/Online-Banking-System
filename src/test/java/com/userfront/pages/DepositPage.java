package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 存款页面对象类
 * 封装了存款页面的所有元素定位和操作方法
 */
public class DepositPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 存款金额输入框
     */
    @FindBy(id = "amount")
    private WebElement amountInput;

    /**
     * 账户选择下拉框
     */
    @FindBy(xpath = "//select[@id='accountType']")
    private WebElement accountSelect;

    /**
     * 存款按钮
     */
    @FindBy(xpath = "//button[contains(text(),'存款')]")
    private WebElement depositButton;

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
    public DepositPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入存款金额
     * @param amount 存款金额
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
     * 点击存款按钮
     */
    public void clickDepositButton() {
        depositButton.click();
    }

    /**
     * 执行存款操作
     * @param accountType 账户类型
     * @param amount 存款金额
     */
    public void deposit(String accountType, String amount) {
        selectAccount(accountType);
        enterAmount(amount);
        clickDepositButton();
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
     * 判断是否在存款页面
     * @return 当前位置是否在存款页面
     */
    public boolean isDepositPageDisplayed() {
        return driver.getCurrentUrl().contains("/account/deposit");
    }
}
