// Classe desativada: CORS agora centralizado em SecurityConfig.
// Mantida apenas como referência temporária. Remover depois.
// package com.projetocefoods.cefoods.config;
//
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.web.cors.CorsConfiguration;
// import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
// import org.springframework.web.filter.CorsFilter;
//
// import java.util.List;
//
// @Configuration
// public class CorsConfig {
//     @Bean
//     public CorsFilter corsFilter() {
//         CorsConfiguration config = new CorsConfiguration();
//         config.setAllowCredentials(true);
//         config.setAllowedOriginPatterns(List.of("*"));
//         config.setAllowedHeaders(List.of("*"));
//         config.setAllowedMethods(List.of("*"));
//         config.setExposedHeaders(List.of("Authorization", "Content-Disposition"));
//         config.setMaxAge(3600L);
//         UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//         source.registerCorsConfiguration("/**", config);
//         return new CorsFilter(source);
//     }
// }

