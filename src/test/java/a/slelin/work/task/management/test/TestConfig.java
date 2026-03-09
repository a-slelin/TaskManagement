package a.slelin.work.task.management.test;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

@ActiveProfiles("test")
@Configuration
public class TestConfig {

    public static final String BASE_URL = "http://localhost:8080";

    public static final String API_URL = BASE_URL + "/api";

    public static final String TASK_URL = API_URL + "/tasks";

    public static final String PROJECT_URL = API_URL + "/projects";

    public static final String USER_URL = API_URL + "/users";

    @Bean
    public RestTemplate restTemplate() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }
}
