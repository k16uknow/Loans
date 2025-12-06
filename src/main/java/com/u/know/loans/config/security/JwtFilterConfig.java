package com.u.know.loans.config.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;

@Configuration
public class JwtFilterConfig {

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(
            JwtReactiveAuthenticationManager authenticationManager,
            JwtSecurityContextRepository securityContextRepository
    ) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager);
        filter.setSecurityContextRepository(securityContextRepository);
        return filter;
    }

}
