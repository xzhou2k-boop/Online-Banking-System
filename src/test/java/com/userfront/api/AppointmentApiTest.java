package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;

/**
 * REST API - 预约管理测试
 * TC-API-010 ~ TC-API-011
 */
public class AppointmentApiTest extends ApiBaseTest {

    /**
     * TC-API-010: 管理员获取所有预约
     * 预期: 返回200状态码,返回所有预约JSON数组
     */
    @Test
    public void testGetAllAppointments() {
        Response response = getAdminAuth()
                .when()
                .get("/api/appointment/all");

        response.then()
                .statusCode(200)
                .contentType("application/json");
    }

    /**
     * TC-API-011: 管理员确认预约
     * 预期: 返回200状态码,预约ID=1的状态变更为已确认
     */
    @Test
    public void testConfirmAppointment() {
        Response response = getAdminAuth()
                .when()
                .get("/api/appointment/1/confirm");

        response.then().statusCode(200);
    }
}