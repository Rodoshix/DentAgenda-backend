package com.dentagenda.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

import com.dentagenda.security.JwtAuthFilter;
import com.dentagenda.security.UserDetailsServiceImpl;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        return new UserDetailsServiceImpl();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth
                //Módulo: Usuarios / Autenticación
                .requestMatchers("/api/auth/login").permitAll()
                .requestMatchers("/api/usuarios/recuperar-password").permitAll()
                .requestMatchers("/api/usuarios/restablecer-password").permitAll()
                .requestMatchers("/api/usuarios/cambiar-password").hasAnyRole("PACIENTE", "ODONTOLOGO", "RECEPCIONISTA", "ADMIN")
                .requestMatchers("/api/usuarios/perfil").hasAnyRole("PACIENTE", "ODONTOLOGO", "RECEPCIONISTA", "ADMIN")
                
                //Módulo: Pacientes
                .requestMatchers("/api/pacientes/registro").hasRole("RECEPCIONISTA")
                .requestMatchers("/api/pacientes/crear-cuenta").permitAll()
                .requestMatchers("/api/usuarios/registro-paciente-web").permitAll()
                .requestMatchers("/api/pacientes/{rut}").hasAnyRole("PACIENTE", "RECEPCIONISTA") // Validar rut en backend
                .requestMatchers("/api/pacientes").hasAnyRole("RECEPCIONISTA", "ADMIN")

                //Moduilo: Citas
                .requestMatchers("/api/citas/agendar").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers("/api/citas/{id}/reprogramar").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers("/api/citas/{id}/cancelar").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers("/api/citas/{id}/confirmar").hasRole("RECEPCIONISTA")
                .requestMatchers("/api/citas/paciente/**").hasAnyRole("PACIENTE", "RECEPCIONISTA", "ODONTOLOGO")
                .requestMatchers("/api/citas/odontologo/**").hasAnyRole("ODONTOLOGO", "RECEPCIONISTA")
                .requestMatchers("/api/citas/fecha").hasAnyRole("RECEPCIONISTA", "ODONTOLOGO")
                .requestMatchers("/api/citas/buscar-por-odontologo").hasAnyRole("RECEPCIONISTA", "PACIENTE")
                .requestMatchers("/api/citas/disponibilidad/**").permitAll()
                .requestMatchers("/api/citas/futuras/odontologo").hasRole("ODONTOLOGO")

                //Módulo: Odontólogos
                .requestMatchers("/api/odontologos/registro").hasRole("ADMIN")
                .requestMatchers("/api/bloqueos/registrar").hasRole("ODONTOLOGO")
                .requestMatchers("/api/odontologos/disponibilidad").permitAll()
                .requestMatchers("/api/odontologos").permitAll()
                .requestMatchers("/api/odontologos/agenda/fecha").hasRole("RECEPCIONISTA")

                //Módulo: Recepcionistas
                .requestMatchers("/api/recepcionistas/registro").hasRole("ADMIN")
                .requestMatchers("/api/recepcionistas").hasRole("ADMIN")

                //Modulo: Historial Clínico (Tratamientos)
                .requestMatchers("/api/tratamientos/registrar").hasRole("ODONTOLOGO")
                .requestMatchers("/api/tratamientos/paciente/**").hasAnyRole("PACIENTE", "ODONTOLOGO")
                .requestMatchers("/api/tratamientos/cita/**").hasAnyRole("PACIENTE", "ODONTOLOGO")
                .requestMatchers("/api/tratamientos/{id}/editar").hasRole("ODONTOLOGO")
                
                //Módulo: Extra Pruebas o Indefinidos
                .requestMatchers(HttpMethod.DELETE, "/api/odontologos/eliminar/**").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/recepcionistas/eliminar/**").hasRole("ADMIN")

                .requestMatchers("/api/**").authenticated() // Como base para el resto de las peticiones
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}