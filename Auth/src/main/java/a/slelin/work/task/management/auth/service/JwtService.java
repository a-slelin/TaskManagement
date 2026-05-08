package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.auth.util.JwtHolder;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.security.Key;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Validated
@RequiredArgsConstructor
public class JwtService {

    private final JwtHolder jwtHolder;

    public String generateAccessToken(@NotNull @Valid User user) {
        List<String> roles = user.getRoles().stream()
                .map(Role::getName)
                .toList();

        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .claim("roles", roles)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtHolder.accessExpiration()))
                .signWith(getSignKey())
                .compact();
    }

    public String generateRefreshToken(@NotNull @Valid User user) {
        return Jwts.builder()
                .id(UUID.randomUUID().toString())
                .subject(user.getId().toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtHolder.refreshExpiration()))
                .signWith(getSignKey())
                .compact();
    }

    private Key getSignKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtHolder.secret());
        return Keys.hmacShaKeyFor(keyBytes);
    }

    @SuppressWarnings("deprecation")
    private Claims extractClaims(String token) {
        return Jwts.parser()
                .setSigningKey(getSignKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @SuppressWarnings("unused")
    public UUID extractUserId(@NotBlank String token) {
        Claims claims = extractClaims(token);
        return UUID.fromString(claims.getSubject());
    }

    @SuppressWarnings({"unchecked", "unused"})
    public List<String> extractRoles(@NotBlank String token) {
        Claims claims = extractClaims(token);
        return claims.get("roles", List.class);
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isTokenValid(@NotBlank String token) {
        try {
            extractClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
