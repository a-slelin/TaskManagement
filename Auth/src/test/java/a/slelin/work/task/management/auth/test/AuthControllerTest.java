package a.slelin.work.task.management.auth.test;

import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import a.slelin.work.task.management.core.exception.ErrorResponse;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тестируем AuthController")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class AuthControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d/auth".formatted(port);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/login по имени пользователя : успех")
    public void test1() {

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/login по телефону пользователя : успех")
    public void test2() {

        LoginRequest login = new LoginRequest("+79051234567", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/login по электронной почте пользователя : успех")
    public void test3() {

        LoginRequest login = new LoginRequest("alex.petrov@google.com", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/login с неправильным паролем : ошибка 401 неавторизован")
    public void test4() {

        LoginRequest login = new LoginRequest("alex_petrov", "wrong_password");

        try {
            rest.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    new HttpEntity<>(login),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/login", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/login с неправильным именем : ошибка 401 неавторизован")
    public void test5() {

        LoginRequest login = new LoginRequest("wrong_username", "password");

        try {
            rest.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    new HttpEntity<>(login),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/login", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/login несколько раз логинимся : успех")
    public void test6() {

        /*
         * Логинимся под пользователем алекс 1‑й раз.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Логинимся под пользователем алекс 2‑й раз.
         * */

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse2.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse2.refreshToken());

        /*
         * Логинимся под пользователем алекс 3‑й раз.
         * */

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response3.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse3.accessToken());
        assertNotEquals(jwtResponse2.accessToken(), jwtResponse3.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse3.refreshToken());
        assertNotEquals(jwtResponse2.refreshToken(), jwtResponse3.refreshToken());
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/register : успех")
    public void test7() {

        UserWD newUser = UserWD.builder()
                .username("alex_slelin")
                .password("password")
                .gender("male")
                .phone("+79864532345")
                .email("a.slelin.work@mail.ru")
                .build();

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/register",
                HttpMethod.POST,
                new HttpEntity<>(newUser),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем POST /auth/register + POST /auth/login : успех")
    public void test8() {

        /*
         * Регистрируем пользователя.
         * */

        UserWD newUser = UserWD.builder()
                .username("alex_slelin")
                .password("hard_password")
                .gender("male")
                .phone("+79864532345")
                .email("a.slelin.work@mail.ru")
                .build();

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/register",
                HttpMethod.POST,
                new HttpEntity<>(newUser),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Пытаемся залогиниться с неправильным паролем.
         * */

        LoginRequest login = new LoginRequest("alex_slelin", "wrong_password");

        try {
            rest.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    new HttpEntity<>(login),
                    JwtResponse.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");
        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/login", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.POST, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.UNAUTHORIZED, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());
        }


        /*
         * Логинимся с правильным паролем.
         * */

        login = new LoginRequest("alex_slelin", "hard_password");
        response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse2 = response.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse2.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse2.refreshToken());
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем GET /auth/refresh : успех")
    public void test9() {

        /*
         * Логинимся как пользователь алекс.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Обновляем токен.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.refreshToken());

        response = rest.exchange(
                baseUrl + "/refresh",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse2 = response.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse2.accessToken());
        assertEquals(jwtResponse.refreshToken(), jwtResponse2.refreshToken());
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем GET /auth/logout : успех")
    public void test10() {

        /*
         * Логинимся под пользователем алекс.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Выходи из системы.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response2 = rest.exchange(
                baseUrl + "/logout",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Void.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем GET /auth/logout/all : успех")
    public void test11() {

        /*
         * Логинимся под пользователем алекс.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        /*
         * Логинимся 2‑й раз.
         * */

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse2.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse2.refreshToken());

        /*
         * Логинимся 3‑й раз.
         * */

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response3.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse3.accessToken());
        assertNotEquals(jwtResponse2.accessToken(), jwtResponse3.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse3.refreshToken());
        assertNotEquals(jwtResponse2.refreshToken(), jwtResponse3.refreshToken());

        /*
         * Выходим из всех сессий.
         * */

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response4 = rest.exchange(
                baseUrl + "/logout/all",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Void.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());
    }
}
