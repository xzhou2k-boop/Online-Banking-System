package com.userfront.api;

import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;

/**
 * Session认证辅助类
 * 用于管理Web端点的会话认证
 */
public class SessionAuthHelper {

    private String sessionCookie;

    /**
     * 使用用户名密码登录并获取Session
     */
    public void login(String username, String password) {
        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("username", username)
                .formParam("password", password)
                .when()
                .post("/index");

        response.then().statusCode(302);

        sessionCookie = response.getCookie("JSESSIONID");
    }

    /**
     * 登录user1用户
     */
    public void loginAsUser1() {
        login("user1", "password1");
    }

    /**
     * 登录admin用户
     */
    public void loginAsAdmin() {
        login("admin", "admin");
    }

    /**
     * 获取带Session认证的请求规范
     */
    public RequestSpecification getAuthenticatedRequest() {
        return given()
                .cookie("JSESSIONID", sessionCookie);
    }

    /**
     * 获取Session Cookie
     */
    public String getSessionCookie() {
        return sessionCookie;
    }

    /**
     * 检查是否已登录
     */
    public boolean isLoggedIn() {
        return sessionCookie != null && !sessionCookie.isEmpty();
    }
}