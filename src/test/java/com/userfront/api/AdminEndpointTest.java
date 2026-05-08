package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Web端点测试 - 管理员端点
 * TC-API-060 ~ TC-API-063
 */
public class AdminEndpointTest extends ApiBaseTest {

    private SessionAuthHelper adminSession;
    private SessionAuthHelper userSession;

    @BeforeMethod
    public void setUpAdminSession() {
        adminSession = new SessionAuthHelper();
        adminSession.loginAsAdmin();
    }

    @BeforeMethod(dependsOnMethods = "setUpAdminSession")
    public void setUpUserSession() {
        userSession = new SessionAuthHelper();
        userSession.loginAsUser1();
    }

    /**
     * TC-API-060: 管理员访问用户管理
     */
    @Test
    public void testAdminAccessUserManagement() {
        Response response = adminSession.getAuthenticatedRequest()
                .when()
                .get("/admin/users");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-061: 管理员访问预约管理
     */
    @Test
    public void testAdminAccessAppointmentManagement() {
        Response response = adminSession.getAuthenticatedRequest()
                .when()
                .get("/admin/appointments");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-062: 管理员访问交易监控
     */
    @Test
    public void testAdminAccessTransactionMonitoring() {
        Response response = adminSession.getAuthenticatedRequest()
                .when()
                .get("/admin/transactions");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-063: 普通用户访问管理员页面
     */
    @Test
    public void testUserAccessAdminPage() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/admin/users");

        response.then()
                .statusCode(anyOf(is(403), is(302)));
    }
}