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
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера, отвечающего за аутентификацию")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    @DisplayName("Получаем токены доступа и обновления по имени пользователя в системе")
    public void test1() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
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
    @DisplayName("Получаем токены доступа и обновления по телефону пользователя")
    public void test2() {
        LoginRequest login = new LoginRequest("+79051234567", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
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
    @DisplayName("Получаем токены доступа и обновления по электронной почте пользователя")
    public void test3() {
        LoginRequest login = new LoginRequest("alex.petrov@google.com", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
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
    @DisplayName("Получаем ошибку c неправильным паролем")
    public void test4() {
        LoginRequest login = new LoginRequest("alex_petrov", "wrong_password");

        try {
            rest.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    new HttpEntity<>(login),
                    JwtResponse.class);
            fail("Should throw HttpClientErrorException.Conflict");
        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse response = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(response);
        }
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Получаем ошибку c неправильным именем пользователя")
    public void test5() {
        LoginRequest login = new LoginRequest("wrong_username", "password");

        try {
            rest.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    new HttpEntity<>(login),
                    JwtResponse.class);
            fail("Should throw HttpClientErrorException.Conflict");
        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse response = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(response);
        }
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Можем залогинится несколько раз (3)")
    public void test6() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse2.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse2.refreshToken());

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
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Регистрируем пользователя и получаем токены")
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
                JwtResponse.class);
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
    @DisplayName("Регистрируем пользователя, а затем логинимся")
    public void test8() {
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
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        LoginRequest login = new LoginRequest("alex_slelin", "wrong_password");

        try {
            rest.exchange(
                    baseUrl + "/login",
                    HttpMethod.POST,
                    new HttpEntity<>(login),
                    JwtResponse.class);
            fail("Should throw HttpClientErrorException.Conflict");
        } catch (HttpClientErrorException.Conflict e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.CONFLICT, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
        }

        login = new LoginRequest("alex_slelin", "hard_password");
        response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
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
    @DisplayName("Логинимся, после чего пытаемся обновить токен")
    public void test9() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.refreshToken());

        response = rest.exchange(
                baseUrl + "/refresh",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                JwtResponse.class);
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
    @DisplayName("Логинимся, после чего выходим")
    public void test10() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response2 = rest.exchange(
                baseUrl + "/logout",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Void.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Логинимся 3 раза, а затем выходим из всех сессий")
    public void test11() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        JwtResponse jwtResponse = response.getBody();
        assertNotNull(jwtResponse);
        assertNotNull(jwtResponse.accessToken());
        assertNotNull(jwtResponse.refreshToken());

        ResponseEntity<JwtResponse> response2 = rest.exchange(
                baseUrl + "/login",
                HttpMethod.POST,
                new HttpEntity<>(login),
                JwtResponse.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        JwtResponse jwtResponse2 = response2.getBody();
        assertNotNull(jwtResponse2);
        assertNotNull(jwtResponse2.accessToken());
        assertNotNull(jwtResponse2.refreshToken());

        assertNotEquals(jwtResponse.accessToken(), jwtResponse2.accessToken());
        assertNotEquals(jwtResponse.refreshToken(), jwtResponse2.refreshToken());

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

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(jwtResponse.refreshToken());

        ResponseEntity<Void> response4 = rest.exchange(
                baseUrl + "/logout/all",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                Void.class);
        assertNotNull(response4);
        assertNotNull(response4.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response4.getStatusCode());
    }
}
