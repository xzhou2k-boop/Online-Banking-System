package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * 预约页面对象类
 * 封装了预约页面的所有元素定位和操作方法
 */
public class AppointmentPage {

    private WebDriver driver;
    private WebDriverWait wait;

    /**
     * 日期输入框
     */
    @FindBy(id = "date")
    private WebElement dateInput;

    /**
     * 时间输入框
     */
    @FindBy(id = "time")
    private WebElement timeInput;

    /**
     * 地点选择下拉框
     */
    @FindBy(id = "location")
    private WebElement locationInput;

    /**
     * 备注输入框
     */
    @FindBy(id = "description")
    private WebElement descriptionInput;

    /**
     * 提交按钮
     */
    @FindBy(xpath = "//button[contains(text(),'提交')]")
    private WebElement submitButton;

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
    public AppointmentPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, 10);
        PageFactory.initElements(driver, this);
    }

    /**
     * 输入日期
     * @param date 日期（格式：yyyy-MM-dd）
     */
    public void enterDate(String date) {
        wait.until(ExpectedConditions.visibilityOf(dateInput));
        dateInput.clear();
        dateInput.sendKeys(date);
    }

    /**
     * 输入时间
     * @param time 时间（格式：HH:mm）
     */
    public void enterTime(String time) {
        timeInput.clear();
        timeInput.sendKeys(time);
    }

    /**
     * 输入或选择办理地点
     * @param location 办理地点
     */
    public void enterLocation(String location) {
        locationInput.clear();
        locationInput.sendKeys(location);
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
     * 点击提交按钮
     */
    public void clickSubmitButton() {
        submitButton.click();
    }

    /**
     * 创建预约
     * @param date 日期
     * @param time 时间
     * @param location 办理地点
     * @param description 备注
     */
    public void createAppointment(String date, String time, String location, String description) {
        enterDate(date);
        enterTime(time);
        enterLocation(location);
        enterDescription(description);
        clickSubmitButton();
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
     * 判断是否在预约页面
     * @return 当前位置是否在预约页面
     */
    public boolean isAppointmentPageDisplayed() {
        return driver.getCurrentUrl().contains("/appointment/create");
    }

    /**
     * 检查预约是否在列表中
     * @param location 办理地点
     * @return 预约在列表中返回true
     */
    public boolean isAppointmentInList(String location) {
        return driver.getPageSource().contains(location);
    }
}
