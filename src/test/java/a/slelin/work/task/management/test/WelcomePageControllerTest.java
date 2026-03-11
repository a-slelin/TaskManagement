package a.slelin.work.task.management.test;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import static a.slelin.work.task.management.test.TestConfig.BASE_URL;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ActiveProfiles("test")
@DisplayName("Тест приветственной страницы")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class WelcomePageControllerTest {

    @Autowired
    private RestTemplate rest;

    @Test
    @DisplayName("Открываем стартовую страницу...")
    public void testWelcomePage() {
        ResponseEntity<String> response = rest.exchange(
                BASE_URL,
                HttpMethod.GET,
                null,
                String.class);
        assertNotNull(response);
        assertNotNull(response.getStatusCode());
        assertEquals(HttpStatus.OK, response.getStatusCode());

        String body = response.getBody();
        assertNotNull(body);

        ResponseEntity<String> response2 = rest.exchange(
                BASE_URL + "/",
                HttpMethod.GET,
                null,
                String.class);
        assertNotNull(response2);
        assertNotNull(response2.getStatusCode());
        assertEquals(HttpStatus.OK, response2.getStatusCode());

        String body2 = response2.getBody();
        assertNotNull(body2);
    }
}
