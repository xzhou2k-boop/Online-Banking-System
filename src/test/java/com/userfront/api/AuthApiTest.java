package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.is;

/**
 * 认证与授权测试
 * TC-API-020 ~ TC-API-023
 */
public class AuthApiTest extends ApiBaseTest {

    /**
     * TC-API-020: 未认证用户访问受保护API
     * 预期: 返回401未授权错误
     */
    @Test
    public void testUnauthenticatedAccess() {
        Response response = getNoAuth()
                .when()
                .get("/api/user/all");

        response.then()
                .statusCode(401);
    }

    /**
     * TC-API-021: 普通用户认证访问管理员API
     * 预期: 返回403禁止访问错误
     */
    @Test
    public void testUserAccessAdminApi() {
        Response response = getUser1Auth()
                .when()
                .get("/api/user/all");

        response.then().statusCode(403);
    }

    /**
     * TC-API-022: 正确管理员认证访问API
     * 预期: 返回200状态码,正常返回数据
     */
    @Test
    public void testAdminAccessApi() {
        Response response = getAdminAuth()
                .when()
                .get("/api/user/all");

        response.then()
                .statusCode(200)
                .contentType("application/json");
    }

    /**
     * TC-API-023: 无效认证信息访问API
     * 预期: 返回401认证失败
     */
    @Test
    public void testInvalidCredentials() {
        Response response = getInvalidAuth()
                .when()
                .get("/api/user/all");

        response.then().statusCode(401);
    }
}