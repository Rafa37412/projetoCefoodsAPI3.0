package com.projetocefoods.cefoods.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            // Desabilita a proteção CSRF, que é comum para APIs REST stateless
            .csrf(AbstractHttpConfigurer::disable)
            // Define as regras de autorização para os endpoints
            .authorizeHttpRequests(authorize -> authorize
                // Permite qualquer método para /usuarios e /usuarios/**
                .requestMatchers("/usuarios", "/usuarios/**").permitAll()
                // Exige autenticação para qualquer outra requisição
                .anyRequest().authenticated()
            );
        return http.build();
    }
}
