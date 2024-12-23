package com.jwt.auth.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.jwt.auth.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
    private final UserRepository userRepository;
    /* al utilizar La anotación @Bean en Spring se le esta diciendo que este metodo debe ser ejecutado para crear y registrar
     * un componente dentro del contexto de la aplicacion ⬇️
     */
    @Bean
    /* Crea y configura un AuthenticationManager, que es responsable de gestionar el proceso de autenticación. */
    /* AuthenticationManager se utiliza cuando un usuario intenta iniciar sesión. Este gestor se encarga
     de autenticar al usuario usando un AuthenticationProvider. */
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        /* AuthenticationConfiguration es una clase proporcionada por Spring Security que ayuda a crear un AuthenticationManager. */
        return config.getAuthenticationManager();
    }
    /*  Crea un AuthenticationProvider que será utilizado para autenticar a los usuarios. */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        /* DaoAuthenticationProvider es un tipo de proveedor de autenticación que usa un UserDetailsService
         (en este caso, el userDetailsService() que has definido) para cargar los detalles del usuario y un PasswordEncoder 
         (en este caso, el passwordEncoder()) para verificar la contraseña. */
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        /*  Configura el servicio que busca los datos del usuario (como nombre de usuario y contraseña). */
        authenticationProvider.setUserDetailsService(userDetailsService());
        /* Configura el codificador de contraseñas para verificar las contraseñas. */
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;

    }
    /* Crea un PasswordEncoder que se encargará de codificar y verificar contraseñas. */
    @Bean
    public PasswordEncoder passwordEncoder() {
    /*  es un tipo de codificador que utiliza el algoritmo BCrypt para proteger las contraseñas */
        return new BCryptPasswordEncoder();
    }

    /* Crea un UserDetailsService que Spring Security usará para cargar los detalles del usuario desde la base de datos. */
    @Bean
    public UserDetailsService userDetailsService() {
        return username -> userRepository.findByUsername(username)
        .orElseThrow(()->new UsernameNotFoundException("user not found"));
    }
}
