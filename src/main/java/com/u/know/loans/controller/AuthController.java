package com.u.know.loans.controller;

import com.u.know.loans.controller.request.LoginRequest;
import com.u.know.loans.controller.request.RefreshRequest;
import com.u.know.loans.controller.response.LoginResponse;
import com.u.know.loans.controller.response.wrapper.ApiResponse;
import com.u.know.loans.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> login(@RequestBody Mono<LoginRequest> loginRequestMono) {
        return loginRequestMono
                .flatMap(authService::login)
                .map(loginResponse -> ResponseEntity.ok().body(ApiResponse.success(loginResponse)));
    }

    @PostMapping("/refresh")
    public Mono<ResponseEntity<ApiResponse<LoginResponse>>> refresh(@RequestBody Mono<RefreshRequest> loginRequestMono) {
        return loginRequestMono
                .flatMap(authService::refresh)
                .map(loginResponse -> ResponseEntity.ok().body(ApiResponse.success(loginResponse)));
    }
}
