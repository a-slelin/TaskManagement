package a.slelin.work.task.management.api.test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;

@ActiveProfiles("test")
@DisplayName("Тестируем получение ресурсов (img, css, js)")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ResourceTest {

    public static String IMAGES_FOLDER_NAME = "images";

    @SuppressWarnings("unused")
    public static String CSS_FOLDER_NAME = "css";

    @SuppressWarnings("unused")
    public static String JS_FOLDER_NAME = "js";

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
    @DisplayName("Тестируем получение картинки логотипа - logo.jpg")
    public void testLogo() {

        String image = "logo.jpg";

        ResponseEntity<byte[]> response = rest.exchange(
                baseUrl + "/{folder}/{image}",
                HttpMethod.GET,
                null,
                byte[].class,
                IMAGES_FOLDER_NAME,
                image
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        byte[] bytes = response.getBody();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        ResponseEntity<byte[]> response2 = rest.exchange(
                baseUrl + "/{folder}/{image}/",
                HttpMethod.GET,
                null,
                byte[].class,
                IMAGES_FOLDER_NAME,
                image
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        byte[] bytes2 = response2.getBody();
        assertNotNull(bytes2);
        assertTrue(bytes2.length > 0);
    }

    @Test
    @DisplayName("Тестируем получение картинки иконки - icon.svg")
    public void testIcon() {

        String image = "icon.svg";

        ResponseEntity<byte[]> response = rest.exchange(
                baseUrl + "/{folder}/{image}",
                HttpMethod.GET,
                null,
                byte[].class,
                IMAGES_FOLDER_NAME,
                image
        );
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        byte[] bytes = response.getBody();
        assertNotNull(bytes);
        assertTrue(bytes.length > 0);

        ResponseEntity<byte[]> response2 = rest.exchange(
                baseUrl + "/{folder}/{image}/",
                HttpMethod.GET,
                null,
                byte[].class,
                IMAGES_FOLDER_NAME,
                image
        );
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        byte[] bytes2 = response2.getBody();
        assertNotNull(bytes2);
        assertTrue(bytes2.length > 0);
    }
}