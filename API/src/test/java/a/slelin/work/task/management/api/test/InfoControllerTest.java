package a.slelin.work.task.management.api.test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DisplayName("Тест информационного контроллера")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InfoControllerTest {

    @Autowired
    private RestTemplate rest;

    @LocalServerPort
    private int port;

    private String baseUrl;

    private String apiUrl;

    @BeforeEach
    void beforeEach() {
        baseUrl = "http://localhost:%d".formatted(port);
        apiUrl = baseUrl + "/api";
    }

    @Test
    @Order(1)
    @DisplayName("Тестируем путь */help")
    public void testPath3() {
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
    }

    @Test
    @Order(2)
    @DisplayName("Тестируем путь */help")
    public void testPath4() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/help/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(3)
    @DisplayName("Тестируем путь */info")
    public void testPath5() {
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
    }

    @Test
    @Order(4)
    @DisplayName("Тестируем путь */info/")
    public void testPath6() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                baseUrl + "/info/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(5)
    @DisplayName("Тестируем путь */api")
    public void testApiPath() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                apiUrl,
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(6)
    @DisplayName("Тестируем путь */api/")
    public void testApiPath2() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                apiUrl + "/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(7)
    @DisplayName("Тестируем путь */api/info")
    public void testApiInfoPath() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                apiUrl + "/info",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(8)
    @DisplayName("Тестируем путь */api/info/")
    public void testApiInfoPath2() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                apiUrl + "/info/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(9)
    @DisplayName("Тестируем путь */api/help")
    public void testApiHelpPath() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                apiUrl + "/help",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }

    @Test
    @Order(10)
    @DisplayName("Тестируем путь */api/help/")
    public void testApiHelpPath2() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                apiUrl + "/help/",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {
                });
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        Map<String, Object> body = response.getBody();
        assertNotNull(body);
    }
}