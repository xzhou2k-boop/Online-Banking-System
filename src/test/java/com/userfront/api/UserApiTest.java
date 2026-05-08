package com.userfront.api;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.get;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * REST API - 用户管理测试
 * TC-API-001 ~ TC-API-019
 */
public class UserApiTest extends ApiBaseTest {

    /**
     * TC-API-001: 获取所有用户
     * 预期: 返回200状态码,返回包含所有用户的JSON数组
     */
    @Test
    public void testGetAllUsers() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/all")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        List<Map<String, Object>> users = response.jsonPath().getList("data");

        Assert.assertEquals(code,200);
        Assert.assertTrue(message.contains("获取用户列表成功"));
        Assert.assertNotNull(users);

    }

    /**
     * TC-API-002: 以普通用户身份获取所有用户列表
     * 预期: 返回403禁止访问错误
     */
    @Test
    public void testGetAllUsersWithNotAdmin() {
        Response response = getUser1Auth()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/all")
                .then()
                .statusCode(403)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,403);
        Assert.assertTrue(message.contains("权限不足，无法访问该资源"));
        Assert.assertEquals(path,"/api/user/all");
    }

    /**
     * TC-API-003: 获取用户主账户交易
     * 预期: 返回200状态码,返回user1主账户交易JSON数组
     */
    @Test
    public void testGetUserPrimaryTransaction() {
        Response response = getAdminAuth()
                .queryParam("username", USER1_USERNAME)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/primary/transaction")
                .then()
                .statusCode(200)
                .extract()
                .response();

        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        List<Map<String, Object>> transactions = response.jsonPath().getList("data");

        Assert.assertEquals(code,200);
        Assert.assertTrue(message.contains("获取主账户交易记录成功"));
        Assert.assertFalse(transactions.isEmpty()); // 交易记录不为空
    }

    /**
     * TC-API-004: 获取不存在用户主账户交易
     * 预期: 返回200状态码,返回user1主账户交易JSON数组
     */
    @Test
    public void testGetNotExistUserPrimaryTransaction() {
        Response response = getAdminAuth()
                .queryParam("username", "notexistuser")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/primary/transaction")
                .then()
                .statusCode(200)
                .extract()
                .response();

        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(code,500);
        Assert.assertTrue(message.contains("获取主账户交易记录失败"));
    }

    /**
     * TC-API-005: 获取用户储蓄账户交易
     * 预期: 返回200状态码,返回user1储蓄账户交易JSON数组
     */
    @Test
    public void testGetUserSavingsTransaction() {
        Response response = getAdminAuth()
                .queryParam("username", USER1_USERNAME)
                .when()
                .get("/api/user/savings/transaction")
                .then()
                .statusCode(200)
                .extract()
                .response();

        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        List<Map<String, Object>> transactions = response.jsonPath().getList("data");

        Assert.assertEquals(code,200);
        Assert.assertTrue(message.contains("获取储蓄账户交易记录成功"));
        Assert.assertFalse(transactions.isEmpty());
    }


    /**
     * TC-API-006: 启用用户
     * 预期: 返回200状态码,启用成功
     */
    @Test
    public void testEnableUser() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/enable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String data = response.jsonPath().getString("data");

        Assert.assertEquals(code,200);
        Assert.assertTrue(message.contains("已成功启用"));
        Assert.assertEquals(data,USER3_USERNAME);
    }

    /**
     * TC-API-007: 启用不存在用户
     * 预期: 返回失败code 404,启用失败
     */
    @Test
    public void testEnableNotExistUser() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + "notexist" + "/enable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(code,404);
        Assert.assertEquals(message,"用户不存在: notexist");
    }

    /**
     * TC-API-008: 启用已经启用的用户
     * 预期: 返回失败code 400,启用失败
     */
    @Test
    public void testEnableUserIsEnabled() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER1_USERNAME + "/enable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(code,400);
        Assert.assertEquals(message,"用户已经是启用状态: "+USER1_USERNAME);
    }

    /**
     * TC-API-009: 以普通用户身份启用用户
     * 预期: 返回失败code 403,启用失败
     */
    @Test
    public void testEnableUserWithNotAdmin() {
        Response response = getUser1Auth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/enable")
                .then()
                .statusCode(403)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,403);
        Assert.assertTrue(message.contains("权限不足，无法访问该资源"));
        Assert.assertEquals(path,"/api/user/user3/enable");
    }


    /**
     * TC-API-010: 禁用用户
     * 预期: 返回200状态码,用户禁用成功
     */
    @Test
    public void testDisableUser() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER2_USERNAME + "/disable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String data = response.jsonPath().getString("data");

        Assert.assertEquals(code,200);
        Assert.assertTrue(message.contains("已成功禁用"));
        Assert.assertEquals(data,USER2_USERNAME);
    }

    /**
     * TC-API-011: 禁用不存在用户
     * 预期: 返回失败code 404,禁用失败
     */
    @Test
    public void testDisableNoExistUser() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/notexist/disable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(code,404);
        Assert.assertTrue(message.contains("用户不存在: notexist"));
    }

    /**
     * TC-API-012: 禁用已经被禁用的用户
     * 预期: 返回失败code 400,禁用失败
     */
    @Test
    public void testDisableUserIsDisabled() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/disable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(code,400);
        Assert.assertTrue(message.contains("用户已被禁用"));
    }

    /**
     * TC-API-013: 禁用admin账户
     * 预期: 操作被拒绝,admin账户状态不受影响
     */
    @Test
    public void testDisableAdminAccount() {
        Response response = getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/admin/disable")
                .then()
                .statusCode(200)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");

        Assert.assertEquals(code,403);
        Assert.assertTrue(message.contains("无法禁用管理员账户"));
    }

    /**
     * TC-API-014: 以普通用户身份禁用用户
     * 预期: 返回失败code 403,禁用失败
     */
    @Test
    public void testDisableUserWithNotAdmin() {
        Response response = getUser1Auth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER2_USERNAME + "/disable")
                .then()
                .statusCode(403)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,403);
        Assert.assertTrue(message.contains("权限不足，无法访问该资源"));
        Assert.assertEquals(path,"/api/user/user2/disable");
    }

    /**
     * TC-API-015: 未认证用户获取所有用户列表
     * 预期: 返回401 未认证错误
     */
    @Test
    public void testGetAllUsersWithUnauthenticated() {
        Response response = getNoAuth()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/all")
                .then()
                .statusCode(401)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,401);
        Assert.assertTrue(message.contains("未认证，请先登录"));
        Assert.assertEquals(path,"/api/user/all");
    }

    /**
     * TC-API-016: 未认证用户获取主账户交易列表
     * 预期: 返回401 未认证错误
     */
    @Test
    public void testGetUserPrimaryTransactionWithUnauthenticated() {
        Response response = getNoAuth()
                .queryParam("username", USER1_USERNAME)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/primary/transaction")
                .then()
                .statusCode(401)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,401);
        Assert.assertTrue(message.contains("未认证，请先登录"));
        Assert.assertEquals(path,"/api/user/primary/transaction");
    }

    /**
     * TC-API-017: 未认证用户获取储蓄账户交易列表
     * 预期: 返回401 未认证错误
     */
    @Test
    public void testGetUserSavingsTransactionWithUnauthenticated() {
        Response response = getNoAuth()
                .queryParam("username", USER1_USERNAME)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/savings/transaction")
                .then()
                .statusCode(401)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,401);
        Assert.assertTrue(message.contains("未认证，请先登录"));
        Assert.assertEquals(path,"/api/user/savings/transaction");
    }

    /**
     * TC-API-018: 未认证用户启用用户
     * 预期: 返回失败code 401,启用失败
     */
    @Test
    public void testEnableUserWithUnauthenticated() {
        Response response = getNoAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/enable")
                .then()
                .statusCode(401)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,401);
        Assert.assertTrue(message.contains("未认证，请先登录"));
        Assert.assertEquals(path,"/api/user/user3/enable");
    }

    /**
     * TC-API-019: 未认证用户禁用用户
     * 预期: 返回失败code 401,禁用失败
     */
    @Test
    public void testDisableUserWithUnauthenticated() {
        Response response = getNoAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER1_USERNAME + "/disable")
                .then()
                .statusCode(401)
                .extract()
                .response();

        // 手动解析 JSON
        int code = response.jsonPath().getInt("code");
        String message = response.jsonPath().getString("message");
        String path = response.jsonPath().getString("path");

        Assert.assertEquals(code,401);
        Assert.assertTrue(message.contains("未认证，请先登录"));
        Assert.assertEquals(path,"/api/user/user1/disable");
    }}