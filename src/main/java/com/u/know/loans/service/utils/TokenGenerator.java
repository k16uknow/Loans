package com.u.know.loans.service.utils;

import com.u.know.loans.domain.AppUser;
import io.jsonwebtoken.Jwts;
import lombok.Getter;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

@Service
@Getter
public class TokenGenerator {

    private final SecretKey secretKey;

    public TokenGenerator() {
        this.secretKey = Jwts.SIG.HS512.key().build();;
    }

    public String generateJwtToken(AppUser user) {
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("roles", user.getRoles())
                .issuedAt(new Date())
                .expiration(Date.from(Instant.now().plus(1, ChronoUnit.HOURS)))
                .signWith(secretKey)
                .compact();
    }

}
