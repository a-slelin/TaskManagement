package a.slelin.work.task.management.auth.util;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "jwt")
public record JwtHolder(@NotBlank @Size(min = 88) String secret,
                        @NotNull @Min(60000) @DefaultValue("900000") Long accessExpiration,
                        @NotNull @Min(900000) @DefaultValue("86400000") Long refreshExpiration) {
}
