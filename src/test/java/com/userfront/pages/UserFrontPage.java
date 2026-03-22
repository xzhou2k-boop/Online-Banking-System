package com.userfront.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

/**
 * 用户首页页面对象类
 * 继承自HomePage基类，封装了用户登录后的首页及导航菜单的所有元素定位和操作方法
 */
public class UserFrontPage extends HomePage {

    /**
     * 存款链接
     */
    @FindBy(xpath = "//a[contains(@href,'/account/deposit')]")
    private WebElement depositLink;

    /**
     * 取款链接
     */
    @FindBy(xpath = "//a[contains(@href,'/account/withdraw')]")
    private WebElement withdrawLink;

    /**
     * 构造函数
     * @param driver WebDriver实例
     */
    public UserFrontPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 点击存款链接
     */
    public void clickDepositLink() {
        wait.until(ExpectedConditions.elementToBeClickable(depositLink));
        depositLink.click();
    }

    /**
     * 点击取款链接
     */
    public void clickWithdrawLink() {
        wait.until(ExpectedConditions.elementToBeClickable(withdrawLink));
        withdrawLink.click();
    }

    /**
     * 获取主账户余额
     * @return 主账户余额文本
     */
    public String getPrimaryAccountBalance() {
        return driver.findElement(By.xpath("//div[contains(@class,'panel-info')]//h1")).getText();
    }

    /**
     * 获取储蓄账户余额
     * @return 储蓄账户余额文本
     */
    public String getSavingsAccountBalance() {
        return driver.findElement(By.xpath("//div[contains(@class,'panel-success')]//h1")).getText();
    }

    /**
     * 判断是否在用户首页
     * @return 当前位置是否在用户首页
     */
    public boolean isUserFrontPageDisplayed() {
        return driver.getCurrentUrl().contains("/userFront");
    }

    /**
     * 等待首页加载完成
     */
    public void waitForHomePage() {
        wait.until(ExpectedConditions.urlContains("/userFront"));
    }

    /**
     * 导航到存款页面
     */
    public void navigateToDeposit() {
        clickDepositLink();
    }

    /**
     * 导航到取款页面
     */
    public void navigateToWithdraw() {
        clickWithdrawLink();
    }
}
