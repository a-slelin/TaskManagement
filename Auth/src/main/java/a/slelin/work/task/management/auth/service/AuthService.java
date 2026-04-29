package a.slelin.work.task.management.auth.service;

import a.slelin.work.task.management.auth.entity.RefreshToken;
import a.slelin.work.task.management.auth.entity.Role;
import a.slelin.work.task.management.auth.entity.User;
import a.slelin.work.task.management.auth.mapper.UserMapper;
import a.slelin.work.task.management.auth.repository.RefreshTokenRepository;
import a.slelin.work.task.management.auth.repository.RoleRepository;
import a.slelin.work.task.management.auth.repository.UserRepository;
import a.slelin.work.task.management.auth.util.JwtHolder;
import a.slelin.work.task.management.core.dto.auth.JwtResponse;
import a.slelin.work.task.management.core.dto.auth.LoginRequest;
import a.slelin.work.task.management.core.dto.auth.UserWD;
import a.slelin.work.task.management.core.exception.EntityNotFoundByPropertyException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Service
@Validated
@Transactional
@RequiredArgsConstructor
public class AuthService {

    private final PasswordEncoder encoder;

    private final UserMapper userMapper;

    private final UserRepository userRepository;

    private final RoleRepository roleRepository;

    private final RefreshTokenRepository refreshTokenRepository;

    private final JwtHolder jwtHolder;

    private final JwtService jwtService;

    public JwtResponse login(@NotNull @Valid LoginRequest login) {
        User user = userRepository.findByFactor(login.factor())
                .orElseThrow(() -> new BadCredentialsException("Invalid factor or password."));

        if (!encoder.matches(login.password(), user.getPassword())) {
            throw new BadCredentialsException("Invalid factor or password.");
        }

        String refreshToken = jwtService.generateRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtHolder.refreshExpiration() / 1000))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new JwtResponse(accessToken, refreshToken);
    }

    @Transactional(readOnly = true)
    public JwtResponse refresh(@NotNull String refreshToken) {

        if (refreshToken.startsWith("Bearer")) {
            refreshToken = refreshToken.substring("Bearer".length()).trim();
        }

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid refresh token.");
        }

        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new AuthenticationCredentialsNotFoundException("Refresh token not found."));

        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Refresh token is expired.");
        }

        String accessToken = jwtService.generateAccessToken(rt.getUser());

        return new JwtResponse(accessToken, refreshToken);
    }

    public void logout(@NotNull String refreshToken) {

        if (refreshToken.startsWith("Bearer")) {
            refreshToken = refreshToken.substring("Bearer".length()).trim();
        }

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid refresh token.");
        }

        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Refresh token not found."));

        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Invalid refresh token.");
        }

        refreshTokenRepository.delete(rt);
    }

    public JwtResponse register(@NotNull @Valid UserWD newUser) {
        User user = userMapper.toEntity(newUser);

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new EntityNotFoundByPropertyException(Role.class, "name", "ROLE_USER"));
        Set<Role> roles = new HashSet<>();
        roles.add(role);
        user.setRoles(roles);

        user = userRepository.save(user);

        String refreshToken = jwtService.generateRefreshToken(user);
        String accessToken = jwtService.generateAccessToken(user);

        RefreshToken refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .token(refreshToken)
                .expiryDate(LocalDateTime.now().plusSeconds(jwtHolder.refreshExpiration() / 1000))
                .build();
        refreshTokenRepository.save(refreshTokenEntity);

        return new JwtResponse(accessToken, refreshToken);
    }

    public void logoutAll(@NotNull String refreshToken) {

        if (refreshToken.startsWith("Bearer")) {
            refreshToken = refreshToken.substring("Bearer".length()).trim();
        }

        if (!jwtService.isTokenValid(refreshToken)) {
            throw new AuthenticationCredentialsNotFoundException("Invalid refresh token.");
        }

        RefreshToken rt = refreshTokenRepository.findByToken(refreshToken)
                .orElseThrow(() -> new BadCredentialsException("Invalid refresh token."));

        if (rt.getExpiryDate().isBefore(LocalDateTime.now())) {
            throw new BadCredentialsException("Invalid refresh token.");
        }

        refreshTokenRepository.deleteByUser(rt.getUser());
    }

    @Scheduled(cron = "0 0 3 * * *")
    protected void cleanExpiredRefreshTokens() {
        refreshTokenRepository.deleteAllExpired();
    }
}
