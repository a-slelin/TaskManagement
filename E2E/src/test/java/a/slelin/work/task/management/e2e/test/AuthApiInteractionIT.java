package a.slelin.work.task.management.e2e.test;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.postgresql.PostgreSQLContainer;

@SpringBootTest(classes = Config.class)
@TestClassOrder(ClassOrderer.OrderAnnotation.class)
@DisplayName("Тестируем взаимодействие микросервисов auth & api")
public class AuthApiInteractionIT {

    @Autowired
    private ApplicationHolder holder;

    @Autowired
    private RestTemplate rest;

    @Autowired
    @Qualifier("apiDb")
    private PostgreSQLContainer apiDb;

    @Autowired
    @Qualifier("api")
    private GenericContainer<?> api;

    @Autowired
    @Qualifier("authDb")
    private PostgreSQLContainer authDb;

    @Autowired
    @Qualifier("auth")
    private GenericContainer<?> auth;

    private String apiUrl;

    private String authUrl;

    @BeforeEach
    @SuppressWarnings("HttpUrlsUsage")
    void setup() {
        apiDb.start();
        api.start();
        authDb.start();
        auth.start();

        apiUrl = "http://%s:%d".formatted(api.getHost(), api.getMappedPort(holder.apiPort()));
        authUrl = "http://%s:%d".formatted(auth.getHost(), auth.getMappedPort(holder.authPort()));
    }

    @AfterEach
    void teardown() {
        api.stop();
        apiDb.stop();
        auth.stop();
        authDb.stop();
    }

    @Test
    @Order(1)
    @DisplayName("Тест для api")
    public void testSetup() {
        String apiHtml = rest.getForObject(apiUrl, String.class);
        System.out.println("API HTML: " + apiHtml);
    }

    @Test
    @Order(2)
    @DisplayName("Тест для auth")
    public void testAuth() {
        String authHtml = rest.getForObject(authUrl, String.class);
        System.out.println("AUTH HTML: " + authHtml);
    }
}
