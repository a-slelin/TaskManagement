package a.slelin.work.task.management.api.test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DisplayName("Тест информационного контроллера")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
public class InfoControllerTest {

    @Autowired
    private RestTemplate rest;

    @Autowired
    @Qualifier("alexToken")
    private String alexToken;

    @Autowired
    @Qualifier("adminToken")
    private String adminToken;

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
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(alexToken);

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
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(adminToken);

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
    @DisplayName("Тестируем путь /help")
    public void test4() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/help/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> body2 = response2.getBody();
        assertNotNull(body2);
    }

    @Test
    @Order(5)
    @DirtiesContext
    @DisplayName("Тестируем путь /info")
    public void test5() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/info/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> body2 = response2.getBody();
        assertNotNull(body2);
    }

    @Test
    @Order(6)
    @DirtiesContext
    @DisplayName("Тестируем путь /api")
    public void test6() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/api",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> body2 = response2.getBody();
        assertNotNull(body2);
    }

    @Test
    @Order(7)
    @DirtiesContext
    @DisplayName("Тестируем путь /api/help")
    public void test7() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/api/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> body2 = response2.getBody();
        assertNotNull(body2);
    }

    @Test
    @Order(8)
    @DirtiesContext
    @DisplayName("Тестируем путь /api/info")
    public void test8() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/api/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);

        ResponseEntity<Map<String, Object>> response2 = rest.exchange(
                baseUrl + "/api/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        Map<String, Object> body2 = response2.getBody();
        assertNotNull(body2);
    }
}