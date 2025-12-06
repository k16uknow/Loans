package com.u.know.loans.service;

import com.u.know.loans.domain.AppUser;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class UserService {

    public Mono<AppUser> findByUsername(String username) {
        return Mono.just(new AppUser("kevin", "$2a$10$d56mGtA/qw3wM0ljc3ckAeHH6Ju6a4Wt5eVwV9kg4BOs1fDgP3Gg6", List.of("ROLE_ADMIN")));
    }

}
