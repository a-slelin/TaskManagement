package a.slelin.work.task.management.auth.test;

import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserRD;
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

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DisplayName("Тестируем UserController")
@SuppressWarnings("CatchMayIgnoreException")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class UserControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String authUrl;

    private String apiUrl;

    @BeforeEach
    void beforeEach() {
        authUrl = "http://localhost:%d/auth/login".formatted(port);
        apiUrl = "http://localhost:%d/api/user".formatted(port);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/user с неавторизованным пользователем : ошибка 401 неавторизован")
    public void test1() {

        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    UserWD.class
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
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
    @Order(2)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/user с авторизованным пользователем : успех")
    public void test2() {

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем свой аккаунт.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        assertNotNull(user.id());
        assertNotNull(user.username());
        assertEquals("alex_petrov", user.username());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Тестируем GET /api/user с администратором : успех")
    public void test3() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Получаем свой аккаунт.
         * */

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        assertNotNull(user.id());
        assertNotNull(user.username());
        assertEquals("admin", user.username());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем PATСН /api/user c авторизованным пользователем : успех")
    public void test4() {

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Изменяем аккаунт.
         * */

        UserWD updUser = UserWD.builder()
                .gender("female")
                .phone("+78964563434")
                .password("new_password")
                .build();

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.PATCH,
                new HttpEntity<>(updUser, headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        assertNotNull(user.id());
        assertNotNull(user.username());
        assertEquals("alex_petrov", user.username());
        assertNotNull(user.gender());
        assertEquals(updUser.gender(), user.gender());
        assertNotNull(user.phone());
        assertEquals(updUser.phone(), user.phone());

        /*
         * Проверяем, что пароль поменялся.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        try {
            rest.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(login2),
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
            assertEquals(authUrl, errorResponse.path());
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

        /*
         * Проверяем, что можем войти с новым паролем.
         * */

        LoginRequest login3 = new LoginRequest("alex_petrov", "new_password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                authUrl,
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response3.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.accessToken());

        /*
         * Проверяем, сам аккаунт.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user4 = response4.getBody();
        assertNotNull(user4);
        assertNotNull(user4.id());
        assertNotNull(user4.username());
        assertEquals("alex_petrov", user4.username());
        assertNotNull(user4.gender());
        assertEquals(updUser.gender(), user4.gender());
        assertNotNull(user4.phone());
        assertEquals(updUser.phone(), user4.phone());
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/user с администратором : успех")
    public void test5() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Изменяем аккаунт.
         * */

        UserWD updUser = UserWD.builder()
                .gender("female")
                .phone("+78964563434")
                .password("new_password")
                .build();

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.PATCH,
                new HttpEntity<>(updUser, headers),
                UserRD.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        assertNotNull(user.id());
        assertNotNull(user.username());
        assertEquals("admin", user.username());
        assertNotNull(user.gender());
        assertEquals(updUser.gender(), user.gender());
        assertNotNull(user.phone());
        assertEquals(updUser.phone(), user.phone());

        /*
         * Проверяем, что пароль поменялся.
         * */

        LoginRequest login2 = new LoginRequest("admin", "password");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(login2),
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
            assertEquals(authUrl, errorResponse.path());
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

        /*
         * Проверяем, что можем войти с новым паролем.
         * */

        LoginRequest login3 = new LoginRequest("admin", "new_password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                authUrl,
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response3.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.accessToken());

        /*
         * Проверяем, сам аккаунт.
         * */

        ResponseEntity<UserRD> response4 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                UserRD.class
        );
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.OK, response4.getStatusCode());

        UserRD user4 = response4.getBody();
        assertNotNull(user4);
        assertNotNull(user4.id());
        assertNotNull(user4.username());
        assertEquals("admin", user4.username());
        assertNotNull(user4.gender());
        assertEquals(updUser.gender(), user4.gender());
        assertNotNull(user4.phone());
        assertEquals(updUser.phone(), user4.phone());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/user с неуникальным именем : ошибка 409 конфликт")
    public void test6() {

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Изменяем имя на уже существующее.
         * */

        UserWD updUser = UserWD.builder()
                .username("ekaterina_smirnova")
                .build();

        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.PATCH,
                    new HttpEntity<>(updUser, headers),
                    UserRD.class
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.CONFLICT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Conflict, but got " + e.getMessage());
        }
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/user с неуникальным номером телефона : ошибка 409 конфликт")
    public void test7() {

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Изменяем телефон на уже существующий.
         * */

        UserWD updUser = UserWD.builder()
                .phone("+79054567890")
                .build();

        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.PATCH,
                    new HttpEntity<>(updUser, headers),
                    UserRD.class
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.CONFLICT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Conflict, but got " + e.getMessage());
        }
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем PATCH /api/user с неуникальной электронной почтой : ошибка 409 конфликт")
    public void test8() {

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Изменяем почту на уже существующий.
         * */

        UserWD updUser = UserWD.builder()
                .email("katya.s@mail.ru")
                .build();

        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.PATCH,
                    new HttpEntity<>(updUser, headers),
                    UserRD.class
            );
            fail("Should throw HttpClientErrorException.Conflict");

        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(apiUrl, errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.PATCH, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.CONFLICT, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Conflict, but got " + e.getMessage());
        }
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/user с авторизованным пользователем : успех")
    public void test9() {

        /*
         * Логинимся под пользователем.
         * */

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Удаляем аккаунт.
         * */

        ResponseEntity<Void> response2 = rest.exchange(
                apiUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());

        /*
         * Проверяем, что больше не можем залогиниться.
         * */

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        try {
            rest.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(login2),
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
            assertEquals(authUrl, errorResponse.path());
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
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем DELETE /api/user с администратором : успех")
    public void test10() {

        /*
         * Логинимся под администратором.
         * */

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.accessToken());

        /*
         * Удаляем аккаунт.
         * */

        ResponseEntity<Void> response2 = rest.exchange(
                apiUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());

        /*
         * Проверяем, что больше не можем залогиниться.
         * */

        LoginRequest login2 = new LoginRequest("admin", "password");

        try {
            rest.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(login2),
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
            assertEquals(authUrl, errorResponse.path());
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
}
