package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 收款人管理页面对象类
 * 封装了收款人管理页面的所有元素定位和操作方法
 */
public class RecipientPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 收款人姓名输入框
     */
    @FindBy(id = "name")
    private WebElement nameInput;

    /**
     * 收款人邮箱输入框
     */
    @FindBy(id = "email")
    private WebElement emailInput;

    /**
     * 收款人电话输入框
     */
    @FindBy(id = "phone")
    private WebElement phoneInput;

    /**
     * 收款人账号输入框
     */
    @FindBy(id = "accountNumber")
    private WebElement accountNumberInput;

    /**
     * 备注输入框
     */
    @FindBy(id = "description")
    private WebElement descriptionInput;

    /**
     * 添加按钮
     */
    @FindBy(xpath = "//button[contains(text(),'添加')]")
    private WebElement addButton;

    /**
     * 保存按钮
     */
    @FindBy(xpath = "//button[contains(text(),'保存')]")
    private WebElement saveButton;

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
    public RecipientPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入收款人姓名
     * @param name 姓名
     */
    public void enterName(String name) {
        wait.until(ExpectedConditions.visibilityOf(nameInput));
        nameInput.clear();
        nameInput.sendKeys(name);
    }

    /**
     * 输入收款人邮箱
     * @param email 邮箱地址
     */
    public void enterEmail(String email) {
        emailInput.clear();
        emailInput.sendKeys(email);
    }

    /**
     * 输入收款人电话
     * @param phone 电话号码
     */
    public void enterPhone(String phone) {
        phoneInput.clear();
        phoneInput.sendKeys(phone);
    }

    /**
     * 输入收款人账号
     * @param accountNumber 银行账号
     */
    public void enterAccountNumber(String accountNumber) {
        accountNumberInput.clear();
        accountNumberInput.sendKeys(accountNumber);
    }

    /**
     * 输入备注信息
     * @param description 备注
     */
    public void enterDescription(String description) {
        descriptionInput.clear();
        descriptionInput.sendKeys(description);
    }

    /**
     * 点击添加按钮
     */
    public void clickAddButton() {
        addButton.click();
    }

    /**
     * 点击保存按钮
     */
    public void clickSaveButton() {
        saveButton.click();
    }

    /**
     * 添加收款人
     * @param name 姓名
     * @param email 邮箱
     * @param phone 电话
     * @param accountNumber 账号
     * @param description 备注
     */
    public void addRecipient(String name, String email, String phone, String accountNumber, String description) {
        enterName(name);
        enterEmail(email);
        enterPhone(phone);
        enterAccountNumber(accountNumber);
        enterDescription(description);
        clickAddButton();
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
     * 判断是否在收款人管理页面
     * @return 当前位置是否在收款人管理页面
     */
    public boolean isRecipientPageDisplayed() {
        return driver.getCurrentUrl().contains("/transfer/recipient");
    }

    /**
     * 检查收款人是否在列表中
     * @param recipientName 收款人姓名
     * @return 收款人在列表中返回true
     */
    public boolean isRecipientInList(String recipientName) {
        return driver.getPageSource().contains(recipientName);
    }
}
