package com.userfront.api;

import io.restassured.response.Response;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

/**
 * Web端点测试 - 需要认证的端点
 * TC-API-040 ~ TC-API-058
 */
public class AuthenticatedEndpointTest extends ApiBaseTest {

    private SessionAuthHelper userSession;

    @BeforeMethod
    public void setUpSession() {
        userSession = new SessionAuthHelper();
        userSession.loginAsUser1();
    }

    /**
     * TC-API-040: 访问用户首页
     */
    @Test
    public void testAccessUserFront() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/userFront");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-041: 访问存款页面
     */
    @Test
    public void testAccessDepositPage() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/account/deposit");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-042: 执行存款
     */
    @Test
    public void testDeposit() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("accountType", "Primary")
                .formParam("amount", "100.00")
                .when()
                .post("/account/deposit");

        response.then()
                .statusCode(anyOf(is(302), is(200)));
    }

    /**
     * TC-API-043: 访问取款页面
     */
    @Test
    public void testAccessWithdrawPage() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/account/withdraw");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-044: 执行取款
     */
    @Test
    public void testWithdraw() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("accountType", "Primary")
                .formParam("amount", "50.00")
                .when()
                .post("/account/withdraw");

        response.then()
                .statusCode(anyOf(is(302), is(200)));
    }

    /**
     * TC-API-045: 访问主账户页面
     */
    @Test
    public void testAccessPrimaryAccount() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/account/primaryAccount");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-046: 访问储蓄账户页面
     */
    @Test
    public void testAccessSavingsAccount() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/account/savingsAccount");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-047: 访问账户间转账页面
     */
    @Test
    public void testAccessBetweenAccounts() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/transfer/betweenAccounts");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-048: 执行账户间转账
     */
    @Test
    public void testBetweenAccountsTransfer() {
        Response response = userSession.getAuthenticatedRequest()
                .contentType("application/x-www-form-urlencoded")
                .formParam("transferFrom", "Primary")
                .formParam("transferTo", "Savings")
                .formParam("amount", "10.00")
                .when()
                .post("/transfer/betweenAccounts");

        response.then()
                .statusCode(anyOf(is(302), is(200)));
    }

    /**
     * TC-API-049: 访问向他人转账页面
     */
    @Test
    public void testAccessToSomeoneElse() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/transfer/toSomeoneElse");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-051: 访问收款人管理页面
     */
    @Test
    public void testAccessRecipientPage() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/transfer/recipient");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-054: 访问预约创建页面
     */
    @Test
    public void testAccessAppointmentCreatePage() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/appointment/create");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }

    /**
     * TC-API-056: 访问个人资料页面
     */
    @Test
    public void testAccessProfilePage() {
        Response response = userSession.getAuthenticatedRequest()
                .when()
                .get("/user/profile");

        response.then()
                .statusCode(200)
                .contentType(containsString("text/html"));
    }
}