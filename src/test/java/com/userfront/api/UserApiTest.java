package com.userfront.api;

import io.restassured.http.ContentType;
import org.testng.annotations.Test;
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
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/all")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("获取用户列表成功"))
                .body("data", notNullValue())
                .extract()
                .response();
    }

    /**
     * TC-API-002: 以普通用户身份获取所有用户列表
     * 预期: 返回403禁止访问错误
     */
    @Test
    public void testGetAllUsersWithNotAdmin() {
       getUser1Auth()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/all")
                .then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", containsString("权限不足，无法访问该资源"))
                .body("path", equalTo("/api/user/all"))
                .extract()
                .response();
    }

    /**
     * TC-API-003: 获取用户主账户交易
     * 预期: 返回200状态码,返回user1主账户交易JSON数组
     */
    @Test
    public void testGetUserPrimaryTransaction() {
        getAdminAuth()
                .queryParam("username", USER1_USERNAME)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/primary/transaction")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("获取主账户交易记录成功"))
                .body("data", notNullValue())
                .extract()
                .response();
    }

    /**
     * TC-API-004: 获取不存在用户主账户交易
     * 预期: 返回200状态码,返回user1主账户交易JSON数组
     */
    @Test
    public void testGetNotExistUserPrimaryTransaction() {
        getAdminAuth()
                .queryParam("username", "notexistuser")
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/primary/transaction")
                .then()
                .statusCode(200)
                .body("code", equalTo(500))
                .body("message", containsString("获取主账户交易记录失败"))
                .extract()
                .response();
    }

    /**
     * TC-API-005: 获取用户储蓄账户交易
     * 预期: 返回200状态码,返回user1储蓄账户交易JSON数组
     */
    @Test
    public void testGetUserSavingsTransaction() {
        getAdminAuth()
                .queryParam("username", USER1_USERNAME)
                .when()
                .get("/api/user/savings/transaction")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("获取储蓄账户交易记录成功"))
                .body("data", notNullValue())
                .extract()
                .response();
    }


    /**
     * TC-API-006: 启用用户
     * 预期: 返回200状态码,启用成功
     */
    @Test
    public void testEnableUser() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/enable")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("已成功启用"))
                .body("data", equalTo(USER3_USERNAME))
                .extract()
                .response();
    }

    /**
     * TC-API-007: 启用不存在用户
     * 预期: 返回失败code 404,启用失败
     */
    @Test
    public void testEnableNotExistUser() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + "notexist" + "/enable")
                .then()
                .statusCode(200)
                .body("code", equalTo(404))
                .body("message", containsString("用户不存在: notexist"))
                .extract()
                .response();

    }

    /**
     * TC-API-008: 启用已经启用的用户
     * 预期: 返回失败code 400,启用失败
     */
    @Test
    public void testEnableUserIsEnabled() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER1_USERNAME + "/enable")
                .then()
                .statusCode(200)
                .body("code", equalTo(400))
                .body("message", containsString("用户已经是启用状态: "+USER1_USERNAME))
                .extract()
                .response();
    }

    /**
     * TC-API-009: 以普通用户身份启用用户
     * 预期: 返回失败code 403,启用失败
     */
    @Test
    public void testEnableUserWithNotAdmin() {
        getUser1Auth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/enable")
                .then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", containsString("权限不足，无法访问该资源"))
                .body("path", equalTo("/api/user/user3/enable"))
                .extract()
                .response();
   }


    /**
     * TC-API-010: 禁用用户
     * 预期: 返回200状态码,用户禁用成功
     */
    @Test
    public void testDisableUser() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER2_USERNAME + "/disable")
                .then()
                .statusCode(200)
                .body("code", equalTo(200))
                .body("message", containsString("已成功禁用"))
                .body("data", equalTo(USER2_USERNAME))
                .extract()
                .response();
    }

    /**
     * TC-API-011: 禁用不存在用户
     * 预期: 返回失败code 404,禁用失败
     */
    @Test
    public void testDisableNoExistUser() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/notexist/disable")
                .then()
                .statusCode(200)
                .body("code", equalTo(404))
                .body("message", containsString("用户不存在: notexist"))
                .extract()
                .response();
    }

    /**
     * TC-API-012: 禁用已经被禁用的用户
     * 预期: 返回失败code 400,禁用失败
     */
    @Test
    public void testDisableUserIsDisabled() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/disable")
                .then()
                .statusCode(200)
                .body("code", equalTo(400))
                .body("message", containsString("用户已被禁用: "+USER3_USERNAME))
                .extract()
                .response();
    }

    /**
     * TC-API-013: 禁用admin账户
     * 预期: 操作被拒绝,admin账户状态不受影响
     */
    @Test
    public void testDisableAdminAccount() {
        getAdminAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/admin/disable")
                .then()
                .statusCode(200)
                .body("code", equalTo(403))
                .body("message", containsString("无法禁用管理员账户: admin"))
                .extract()
                .response();

    }

    /**
     * TC-API-014: 以普通用户身份禁用用户
     * 预期: 返回失败code 403,禁用失败
     */
    @Test
    public void testDisableUserWithNotAdmin() {
        getUser1Auth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER2_USERNAME + "/disable")
                .then()
                .statusCode(403)
                .body("code", equalTo(403))
                .body("message", containsString("权限不足，无法访问该资源"))
                .body("path", equalTo("/api/user/user2/disable"))
                .extract()
                .response();
    }

    /**
     * TC-API-015: 未认证用户获取所有用户列表
     * 预期: 返回401 未认证错误
     */
    @Test
    public void testGetAllUsersWithUnauthenticated() {
        getNoAuth()
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/all")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", containsString("未认证，请先登录"))
                .body("path", equalTo("/api/user/all"))
                .extract()
                .response();
    }

    /**
     * TC-API-016: 未认证用户获取主账户交易列表
     * 预期: 返回401 未认证错误
     */
    @Test
    public void testGetUserPrimaryTransactionWithUnauthenticated() {
        getNoAuth()
                .queryParam("username", USER1_USERNAME)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/primary/transaction")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", containsString("未认证，请先登录"))
                .body("path", equalTo("/api/user/primary/transaction"))
                .extract()
                .response();
    }

    /**
     * TC-API-017: 未认证用户获取储蓄账户交易列表
     * 预期: 返回401 未认证错误
     */
    @Test
    public void testGetUserSavingsTransactionWithUnauthenticated() {
        getNoAuth()
                .queryParam("username", USER1_USERNAME)
                .contentType(ContentType.JSON)
                .when()
                .get("/api/user/savings/transaction")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", containsString("未认证，请先登录"))
                .body("path", equalTo("/api/user/savings/transaction"))
                .extract()
                .response();

     }

    /**
     * TC-API-018: 未认证用户启用用户
     * 预期: 返回失败code 401,启用失败
     */
    @Test
    public void testEnableUserWithUnauthenticated() {
        getNoAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER3_USERNAME + "/enable")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", containsString("未认证，请先登录"))
                .body("path", equalTo("/api/user/user3/enable"))
                .extract()
                .response();
    }

    /**
     * TC-API-019: 未认证用户禁用用户
     * 预期: 返回失败code 401,禁用失败
     */
    @Test
    public void testDisableUserWithUnauthenticated() {
        getNoAuth()
                .contentType(ContentType.JSON)
                .when()
                .put("/api/user/" + USER1_USERNAME + "/disable")
                .then()
                .statusCode(401)
                .body("code", equalTo(401))
                .body("message", containsString("未认证，请先登录"))
                .body("path", equalTo("/api/user/user1/disable"))
                .extract()
                .response();
    }
}