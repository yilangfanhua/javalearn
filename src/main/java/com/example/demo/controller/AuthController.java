package com.example.demo.controller;


import com.example.demo.common.ApiResponse;
import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.service.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequestMapping("/auth")
@Validated
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping ("/register")
    public ApiResponse<String> register(@Valid @RequestBody RegisterRequest request){
        authService.register(request);
        return ApiResponse.success("注册成功");
    }
    @PostMapping("/login")
    public ApiResponse<AuthResponse> login(@Valid  @RequestBody  LoginRequest request){
        AuthResponse response=authService.login(request);
       
        return ApiResponse.success(response);
    }

}
