package com.u.know.loans.service.utils;

import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class RefreshTokenGenerator {

    private final Map<String, String> store = new ConcurrentHashMap<>();

    public String createToken(String username) {
        String token = UUID.randomUUID().toString();
        store.put(token, username);
        return token;
    }

    public Mono<String> validate(String refreshToken) {
        return Mono.justOrEmpty(store.get(refreshToken));
    }

    public void invalidate(String refreshToken) {
        store.remove(refreshToken);
    }

}
