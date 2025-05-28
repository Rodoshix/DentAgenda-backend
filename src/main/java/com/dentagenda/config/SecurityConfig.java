package com.dentagenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/pacientes/registro").permitAll()       // habilita registro de pacientes
                .requestMatchers("/api/citas/agendar").permitAll()            // habilita agendar 
                .requestMatchers("/api/citas/*/cancelar").permitAll()         // habilita cancelar citas
                .requestMatchers("/api/citas/*/reprogramar").permitAll()      // habilita reprogramar citas
                .requestMatchers("/api/citas/paciente/*").permitAll()         // habilita obtener citas por paciente
                .requestMatchers("/api/citas/fecha").permitAll()              // habilita obtener citas por fecha
                .requestMatchers("/api/citas/estado").permitAll()             // habilita obtener citas por estado
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}