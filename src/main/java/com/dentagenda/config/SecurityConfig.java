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
                .requestMatchers("/api/citas/fecha").permitAll()              // habilita obtener citas por fecha
                .requestMatchers("/api/citas/estado").permitAll()             // habilita obtener citas por estado
                .requestMatchers("/api/citas/buscar-por-odontologo").permitAll() // habilita buscar citas por odontologo
                .requestMatchers("/api/citas/futuras/odontologo").permitAll() // habilita obtener citas futuras por odontologo
                .requestMatchers("/api/pacientes/crear-cuenta").permitAll()   // habilita crear cuenta de paciente
                .requestMatchers("/api/pacientes/login").permitAll()          // habilita login de pacientes
                .requestMatchers("/api/odontologos/registro").permitAll()     // habilita registro de odontologos
                .requestMatchers("/api/citas/odontologo/**").permitAll()       // habilita obtener historial de citas por odontologo
                .requestMatchers("/api/citas/paciente/**").permitAll()        // habilita obtener historial de citas por paciente
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        return http.build();
    }
}