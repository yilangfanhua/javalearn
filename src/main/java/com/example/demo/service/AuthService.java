package com.example.demo.service;

import com.example.demo.dto.AuthResponse;
import com.example.demo.dto.LoginRequest;
import com.example.demo.dto.RegisterRequest;
import com.example.demo.entity.Account;
import com.example.demo.mapper.AccountMapper;
import com.example.demo.security.JwtService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service 
public class AuthService {
    //System.out.println(">>> AuthService 被 Spring 创建了");
    private final AccountMapper accountMapper;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(AccountMapper accountMapper, PasswordEncoder passwordEncoder,
            AuthenticationManager authenticationManager, JwtService jwtService) {
        System.out.println(">>> AuthService 被 Spring 创建了");   // ← 加这行
        this.accountMapper = accountMapper;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public void register(RegisterRequest request){
        Account exitingAccount=accountMapper.findByUserName(request.getUsername());
        if(exitingAccount!=null){
            throw new IllegalArgumentException("用户已存在");
        }
        Account account=new Account(request.getUsername(),passwordEncoder.encode(request.getPassword()),"USER");
        accountMapper.insert(account);
    }
    

    public AuthResponse login(LoginRequest request){
    Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                    request.getUsername(),
                    request.getPassword()
            )
    );
    //认证成功
    UserDetails userDetails = (UserDetails) authentication.getPrincipal();
    String token = jwtService.generateToken(userDetails);
    return new AuthResponse(token, "Bearer", jwtService.getExpirationMs());
}
}
