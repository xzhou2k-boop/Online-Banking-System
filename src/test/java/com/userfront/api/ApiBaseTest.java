package com.userfront.api;

import io.restassured.RestAssured;
import io.restassured.authentication.BasicAuthScheme;
import io.restassured.config.LogConfig;
import io.restassured.config.RestAssuredConfig;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * API测试基础类
 * 提供REST API测试的通用配置和辅助方法
 */
public class ApiBaseTest {

    protected static Properties prop;
    protected static String baseUrl;
    protected static int port;

    // Test accounts
    protected static final String ADMIN_USERNAME = "admin";
    protected static final String ADMIN_PASSWORD = "admin";
    protected static final String USER1_USERNAME = "user1";
    protected static final String USER1_PASSWORD = "password1";
    protected static final String USER2_USERNAME = "user2";
    protected static final String USER2_PASSWORD = "password2";
    protected static final String USER3_USERNAME = "user3";
    protected static final String USER3_PASSWORD = "password3";

    @BeforeClass
    public void setUp() {
        // Load configuration
        prop = new Properties();
        try {
            FileInputStream fis = new FileInputStream("src/test/resources/config.properties");
            prop.load(fis);
            fis.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set base URL and port
        String baseUrlWithPort = prop.getProperty("base.url", "http://localhost:8080");
        if (baseUrlWithPort.startsWith("http://")) {
            baseUrlWithPort = baseUrlWithPort.replace("http://", "");
        }
        if (baseUrlWithPort.contains(":")) {
            String[] parts = baseUrlWithPort.split(":");
            baseUrl = "http://" + parts[0];
            port = Integer.parseInt(parts[1]);
        } else {
            baseUrl = baseUrlWithPort;
            port = 8080;
        }

        RestAssured.baseURI = baseUrl;
        RestAssured.port = port;

        // Configure logging
        RestAssured.config = RestAssuredConfig.newConfig()
                .logConfig(new LogConfig().enableLoggingOfRequestAndResponseIfValidationFails());

        System.out.println("API Test Base URL: " + baseUrl + ":" + port);
    }

    /**
     * 获取管理员认证的请求规范
     */
    protected RequestSpecification getAdminAuth() {
        return RestAssured.given()
                .auth()
                .preemptive()
                .basic(ADMIN_USERNAME, ADMIN_PASSWORD);
    }

    /**
     * 获取普通用户(user1)认证的请求规范
     */
    protected RequestSpecification getUser1Auth() {
        return RestAssured.given()
                .auth()
                .preemptive()
                .basic(USER1_USERNAME, USER1_PASSWORD);
    }

    /**
     * 获取普通用户(user2)认证的请求规范
     */
    protected RequestSpecification getUser2Auth() {
        return RestAssured.given()
                .auth()
                .preemptive()
                .basic(USER2_USERNAME, USER2_PASSWORD);
    }

    /**
     * 获取无认证的请求规范
     */
    protected RequestSpecification getNoAuth() {
        return RestAssured.given();
    }

    /**
     * 获取无效认证的请求规范
     */
    protected RequestSpecification getInvalidAuth() {
        return RestAssured.given()
                .auth()
                .preemptive()
                .basic("invaliduser", "invalidpassword");
    }

    /**
     * 格式化金额用于表单提交
     */
    protected String formatAmount(double amount) {
        return String.format("%.2f", amount);
    }

    /**
     * 获取带有CSRF Token的请求（如果需要）
     */
    protected RequestSpecification getAdminAuthWithCsrf() {
        // 先获取CSRF token
        String csrfToken = RestAssured.given()
                .auth().preemptive().basic(ADMIN_USERNAME, ADMIN_PASSWORD)
                .get("/csrf")
                .then()
                .extract()
                .path("token");

        return RestAssured.given()
                .auth().preemptive().basic(ADMIN_USERNAME, ADMIN_PASSWORD)
                .header("X-CSRF-TOKEN", csrfToken);
    }
}