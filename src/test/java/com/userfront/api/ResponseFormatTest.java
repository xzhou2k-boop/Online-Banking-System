package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * 接口响应格式测试
 * TC-API-080 ~ TC-API-082
 */
public class ResponseFormatTest extends ApiBaseTest {

    /**
     * TC-API-080: API响应格式-JSON
     */
    @Test
    public void testApiResponseJsonFormat() {
        Response response = getAdminAuth()
                .when()
                .get("/api/user/all");

        response.then()
                .statusCode(200)
                .contentType(containsString("application/json"));
    }

    /**
     * TC-API-081: 错误响应格式
     */
    @Test
    public void testErrorResponseFormat() {
        Response response = getNoAuth()
                .when()
                .get("/api/user/all");

        response.then()
                .statusCode(401);
    }

    /**
     * TC-API-082: 成功响应格式
     */
    @Test
    public void testSuccessResponseFormat() {
        Response response = getAdminAuth()
                .when()
                .get("/api/user/user2/enable");

        response.then()
                .statusCode(200);
    }
}