package com.asamurik_rest_api.controller;

import com.asamurik_rest_api.utils.DataGenerator;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.json.simple.JSONObject;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.Random;

import static io.restassured.RestAssured.given;
import static org.testng.Assert.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class AuthControllerTest extends AbstractTestNGSpringContextTests {
    private JSONObject request;
    private String authToken;
    private Random random;
    public static String authorization;
    private DataGenerator dataGenerator;

    // Variable penampung
    private String fullname;
    private String username;
    private String email;
    private String password;
    private String phoneNumber;
    private String isActive;
    private String otp;
    private String newPassword;
    private String resetPasswordToken;

    // Untuk menjaga step estafet
    private Boolean isOk;

    private static final String AUTHORIZATION_HEADER = "Authorization";

    @BeforeClass
    public void init() {
        RestAssured.baseURI = "http://localhost:8080";
        random = new Random();
        dataGenerator = new DataGenerator();
        request = new JSONObject();
    }

    @Test(priority = 0)
    public void testRegister() {
        Response response;
        try {
            isOk = false;
            fullname = dataGenerator.dataNamaLengkap();
            username = dataGenerator.dataUsername();
            email = dataGenerator.dataEmail();
            password = dataGenerator.dataPassword();
            phoneNumber = dataGenerator.dataNoHp();

            request.put("fullname", fullname);
            request.put("username", username);
            request.put("email", email);
            request.put("password", password);
            request.put("phone-number", phoneNumber);

            response = given()
                    .header("Content-Type", "application/json")
                    .header("accept", "*/*")
                    .body(request.toString())
                    .request(Method.POST, "/auth/register");

            int responseCode = response.getStatusCode();
            JsonPath jsonPath = response.jsonPath();
            otp = jsonPath.getString("data.otp");
            System.out.printf("Response body: %s%n", response.getBody().asString());
            if (otp != null && responseCode == 201) {
                isOk = true;
            }

            assertEquals(responseCode, 201);
            assertEquals(jsonPath.getString("data.email"), email);
            assertEquals(jsonPath.getString("message"), "Registrasi berhasil");
            assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
            assertNotNull(jsonPath.getString("timestamp"));
        } catch (Exception e) {
            isOk = false;
        }
    }

    @Test(priority = 10)
    public void testVerifyRegis() {
        Response response;
        request.clear();
        if (isOk) {
            try {
                isOk = false;
                request.put("email", email);
                request.put("otp", otp);

                response = given()
                        .header("Content-Type", "application/json")
                        .header("accept", "*/*")
                        .body(request.toString())
                        .request(Method.POST, "/auth/verify-regis");

                int responseCode = response.getStatusCode();
                JsonPath jsonPath = response.jsonPath();

                System.out.printf("Response body: %s%n", response.getBody().asString());
                if (responseCode == 200) {
                    isOk = true;
                }

                assertEquals(responseCode, 200);
                assertEquals(jsonPath.getString("data"), "");
                assertEquals(jsonPath.getString("message"), "Verifikasi registrasi berhasil");
                assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
                assertNotNull(jsonPath.getString("timestamp"));
            } catch (Exception e) {
                isOk = false;
            }
        } else {
            assertNotNull(null);
        }
    }

    @Test(priority = 20)
    public void testLogin() {
        Response response;
        request.clear();
        if (isOk) {
            try {
                isOk = false;
                request.put("username", username);
                request.put("password", password);

                response = given()
                        .header("Content-Type", "application/json")
                        .header("accept", "*/*")
                        .body(request.toString())
                        .request(Method.POST, "/auth/login");

                int responseCode = response.getStatusCode();
                JsonPath jsonPath = response.jsonPath();
                authToken = jsonPath.getString("data.token");
                System.out.printf("Response body: %s%n", response.getBody().asString());
                if (authToken != null && responseCode == 200) {
                    isOk = true;
                }


                assertEquals(responseCode, 200);
                assertEquals(jsonPath.getString("message"), "Login berhasil");
                assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
                assertNotNull(jsonPath.getString("timestamp"));
            } catch (Exception e) {
                isOk = false;
            }
        } else {
            assertNotNull(null);
        }
    }

    @Test(priority = 30)
    public void testForgotPassword() {
        Response response;
        request.clear();
        if (isOk) {
            try {
                isOk = false;
                request.put("email", email);

                response = given()
                        .header("Content-Type", "application/json")
                        .header("accept", "*/*")
                        .body(request.toString())
                        .request(Method.POST, "/auth/forgot-password");

                int responseCode = response.getStatusCode();
                JsonPath jsonPath = response.jsonPath();
                otp = jsonPath.getString("data.otp");
                System.out.printf("Response body: %s%n", response.getBody().asString());
                if (otp != null && responseCode == 200) {
                    isOk = true;
                }

                assertEquals(responseCode, 200);
                assertEquals(jsonPath.getString("data.email"), email);
                assertEquals(jsonPath.getString("message"), "OTP berhasil dikirim ke email anda");
                assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
                assertNotNull(jsonPath.getString("timestamp"));
            } catch (Exception e) {
                isOk = false;
            }
        } else {
            assertNotNull(null);
        }
    }

    @Test(priority = 40)
    public void testSendOtp() {
        Response response;
        request.clear();
        if (isOk) {
            try {
                isOk = false;
                request.put("email", email);

                response = given()
                        .header("Content-Type", "application/json")
                        .header("accept", "*/*")
                        .body(request.toString())
                        .request(Method.POST, "/auth/send-otp");

                int responseCode = response.getStatusCode();
                JsonPath jsonPath = response.jsonPath();
                otp = jsonPath.getString("data.otp");
                System.out.printf("Response body: %s%n", response.getBody().asString());
                if (otp != null && responseCode == 200) {
                    isOk = true;
                }

                assertEquals(responseCode, 200);
                assertEquals(jsonPath.getString("message"), "OTP berhasil dikirim ke email anda");
                assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
                assertNotNull(jsonPath.getString("timestamp"));
            } catch (Exception e) {
                isOk = false;
            }
        } else {
            assertNotNull(null);
        }
    }

    @Test(priority = 50)
    public void testVerifyForgotPassword() {
        Response response;
        request.clear();
        if (isOk) {
            try {
                isOk = false;
                request.put("email", email);
                request.put("otp", otp);

                response = given()
                        .header("Content-Type", "application/json")
                        .header("accept", "*/*")
                        .body(request.toString())
                        .request(Method.POST, "/auth/verify-forgot-password");

                int responseCode = response.getStatusCode();
                JsonPath jsonPath = response.jsonPath();
                resetPasswordToken = jsonPath.getString("data.token");
                System.out.printf("Response body: %s%n", response.getBody().asString());
                if (resetPasswordToken != null && responseCode == 200) {
                    isOk = true;
                }

                assertEquals(responseCode, 200);
                assertEquals(jsonPath.getString("message"), "Verifikasi OTP berhasil, silahkan gunakan token ini untuk reset password");
                assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
                assertNotNull(jsonPath.getString("timestamp"));
            } catch (Exception e) {
                isOk = false;
            }
        } else {
            assertNotNull(null);
        }
        System.out.println(isOk);
    }

    @Test(priority = 60)
    public void testResetPassword() {
        Response response;
        request.clear();
        if (isOk) {
            try {
                isOk = false;
                newPassword = dataGenerator.dataPassword();

                request.put("email", email);
                request.put("new-password", newPassword);
                request.put("confirm-new-password", newPassword);
                request.put("token", resetPasswordToken);

                response = given()
                        .header("Content-Type", "application/json")
                        .header("accept", "*/*")
                        .body(request.toString())
                        .request(Method.POST, "/auth/reset-password");

                int responseCode = response.getStatusCode();
                JsonPath jsonPath = response.jsonPath();
                System.out.printf("Response body: %s%n", response.getBody().asString());
                if (responseCode == 200) {
                    isOk = true;
                }

                assertEquals(responseCode, 200);
                assertEquals(jsonPath.getString("message"), "Reset password berhasil");
                assertTrue(Boolean.parseBoolean(jsonPath.getString("success")));
                assertNotNull(jsonPath.getString("timestamp"));
            } catch (Exception e) {
                isOk = false;
            }
        } else {
            assertNotNull(null);
        }
    }
}