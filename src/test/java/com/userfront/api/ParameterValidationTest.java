package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * 接口参数验证测试
 * TC-API-070 ~ TC-API-076
 */
public class ParameterValidationTest extends ApiBaseTest {

    private SessionAuthHelper userSession;

    @BeforeMethod
    public void setUpSession() {
        userSession = new SessionAuthHelper();
        userSession.loginAsUser1();
    }

    /**
     * TC-API-070: 存款金额边界-最小有效值
     */
    @Test
    public void testDepositMinAmount() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("accountType", "Primary")
                .formParam("amount", "0.01")
                .when()
                .post("/account/deposit");

        response.then()
                .statusCode(anyOf(is(302), is(200)));
    }

    /**
     * TC-API-071: 存款金额边界-无效值0
     */
    @Test
    public void testDepositZeroAmount() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("accountType", "Primary")
                .formParam("amount", "0")
                .when()
                .post("/account/deposit");

        response.then()
                .body(containsString("存款金额必须大于0"));
    }

    /**
     * TC-API-072: 取款金额边界-等于余额
     */
    @Test
    public void testWithdrawEqualToBalance() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("accountType", "Primary")
                .formParam("amount", "100.00")
                .when()
                .post("/account/withdraw");

        response.then()
                .statusCode(anyOf(is(302), is(200), is(400)));
    }

    /**
     * TC-API-073: 取款金额边界-超过余额
     */
    @Test
    public void testWithdrawExceedBalance() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("accountType", "Primary")
                .formParam("amount", "999999.00")
                .when()
                .post("/account/withdraw");

        response.then()
                .body(anyOf(containsString("余额不足"), containsString("Insufficient")));
    }

    /**
     * TC-API-074: 转账金额边界-等于余额
     */
    @Test
    public void testTransferEqualToBalance() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("transferFrom", "Primary")
                .formParam("transferTo", "Savings")
                .formParam("amount", "100.00")
                .when()
                .post("/transfer/betweenAccounts");

        response.then()
                .statusCode(anyOf(is(302), is(200), is(400)));
    }

    /**
     * TC-API-075: 用户名长度测试-超长
     */
    @Test
    public void testSignupLongUsername() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            sb.append("a");
        }
        String longUsername = sb.toString();

        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("firstName", "Test")
                .formParam("lastName", "User")
                .formParam("username", longUsername)
                .formParam("email", "long@test.com")
                .formParam("phone", "1234567890")
                .formParam("address", "Test Address")
                .formParam("password", "Test123456")
                .formParam("confirmPassword", "Test123456")
                .when()
                .post("/signup");

        response.then()
                .statusCode(anyOf(is(302), is(200), is(400)));
    }

    /**
     * TC-API-076: 邮箱格式测试-无效格式
     */
    @Test
    public void testSignupInvalidEmail() {
        Response response = given()
                .contentType("application/x-www-form-urlencoded")
                .formParam("firstName", "Test")
                .formParam("lastName", "User")
                .formParam("username", "testuser_" + System.currentTimeMillis())
                .formParam("email", "invalid-email")
                .formParam("phone", "1234567890")
                .formParam("address", "Test Address")
                .formParam("password", "Test123456")
                .formParam("confirmPassword", "Test123456")
                .when()
                .post("/signup");

        response.then()
                .statusCode(anyOf(is(200), is(302)));
    }
}