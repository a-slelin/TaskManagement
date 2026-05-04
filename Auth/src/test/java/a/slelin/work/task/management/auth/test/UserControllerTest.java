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
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера, отвечающего за пользователей и их аккаунты")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
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
    @DisplayName("Не авторизованный пользователь не может обращаться ни к какому аккаунту")
    public void test1() {
        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    apiUrl,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Пользователь может обращаться к своему аккаунту")
    public void test2() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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
        headers.setBearerAuth(jwtResponse.accessToken());

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                });
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
    @DisplayName("Пользователь может изменять данные своего аккаунта")
    public void test3() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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
        headers.setBearerAuth(jwtResponse.accessToken());

        UserWD updUser = UserWD.builder()
                .gender("female")
                .phone("+78964563434")
                .password("new_password")
                .build();

        ResponseEntity<UserRD> response2 = rest.exchange(
                apiUrl,
                HttpMethod.PATCH,
                new HttpEntity<>(updUser, headers),
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        UserRD user = response2.getBody();
        assertNotNull(user);
        assertNotNull(user.id());
        assertNotNull(user.username());
        assertEquals("alex_petrov", user.username());
        assertNotNull(user.gender());
        assertEquals(updUser.gender(), user.gender());
        assertNotNull(user.phone());
        assertEquals(updUser.phone(), user.phone());

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(login2),
                    JwtResponse.class);
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }

        LoginRequest login3 = new LoginRequest("alex_petrov", "new_password");

        ResponseEntity<JwtResponse> response3 = rest.exchange(
                authUrl,
                HttpMethod.POST,
                new HttpEntity<>(login3),
                JwtResponse.class);
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        JwtResponse jwtResponse3 = response.getBody();
        assertNotNull(jwtResponse3);
        assertNotNull(jwtResponse3.accessToken());
        assertNotNull(jwtResponse3.refreshToken());

        HttpHeaders headers3 = new HttpHeaders();
        headers3.setBearerAuth(jwtResponse3.accessToken());

        ResponseEntity<UserRD> response4 = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                new HttpEntity<>(headers3),
                new ParameterizedTypeReference<>() {
                });
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
    @Order(4)
    @DirtiesContext
    @DisplayName("Пользователь может удалить свой аккаунт")
    public void test4() {
        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                authUrl,
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
        headers.setBearerAuth(jwtResponse.accessToken());

        ResponseEntity<Void> response2 = rest.exchange(
                apiUrl,
                HttpMethod.DELETE,
                new HttpEntity<>(headers),
                Void.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.NO_CONTENT, response2.getStatusCode());

        LoginRequest login2 = new LoginRequest("alex_petrov", "password");

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    authUrl,
                    HttpMethod.POST,
                    new HttpEntity<>(login2),
                    JwtResponse.class);
            fail("Should throw HttpClientErrorException.Unauthorized");

        } catch (HttpClientErrorException.Unauthorized e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.UNAUTHORIZED, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Unauthorized, but got " + e.getMessage());
        }
    }

}
