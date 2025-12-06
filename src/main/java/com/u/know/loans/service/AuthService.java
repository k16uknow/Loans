package com.u.know.loans.service;

import com.u.know.loans.controller.request.LoginRequest;
import com.u.know.loans.controller.request.RefreshRequest;
import com.u.know.loans.controller.response.LoginResponse;
import com.u.know.loans.exception.NotFoundException;
import com.u.know.loans.service.utils.RefreshTokenGenerator;
import com.u.know.loans.service.utils.TokenGenerator;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class AuthService {

    private final UserService userService;
    private final TokenGenerator tokenGenerator;
    private final RefreshTokenGenerator refreshTokenGenerator;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserService userService, TokenGenerator tokenGenerator, RefreshTokenGenerator refreshTokenGenerator, PasswordEncoder passwordEncoder) {
        this.userService = userService;
        this.tokenGenerator = tokenGenerator;
        this.refreshTokenGenerator = refreshTokenGenerator;
        this.passwordEncoder = passwordEncoder;
    }

    public Mono<LoginResponse> login(LoginRequest loginRequest) {
        return userService.findByUsername(loginRequest.username())
                .switchIfEmpty(Mono.error(new NotFoundException("Invalid credentials")))
                .flatMap( appUser -> {
                    if(!passwordEncoder.matches(loginRequest.password(), appUser.getPasswordHash())) {
                        log.info("HASH: {}", passwordEncoder.encode(loginRequest.password()));
                        return Mono.error(new RuntimeException("Invalid credentials"));
                    }

                    String token = tokenGenerator.generateJwtToken(appUser);
                    String refreshToken = refreshTokenGenerator.createToken(appUser.getUsername());

                    return Mono.just(new LoginResponse(token, refreshToken));
                });
    }

    public Mono<LoginResponse> refresh(RefreshRequest refreshRequest) {
        return refreshTokenGenerator.validate(refreshRequest.refreshToken())
                .switchIfEmpty(Mono.error(new RuntimeException("Invalid refresh token")))
                .flatMap(userService::findByUsername)
                .switchIfEmpty(Mono.error(new RuntimeException("User not found")))
                .flatMap( username -> {
                    refreshTokenGenerator.invalidate(refreshRequest.refreshToken());
                    String token = tokenGenerator.generateJwtToken(username);
                    String refreshToken = refreshTokenGenerator.createToken(username.getUsername());
                    return Mono.just(new LoginResponse(token, refreshToken));
                });
    }
}
