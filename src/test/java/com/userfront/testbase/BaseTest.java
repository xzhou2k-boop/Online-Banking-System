package com.userfront.testbase;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.*;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.TimeUnit;

/**
 * 基础测试类
 * 继承自该类的所有测试用例都将获得以下功能：
 * 1. 自动加载测试配置文件
 * 2. 自动初始化Selenium WebDriver
 * 3. 自动生成测试报告
 * 4. 测试失败时自动截图
 * 5. 测试结束后自动关闭浏览器
 */
public class BaseTest {

    /**
     * WebDriver实例，用于控制浏览器
     */
    protected static WebDriver driver;
    
    /**
     * 测试配置文件对象，用于读取config.properties中的配置项
     */
    protected static Properties prop;
    
    /**
     * ExtentReports报告实例，用于生成HTML测试报告
     */
    protected static ExtentReports extentReports;
    
    /**
     * ExtentTest实例，用于记录测试步骤和结果
     */
    protected static ExtentTest extentTest;
    
    /**
     * ExtentHtmlReporter实例，用于生成HTML报告文件
     */
    protected static ExtentHtmlReporter htmlReporter;

    /**
     * 初始化方法，在测试套件开始前执行一次
     * 负责加载配置文件和初始化测试报告
     */
    @BeforeSuite
    public void init() {
        // 加载测试配置文件
        prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            prop.load(fis);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // 初始化HTML报告生成器
        htmlReporter = new ExtentHtmlReporter(prop.getProperty("report.path", "target/test-output/ExtentReport.html"));
        extentReports = new ExtentReports();
        extentReports.attachReporter(htmlReporter);
    }

    /**
     * 测试前置方法，在每个测试方法执行前执行
     * 负责初始化WebDriver和打开浏览器
     */
    @BeforeMethod
    public void setUp() {
        // 从配置文件中读取浏览器类型，默认为chrome
        String browser = prop.getProperty("browser", "chrome");
        // 从配置文件中读取是否使用无头模式，默认为false
        boolean headless = Boolean.parseBoolean(prop.getProperty("headless.mode", "false"));

        if (browser.equalsIgnoreCase("chrome")) {
            // 设置ChromeDriver路径
            System.setProperty("webdriver.chrome.driver", prop.getProperty("chrome.driver.path", "src/test/resources/drivers/chromedriver.exe"));
            
            // 配置Chrome选项
            ChromeOptions options = new ChromeOptions();
            if (headless) {
                // 如果配置为无头模式，则不显示浏览器窗口
                options.addArguments("--headless");
            }
            // 禁用浏览器通知
            options.addArguments("--disable-notifications");
            // 启动时最大化窗口
            options.addArguments("--start-maximized");
            
            // 创建Chrome浏览器实例
            driver = new ChromeDriver(options);
        }

        // 设置隐式等待时间（查找元素时等待的最长时间）
        driver.manage().timeouts().implicitlyWait(Long.parseLong(prop.getProperty("implicit.wait", "10000")), TimeUnit.MILLISECONDS);
        // 设置页面加载超时时间
        driver.manage().timeouts().pageLoadTimeout(Long.parseLong(prop.getProperty("page.load.timeout", "30000")), TimeUnit.MILLISECONDS);
        // 最大化浏览器窗口
        driver.manage().window().maximize();
        // 打开被测试的应用程序URL
        driver.get(prop.getProperty("base.url", "http://localhost:8080"));
    }

    /**
     * 测试后置方法，在每个测试方法执行后执行
     * 负责清理测试环境和生成测试报告
     * @param result 测试结果对象，用于判断测试是否失败
     */
    @AfterMethod
    public void tearDown(ITestResult result) {
        // 如果测试失败且配置了失败时截图，则进行截图
        if (result.getStatus() == ITestResult.FAILURE && Boolean.parseBoolean(prop.getProperty("screenshot.on.failure", "true"))) {
            takeScreenshot(result.getName());
        }
        // 关闭浏览器
        if (driver != null) {
            driver.quit();
        }
    }

    /**
     * 测试套件结束后执行的方法
     * 负责生成最终的测试报告
     */
    @AfterSuite
    public void finish() {
        // 将测试报告写入文件
        extentReports.flush();
    }

    /**
     * 截图方法
     * 当测试失败时自动调用，将当前页面截图保存到指定目录
     * @param testName 测试名称，用于命名截图文件
     */
    protected void takeScreenshot(String testName) {
        try {
            // 将当前页面截图并保存为临时文件
            File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            // 构造截图保存路径
            String screenshotPath = prop.getProperty("screenshot.path", "target/screenshots/") + testName + ".png";
            // 创建目录（如果不存在）
            new File(screenshotPath).mkdirs();
            // 创建目标文件
            File destFile = new File(screenshotPath);
            // 将截图复制到目标文件
            org.apache.commons.io.FileUtils.copyFile(screenshot, destFile);
            System.out.println("Screenshot saved: " + screenshotPath);
        } catch (IOException e) {
            System.out.println("Screenshot capture failed: " + e.getMessage());
        }
    }

    /**
     * 等待元素出现的方法
     * 显式等待元素可见后返回该元素
     * @param locator 元素定位器
     * @param timeout 超时时间（秒）
     * @return 找到的WebElement元素
     */
    protected WebElement waitForElement(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    /**
     * 等待URL包含指定字符串
     * @param urlPart URL中应包含的字符串
     * @param timeout 超时时间（秒）
     */
    protected void waitForUrlContains(String urlPart, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.urlContains(urlPart));
    }

    /**
     * 检查元素是否存在
     * @param locator 元素定位器
     * @return 元素存在返回true，否则返回false
     */
    protected boolean isElementPresent(By locator) {
        try {
            driver.findElement(locator);
            return true;
        } catch (NoSuchElementException e) {
            return false;
        }
    }

    /**
     * 等待元素可点击后点击
     * @param locator 元素定位器
     * @param timeout 超时时间（秒）
     */
    protected void clickWhenReady(By locator, int timeout) {
        WebDriverWait wait = new WebDriverWait(driver, timeout);
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
    }

    /**
     * 通过值选择下拉框选项
     * @param dropdown 下拉框元素
     * @param value 要选择的值
     */
    protected void selectDropdownByValue(WebElement dropdown, String value) {
        Select select = new Select(dropdown);
        select.selectByValue(value);
    }

    /**
     * 通过文本选择下拉框选项
     * @param dropdown 下拉框元素
     * @param text 要选择的文本
     */
    protected void selectDropdownByText(WebElement dropdown, String text) {
        Select select = new Select(dropdown);
        select.selectByVisibleText(text);
    }
}
