package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 转账页面对象类
 * 封装了转账页面的所有元素定位和操作方法
 * 包括账户间转账和向他人转账
 */
public class TransferPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 转出账户下拉框
     */
    @FindBy(xpath = "//select[@id='transferFrom']")
    private WebElement fromAccountSelect;

    /**
     * 转入账户下拉框
     */
    @FindBy(xpath = "//select[@id='transferTo']")
    private WebElement toAccountSelect;

    /**
     * 收款人下拉框(向他人转账)
     */
    @FindBy(xpath = "//select[@id='recipientName']")
    private WebElement recipientNameSelect;

    /**
     * 转账金额输入框
     */
    @FindBy(id = "amount")
    private WebElement amountInput;

    /**
     * 转账按钮
     */
    @FindBy(xpath = "//button[contains(text(),'转账')]")
    private WebElement transferButton;

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
    public TransferPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 选择转出账户
     * @param accountType 账户类型（Primary Account/Savings Account）
     */
    public void selectFromAccount(String accountType) {
        wait.until(ExpectedConditions.visibilityOf(fromAccountSelect));
        Select select = new Select(fromAccountSelect);
        select.selectByVisibleText(accountType);
    }

    /**
     * 获取当前选中的转出账户类型
     * @return
     */
    public String getFromAccount(){
        Select select = new Select(fromAccountSelect);
        return select.getFirstSelectedOption().getText();
    }

    /**
     * 选择转入账户（账户间转账时使用）
     * @param accountType 账户类型
     */
    public void selectToAccount(String accountType) {
        Select select = new Select(toAccountSelect);
        select.selectByVisibleText(accountType);
    }

    /**
     * 获取当前选中的转入账户类型
     * @return
     */
    public String getToAccount(){
        Select select = new Select(toAccountSelect);
        return select.getFirstSelectedOption().getText();
    }

    /**
     * 选择收款人（向他人转账时使用）
     * @param recipientName 收款人姓名
     */
    public void selectToRecipient(String recipientName) {
        Select select = new Select(recipientNameSelect);
        select.selectByVisibleText(recipientName);
    }

    /**
     * 输入转账金额
     * @param amount 转账金额
     */
    public void enterAmount(String amount) {
        amountInput.clear();
        amountInput.sendKeys(amount);
    }

    /**
     * 点击转账按钮
     */
    public void clickTransferButton() {
        transferButton.click();
    }

    /**
     * 执行账户间转账操作
     * @param fromAccount 转出账户
     * @param toAccount 转入账户
     * @param amount 转账金额
     */
    public void transferBetweenAccounts(String fromAccount, String toAccount, String amount) {
        selectFromAccount(fromAccount);
        selectToAccount(toAccount);
        enterAmount(amount);
        clickTransferButton();
    }

    /**
     * 执行向他人转账操作
     * @param fromAccount 转出账户
     * @param recipientName 收款人姓名
     * @param amount 转账金额
     */
    public void transferToSomeone(String fromAccount, String recipientName, String amount) {
        selectFromAccount(fromAccount);
        selectToRecipient(recipientName);
        enterAmount(amount);
        clickTransferButton();
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
     * 判断是否在转账页面
     * @return 当前位置是否在转账页面
     */
    public boolean isTransferPageDisplayed() {
        return driver.getCurrentUrl().contains("/transfer");
    }
}
