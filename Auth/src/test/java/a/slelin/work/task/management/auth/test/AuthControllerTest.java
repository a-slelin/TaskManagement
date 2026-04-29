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
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
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
    public void testAccessAndRefreshTokensByUsername() {
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
    @DisplayName("Получаем токены доступа и обновления по телефону")
    public void testAccessAndRefreshTokensByPhone() {
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
    @DisplayName("Получаем токены доступа и обновления по электронной почте")
    public void testAccessAndRefreshTokensByEmail() {
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
    public void testAccessAndRefreshTokensByUsernameWithWrongPassword() {
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
    @DisplayName("Получаем ошибку не найдено c неправильным именем пользователя")
    public void testAccessAndRefreshTokensByUsernameWithWrongUsername() {
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
    @DisplayName("Регистрируем пользователя и получаем токены")
    public void testRegisterUser() {
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
    @Order(7)
    @DirtiesContext
    @DisplayName("Регистрируем пользователя, а затем логинимся")
    public void testRegisterUserAndThenLogin() {
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
    @Order(8)
    @DirtiesContext
    @DisplayName("Логинимся, после чего пытаемся обновить токен")
    public void testRefreshToken() {
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
}
