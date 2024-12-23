package com.jwt.auth.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.jwt.auth.Jwt.JwtAutenticationFilter;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    /* Este es un filtro personalizado que probablemente verifica el token JWT en las solicitudes HTTP 
    entrantes para autenticar a los usuarios */
    private final JwtAutenticationFilter jwtAutenticationFilter;
    /*  Este proveedor es responsable de autenticar a los usuarios utilizando las credenciales
     proporcionadas en las solicitudes HTTP */
    private final AuthenticationProvider authProvider;
    @Bean
    public  SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
        return http
        .csrf(csrf ->
        csrf.disable())/* desactiva la protección CSRF */
        .authorizeHttpRequests(authRequest ->
            authRequest
            .requestMatchers("/auth/**").permitAll()/* permite que cual quier peticion que tenha auth sea accedida sin autenticacion */
            .anyRequest().authenticated()/* esto quiere decir que el resto de solicitudes deben de estar autenticados */
        )
        .sessionManagement(sessionManagement->
        sessionManagement
        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)) /* sirve para que spring no itente mantener el estado de la session en el servidor */
        /* es el proveedor de autenticación que se utilizará para autenticar las solicitudes. 
        Este proveedor fue configurado previamente en ApplicationConfig (probablemente el DaoAuthenticationProvider),
         el cual usa un UserDetailsService y un PasswordEncoder para verificar las credenciales del usuario. */
        .authenticationProvider(authProvider)
        /*  (jwtAutenticationFilter) que valida el token JWT antes de que Spring Security procese la solicitud. */
        .addFilterBefore(jwtAutenticationFilter, UsernamePasswordAuthenticationFilter.class)
        /* construye la cadena de filtros de seguridad configurada y la devuelve. */
        .build();
    }
}
