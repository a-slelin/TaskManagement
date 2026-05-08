package a.slelin.work.task.management.e2e.test;

import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.postgresql.PostgreSQLContainer;

@Configuration
@ComponentScan
public class Config {

    @Autowired
    private ApplicationHolder holder;

    @Bean
    public RestTemplate restTemplate() {
        CloseableHttpClient httpClient = HttpClients.custom().build();
        HttpComponentsClientHttpRequestFactory factory =
                new HttpComponentsClientHttpRequestFactory(httpClient);
        return new RestTemplate(factory);
    }

    @Bean
    public Network apiNet() {
        return Network.newNetwork();
    }

    @Bean
    public Network authNet() {
        return Network.newNetwork();
    }

    @Bean
    @SuppressWarnings("resource")
    public PostgreSQLContainer apiDb() {
        return new PostgreSQLContainer(holder.postgresImage())
                .withNetwork(apiNet())
                .withNetworkAliases(holder.apiDbHost())
                .withExposedPorts(holder.apiDbPort())
                .withDatabaseName(holder.apiDbName())
                .withUsername(holder.apiDbUserName())
                .withPassword(holder.apiDbUserPassword());
    }

    @Bean
    @SuppressWarnings("resource")
    public PostgreSQLContainer authDb() {
        return new PostgreSQLContainer(holder.postgresImage())
                .withNetwork(authNet())
                .withNetworkAliases(holder.authDbHost())
                .withExposedPorts(holder.authDbPort())
                .withDatabaseName(holder.authDbName())
                .withUsername(holder.authDbUserName())
                .withPassword(holder.authDbUserPassword());
    }

    @Bean
    @SuppressWarnings("resource")
    public GenericContainer<?> api() {
        return new GenericContainer<>(holder.apiImage())
                .withNetwork(apiNet())
                .withExposedPorts(holder.apiPort())
                .withEnv("DB_HOST", holder.apiDbHost())
                .withEnv("DB_PORT", holder.apiDbPort().toString())
                .withEnv("DB_NAME", holder.apiDbName())
                .withEnv("DB_USER_NAME", holder.apiDbUserName())
                .withEnv("DB_USER_PASSWORD", holder.apiDbUserPassword())
                .withEnv("APP_PORT", holder.apiPort().toString())
                .withEnv("JWT_SECRET", holder.jwtSecret())
                .withEnv("LOG_PATH", holder.apiLog());
    }

    @Bean
    @SuppressWarnings("resource")
    public GenericContainer<?> auth() {
        return new GenericContainer<>(holder.authImage())
                .withNetwork(authNet())
                .withExposedPorts(holder.authPort())
                .withEnv("DB_HOST", holder.authDbHost())
                .withEnv("DB_PORT", holder.authDbPort().toString())
                .withEnv("DB_NAME", holder.authDbName())
                .withEnv("DB_USER_NAME", holder.authDbUserName())
                .withEnv("DB_USER_PASSWORD", holder.authDbUserPassword())
                .withEnv("APP_PORT", holder.authPort().toString())
                .withEnv("JWT_SECRET", holder.jwtSecret())
                .withEnv("JWT_ACCESS_EXPIRATION", holder.jwtAccessExpiration())
                .withEnv("JWT_REFRESH_EXPIRATION", holder.jwtRefreshExpiration())
                .withEnv("LOG_PATH", holder.authLog());
    }
}
