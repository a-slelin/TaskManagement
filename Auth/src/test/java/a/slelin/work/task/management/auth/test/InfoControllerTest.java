package a.slelin.work.task.management.auth.test;

import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
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

import java.util.Map;

import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("Тест контроллера, отвечающего за базовую информацию")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfoControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
    }

    @Test
    @Order(1)
    @DirtiesContext
    @DisplayName("Неавторизованный пользователь может обращаться к информации")
    public void test1() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    @Order(2)
    @DirtiesContext
    @DisplayName("Пользователь может обращаться к информации")
    public void test2() {

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
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

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/help",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    @Order(3)
    @DirtiesContext
    @DisplayName("Администратор может обращаться к информации")
    public void test3() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
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

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/help",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());
    }

    @Test
    @Order(4)
    @DirtiesContext
    @DisplayName("Тестируем /help")
    public void test4() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/help/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем /info")
    public void test5() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/info/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем /api")
    public void test6() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/api",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем /api/help")
    public void test7() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/api/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/help/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем /api/info")
    public void test8() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/api/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/info/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(9)
    @DirtiesContext
    @DisplayName("Тестируем /auth")
    public void test9() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/auth",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/auth/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(10)
    @DirtiesContext
    @DisplayName("Тестируем /auth/help")
    public void test10() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/auth/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/auth/help/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(11)
    @DirtiesContext
    @DisplayName("Тестируем /auth/info")
    public void test11() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/auth/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> map = response.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/auth/info/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map2 = response2.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(12)
    @DirtiesContext
    @DisplayName("Тестируем /api/admin")
    public void test12() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
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

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/admin",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map = response2.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response3 = rest.exchange(
                baseUrl + "/api/admin/",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        Map<String, Object> map2 = response3.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(13)
    @DirtiesContext
    @DisplayName("Тестируем /api/admin/help")
    public void test13() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
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

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/admin/help",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map = response2.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response3 = rest.exchange(
                baseUrl + "/api/admin/help/",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        Map<String, Object> map2 = response3.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(14)
    @DirtiesContext
    @DisplayName("Тестируем /api/admin/info")
    public void test14() {

        LoginRequest login = new LoginRequest("admin", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
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

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/admin/info",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> map = response2.getBody();
        assertNotNull(map);
        assertFalse(map.isEmpty());

        ResponseEntity<Map<String, Object>> response3 = rest.exchange(
                baseUrl + "/api/admin/info/",
                HttpMethod.GET,
                new HttpEntity<>(headers),
                new ParameterizedTypeReference<>() {
                }
        );
        assertNotNull(response3);
        assertNotNull(response3.getStatusCode());
        assertEquals(HttpStatus.OK, response3.getStatusCode());

        Map<String, Object> map2 = response3.getBody();
        assertNotNull(map2);
        assertFalse(map2.isEmpty());
    }

    @Test
    @Order(15)
    @DirtiesContext
    @DisplayName("Неавторизованный пользователь не имеет доступ к админской информации")
    public void test15() {

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/api/admin",
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
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/api/admin", errorResponse.path());
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
    @Order(16)
    @DirtiesContext
    @DisplayName("Обычный пользователь не имеет доступ к админской информации")
    public void test16() {

        LoginRequest login = new LoginRequest("alex_petrov", "password");

        ResponseEntity<JwtResponse> response = rest.exchange(
                baseUrl + "/auth/login",
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

        //noinspection CatchMayIgnoreException
        try {
            rest.exchange(
                    baseUrl + "/api/admin",
                    HttpMethod.GET,
                    new HttpEntity<>(headers),
                    new ParameterizedTypeReference<>() {
                    }
            );
            fail("Should throw HttpClientErrorException.Forbidden");

        } catch (HttpClientErrorException.Forbidden e) {
            assertNotNull(e);
            assertNotNull(e.getStatusCode());
            assertEquals(HttpStatus.FORBIDDEN, e.getStatusCode());

            ErrorResponse errorResponse = e.getResponseBodyAs(ErrorResponse.class);
            assertNotNull(errorResponse);
            assertNotNull(errorResponse.path());
            assertEquals(baseUrl + "/api/admin", errorResponse.path());
            assertNotNull(errorResponse.httpMethod());
            assertEquals(HttpMethod.GET, errorResponse.httpMethod());
            assertNotNull(errorResponse.httpStatus());
            assertEquals(HttpStatus.FORBIDDEN, errorResponse.httpStatus());
            assertNotNull(errorResponse.message());
            assertNotNull(errorResponse.exception());
            assertNotNull(errorResponse.timestamp());

        } catch (Exception e) {
            fail("Should throw HttpClientErrorException.Forbidden, but got " + e.getMessage());
        }
    }
}
