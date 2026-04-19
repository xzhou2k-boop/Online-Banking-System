package com.userfront.pages;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.ui.ExpectedConditions;

import java.util.List;

/**
 * 储蓄账户页面对象类
 * 继承自HomePage基类，封装了储蓄账户页面的所有元素定位和操作方法
 * 页面URL: /account/savingsAccount
 */
public class SavingsAccountPage extends HomePage {

    /**
     * 储蓄账户余额显示元素
     * 包含人民币符号和余额数值
     */
    @FindBy(xpath = "//div[contains(@class,'panel-success')]//h1")
    private WebElement accountBalance;

    /**
     * 账户余额文本（不含图标）
     */
    @FindBy(xpath = "//div[contains(@class,'panel-success')]//h1//span")
    private WebElement balanceText;

    /**
     * 交易记录表格
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]")
    private WebElement transactionTable;

    /**
     * 交易记录表格的所有行（不含表头）
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//tr")
    private List<WebElement> transactionRows;

    /**
     * 交易日期列
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//td[1]")
    private List<WebElement> transactionDates;

    /**
     * 交易描述列
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//td[2]")
    private List<WebElement> transactionDescriptions;

    /**
     * 交易类型列
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//td[3]")
    private List<WebElement> transactionTypes;

    /**
     * 交易状态列
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//td[4]")
    private List<WebElement> transactionStatuses;

    /**
     * 交易金额列
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//td[5]")
    private List<WebElement> transactionAmounts;

    /**
     * 可用余额列
     */
    @FindBy(xpath = "//table[contains(@class,'table-bordered')]//tbody//td[6]")
    private List<WebElement> availableBalances;

    /**
     * 无交易记录提示
     */
    @FindBy(xpath = "//td[contains(text(),'暂无数据')]")
    private WebElement noDataMessage;

    /**
     * 构造函数
     * @param driver WebDriver实例
     */
    public SavingsAccountPage(WebDriver driver) {
        super(driver);
    }

    /**
     * 获取储蓄账户余额
     * @return 账户余额字符串
     */
    public String getAccountBalance() {
        wait.until(ExpectedConditions.visibilityOf(accountBalance));
        return balanceText.getText();
    }

    /**
     * 获取账户余额（数值）
     * @return 余额数值
     */
    public double getAccountBalanceValue() {
        String balanceStr = getAccountBalance().trim();
        try {
            return Double.parseDouble(balanceStr);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }

    /**
     * 判断交易记录表格是否显示
     * @return 表格显示返回true
     */
    public boolean isTransactionTableDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOf(transactionTable));
            return transactionTable.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取交易记录数量
     * @return 交易记录行数
     */
    public int getTransactionCount() {
        try {
            wait.until(ExpectedConditions.visibilityOf(transactionTable));
            return transactionRows.size();
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 检查是否有交易记录
     * @return 有交易记录返回true
     */
    public boolean hasTransactions() {
        return getTransactionCount() > 0;
    }

    /**
     * 判断是否显示"暂无数据"
     * @return 显示暂无数据返回true
     */
    public boolean isNoDataMessageDisplayed() {
        try {
            return noDataMessage.isDisplayed();
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 获取指定索引的交易日期
     * @param index 交易记录索引（从0开始）
     * @return 交易日期字符串
     */
    public String getTransactionDate(int index) {
        if (index >= 0 && index < transactionDates.size()) {
            wait.until(ExpectedConditions.visibilityOf(transactionDates.get(index)));
            return transactionDates.get(index).getText();
        }
        return "";
    }

    /**
     * 获取指定索引的交易描述
     * @param index 交易记录索引（从0开始）
     * @return 交易描述字符串
     */
    public String getTransactionDescription(int index) {
        if (index >= 0 && index < transactionDescriptions.size()) {
            wait.until(ExpectedConditions.visibilityOf(transactionDescriptions.get(index)));
            return transactionDescriptions.get(index).getText();
        }
        return "";
    }

    /**
     * 获取指定索引的交易类型
     * @param index 交易记录索引（从0开始）
     * @return 交易类型字符串
     */
    public String getTransactionType(int index) {
        if (index >= 0 && index < transactionTypes.size()) {
            wait.until(ExpectedConditions.visibilityOf(transactionTypes.get(index)));
            return transactionTypes.get(index).getText();
        }
        return "";
    }

    /**
     * 获取指定索引的交易状态
     * @param index 交易记录索引（从0开始）
     * @return 交易状态字符串
     */
    public String getTransactionStatus(int index) {
        if (index >= 0 && index < transactionStatuses.size()) {
            wait.until(ExpectedConditions.visibilityOf(transactionStatuses.get(index)));
            return transactionStatuses.get(index).getText();
        }
        return "";
    }

    /**
     * 获取指定索引的交易金额
     * @param index 交易记录索引（从0开始）
     * @return 交易金额字符串
     */
    public String getTransactionAmount(int index) {
        if (index >= 0 && index < transactionAmounts.size()) {
            wait.until(ExpectedConditions.visibilityOf(transactionAmounts.get(index)));
            return transactionAmounts.get(index).getText();
        }
        return "";
    }

    /**
     * 获取指定索引的可用余额
     * @param index 交易记录索引（从0开始）
     * @return 可用余额字符串
     */
    public String getAvailableBalance(int index) {
        if (index >= 0 && index < availableBalances.size()) {
            wait.until(ExpectedConditions.visibilityOf(availableBalances.get(index)));
            return availableBalances.get(index).getText();
        }
        return "";
    }

    /**
     * 获取所有交易记录的完整信息
     * @return 交易记录列表
     */
    public List<String[]> getAllTransactions() {
        List<String[]> transactions = new java.util.ArrayList<>();
        int count = getTransactionCount();
        for (int i = 0; i < count; i++) {
            transactions.add(new String[]{
                getTransactionDate(i),
                getTransactionDescription(i),
                getTransactionType(i),
                getTransactionStatus(i),
                getTransactionAmount(i),
                getAvailableBalance(i)
            });
        }
        return transactions;
    }

    /**
     * 判断是否在储蓄账户页面
     * @return 当前位置是否在储蓄账户页面
     */
    public boolean isSavingsAccountPageDisplayed() {
        return driver.getCurrentUrl().contains("/account/savingsAccount");
    }
}