package com.example.demo.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.http.HttpMethod;




@Configuration
public class SecurityConfig {

    private final JwtService jwtService;
    private final AccountUserDetailsService userDetailsService;

    public SecurityConfig(JwtService jwtService,
                          AccountUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, userDetailsService);
    }



    //private final JwtAuthenticationFilter jwtAuthenticationFilter;

    

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {

        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http)
            throws Exception {

        http
                .csrf()
                .disable()
                .sessionManagement()
                .sessionCreationPolicy(
                        SessionCreationPolicy.STATELESS
                )
                .and()
                .authorizeRequests()
                .antMatchers(
                        "/hello",
                        "/auth/**",
                        "/actuator/health"
                )
                .permitAll()
                .antMatchers(HttpMethod.GET, "/users/**")
                .hasAnyRole("USER", "ADMIN")
                .antMatchers(HttpMethod.POST, "/users/**")
                .hasRole("ADMIN")
                .antMatchers(HttpMethod.PUT, "/users/**")
                .hasRole("ADMIN")
                .antMatchers(HttpMethod.DELETE, "/users/**")
                .hasRole("ADMIN")
                .anyRequest()
                .authenticated()
                .and()
                .addFilterBefore(
                         jwtAuthenticationFilter(),
                         UsernamePasswordAuthenticationFilter.class
                 );

        return http.build();
    }
}