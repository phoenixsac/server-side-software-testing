//package com.had.adminservice.config;
//
////import com.had.adminservice.filter.AuthorizationFilter;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//
//
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//
//
//@Configuration
//@EnableWebSecurity
//public class SecurityConfig {
//
////        @Autowired
////        AuthorizationFilter authorizationFilter;
//
//        @Bean
//        public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//                return http
//                        // Add your authorization filter before UsernamePasswordAuthenticationFilter:
////                        .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)
//                        .csrf(csrf -> csrf.disable()) // CSRF disabled for now
//                        .build();
//        }
//}
//
//
//
//
//// .addFilterBefore(authorizationFilter, UsernamePasswordAuthenticationFilter.class)