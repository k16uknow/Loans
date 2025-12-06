package com.u.know.loans.config.security;

import com.u.know.loans.service.utils.TokenGenerator;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.util.List;

@Slf4j
@Component
public class JwtReactiveAuthenticationManager implements ReactiveAuthenticationManager {

    private final SecretKey secretKey;

    public JwtReactiveAuthenticationManager(TokenGenerator jwtService) {
        this.secretKey = jwtService.getSecretKey();
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();

        return Mono.<Authentication>fromCallable(() -> {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String username = claims.getSubject();

            List<String> roles = claims.get("roles", List.class);
            List<SimpleGrantedAuthority> authorities = roles == null
                    ? List.of()
                    : roles.stream().map(SimpleGrantedAuthority::new).toList();

            return new UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    authorities
            );
        })
                .onErrorResume( e -> Mono.empty());
    }
}
/*
 *
 * ┌────────────────────────┐
 * │  HTTP Request          │
 * │  Authorization: Bearer │
 * └──────────────┬─────────┘
 *                │
 *                ▼
 *  ┌a ─ -s ─────────────────────────────────────────────────────── ┐
 * │ JwtServerAuthenticationConverter                         │
 * │  - Extracts token                                        │
 * │  - Returns UsernamePasswordAuthenticationToken(token)    │
 * └ ─────────────┬──────────────────────────────────────────┘
 *                │
 *                ▼
 * ┌─────────────────────────────────────────────────────────┐
 * │ JwtReactiveAuthenticationManager                         │
 * │  - Validates token                                       │
 * │  - Checks signature                                      │
 * │  - Extracts subject + roles                              │
 * │  - Returns *authenticated* Authentication                │
 * └──────────────┬──────────────────────────────────────────┘
 *                │
 *                ▼
 * ┌─────────────────────────────────────────────────────────┐
 * │ JwtSecurityContextRepository                             │
 * │  - Saves auth into Reactor Context                       │
 * └──────────────┬──────────────────────────────────────────┘
 *                │
 *                ▼
 * ┌────────────────────────┐
 * │ Controller             │
 * └────────────────────────┘
 */