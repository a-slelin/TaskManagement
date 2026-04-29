package a.slelin.work.task.management.e2e.test;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties
@SuppressWarnings("ConfigurationProperties")
public record ApplicationHolder(@Value("${postgres.image}") String postgresImage,

                                @Value("${api.image}") String apiImage,
                                @Value("${api.log}") String apiLog,
                                @Value("${api.port}") Integer apiPort,
                                @Value("${api.db.host}") String apiDbHost,
                                @Value("${api.db.port}") Integer apiDbPort,
                                @Value("${api.db.name}") String apiDbName,
                                @Value("${api.db.user.name}") String apiDbUserName,
                                @Value("${api.db.user.password}") String apiDbUserPassword,

                                @Value("${auth.image}") String authImage,
                                @Value("${auth.log}") String authLog,
                                @Value("${auth.port}") Integer authPort,
                                @Value("${auth.db.host}") String authDbHost,
                                @Value("${auth.db.port}") Integer authDbPort,
                                @Value("${auth.db.name}") String authDbName,
                                @Value("${auth.db.user.name}") String authDbUserName,
                                @Value("${auth.db.user.password}") String authDbUserPassword,

                                @Value("${jwt.secret}") String jwtSecret,
                                @Value("${jwt.access-expiration}") String jwtAccessExpiration,
                                @Value("${jwt.refresh-expiration}") String jwtRefreshExpiration) {
}
