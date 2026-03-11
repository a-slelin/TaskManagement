package a.slelin.work.task.management.test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

import static a.slelin.work.task.management.test.TestConfig.API_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DisplayName("Тест информационного контроллера")
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class InfoControllerTest {

    @Autowired
    private RestTemplate rest;

    @Test
    @Order(1)
    @DisplayName("Тестируем путь */api...")
    public void testApiPath() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                API_URL,
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
    @DisplayName("Тестируем путь */api/...")
    public void testApiPath2() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                API_URL + "/",
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
    @DisplayName("Тестируем путь */api/info...")
    public void testApiInfoPath() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                API_URL + "/info",
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
    @DisplayName("Тестируем путь */api/info/...")
    public void testApiInfoPath2() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                API_URL + "/info/",
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
    @DisplayName("Тестируем путь */api/help...")
    public void testApiHelpPath() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                API_URL + "/help",
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
    @DisplayName("Тестируем путь */api/help/...")
    public void testApiHelpPath2() {
        ResponseEntity<Map<String, Object>> response = rest.exchange(
                API_URL + "/help/",
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
