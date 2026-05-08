package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Web端点测试 - 公开端点
 * TC-API-030 ~ TC-API-033
 */
public class PublicEndpointTest extends ApiBaseTest {

    /**
     * TC-API-030: 访问首页
     * 预期: 返回302重定向到登录页或返回登录页面
     */
    @Test
    public void testAccessHomePage() {
        Response response = getNoAuth()
                .when()
                .get("/");

        response.then()
                .statusCode(anyOf(is(302), is(200)));
    }

    /**
     * TC-API-031: 访问登录页
     * 预期: 返回200状态码,返回登录页面HTML
     */
    @Test
    public void testAccessLoginPage() {
        Response response = getNoAuth()
                .when()
                .get("/index");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-032: 访问注册页
     * 预期: 返回200状态码,返回注册页面HTML
     */
    @Test
    public void testAccessSignupPage() {
        Response response = getNoAuth()
                .when()
                .get("/signup");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-033: 用户注册
     * 预期: 返回302重定向,用户创建成功
     */
    @Test
    public void testUserRegistration() {
        String uniqueUsername = "newuser_" + System.currentTimeMillis();
        String uniqueEmail = uniqueUsername + "@test.com";

        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("firstName", "Test")
                .formParam("lastName", "User")
                .formParam("username", uniqueUsername)
                .formParam("email", uniqueEmail)
                .formParam("phone", "1234567890")
                .formParam("address", "Test Address")
                .formParam("password", "Test123456")
                .formParam("confirmPassword", "Test123456")
                .when()
                .post("/signup");

        response.then()
                .statusCode(anyOf(is(302), is(200)));
    }
}