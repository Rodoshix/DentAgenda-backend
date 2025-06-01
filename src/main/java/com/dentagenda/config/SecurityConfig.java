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
                    // Autenticación
                .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    // Recuperación y restablecimiento
                .requestMatchers(HttpMethod.POST, "/api/usuarios/recuperar-password").permitAll()
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/restablecer-password").permitAll()
                    // Acciones con sesión activa
                .requestMatchers(HttpMethod.PUT, "/api/usuarios/cambiar-password").hasAnyRole("PACIENTE", "ODONTOLOGO", "RECEPCIONISTA", "ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/usuarios/perfil").hasAnyRole("PACIENTE", "ODONTOLOGO", "RECEPCIONISTA", "ADMIN")
                
                //Módulo: Pacientes
                    // Registro del paciente por parte de recepcionista
                .requestMatchers(HttpMethod.POST, "/api/pacientes/registro").hasRole("RECEPCIONISTA")
                    // Registro o activación de cuenta web por parte del paciente
                .requestMatchers(HttpMethod.POST, "/api/pacientes/crear-cuenta").permitAll()
                    // Consultar paciente por RUT
                .requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole("RECEPCIONISTA", "ODONTOLOGO")
                    // Listar todos los pacientes
                .requestMatchers(HttpMethod.GET, "/api/pacientes").hasAnyRole("RECEPCIONISTA", "ADMIN")

                //Moduilo: Citas
                    // Crear, cancelar, reprogramar y confirmar citas  
                .requestMatchers(HttpMethod.POST, "/api/citas/agendar").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/cancelar").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT, "/api/citas/*/reprogramar").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.PUT, "/api/citas/confirmar-asistencia/*").hasRole("RECEPCIONISTA")     
                    // Historial de citas por paciente u odontólogo
                .requestMatchers(HttpMethod.GET, "/api/citas/paciente/**").hasAnyRole("PACIENTE", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET, "/api/citas/odontologo/**").hasAnyRole("ODONTOLOGO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET, "/api/citas/futuras/odontologo").hasRole("ODONTOLOGO")
                    // Buscar citas por odontólogo, fecha y estado
                .requestMatchers(HttpMethod.GET, "/api/citas/estado").hasAnyRole("ODONTOLOGO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET, "/api/citas/fecha").hasAnyRole("ODONTOLOGO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET, "/api/citas/buscar-por-odontologo").hasAnyRole("ODONTOLOGO", "RECEPCIONISTA")
                .requestMatchers(HttpMethod.GET, "/api/citas/disponibilidad").permitAll()

                //Módulo: Odontólogos
                    // Registro y eliminación de odontólogos (solo ADMIN)
                .requestMatchers(HttpMethod.POST, "/api/odontologos/registro").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/odontologos/eliminar/*").hasRole("ADMIN")
                    // Listar odontólogos (público)
                .requestMatchers(HttpMethod.GET, "/api/odontologos").permitAll()
                    // Consultar agenda por odontólogo y fecha (solo recepcionista)
                .requestMatchers(HttpMethod.GET, "/api/odontologos/agenda/fecha").hasRole("RECEPCIONISTA")
                    // Registrar bloqueos de agenda (solo odontólogo)
                .requestMatchers(HttpMethod.POST, "/api/bloqueos/registrar").hasRole("ODONTOLOGO")

                //Módulo: Recepcionistas
                .requestMatchers(HttpMethod.POST, "/api/recepcionistas/registro").hasRole("ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/recepcionistas").hasRole("ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/recepcionistas/eliminar/*").hasRole("ADMIN")

                //Modulo: Historial Clínico (Tratamientos)
                .requestMatchers("/api/tratamientos/registrar").hasRole("ODONTOLOGO")
                .requestMatchers("/api/tratamientos/paciente/**").hasAnyRole("PACIENTE", "ODONTOLOGO")
                .requestMatchers("/api/tratamientos/cita/**").hasAnyRole("PACIENTE", "ODONTOLOGO")
                .requestMatchers("/api/tratamientos/{id}/editar").hasRole("ODONTOLOGO")
                
                //Módulo: Extra Pruebas o Indefinidos
                .requestMatchers("/api/**").authenticated() // Como base para el resto de las peticiones
                .anyRequest().authenticated()
            )
            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }
}