package com.jwt.auth.Auth;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.jwt.auth.Jwt.JwtService;
import com.jwt.auth.User.UserRole;
import com.jwt.auth.User.User;
import com.jwt.auth.User.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthService {
  /*Interactúa con la base de datos para buscar, guardar o modificar usuarios.*/
    private final UserRepository userRepository;
  /*Maneja la generación y validación de tokens JWT (JSON Web Token). */
    private final JwtService jwtService;
  /* Encripta contraseñas antes de guardarlas en la base de datos. También permite verificar contraseñas en el login. */
    private final PasswordEncoder passwordEncoder;
  /*Gestiona el proceso de autenticación utilizando las credenciales del usuario. */
    private final AuthenticationManager authenticationManager;
    public AuthResponse login(LoginRequest request) {
    /*Intenta autenticar al usuario usando su nombre de usuario y contraseña. Si las credenciales no coinciden, lanza una excepción */
      authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword()));
    /* Busca en la base de datos al usuario correspondiente al nombre de usuario proporcionado en el LoginRequest.En caso de que no se 
     * encuentre manda una excepcion 
     */
      UserDetails user = userRepository.findByUsername(request.getUsername()).orElseThrow();
    /*Genera un token JWT basado en los datos del usuario. */
      String token = jwtService.getToken(user);
    /*Construye un objeto de respuesta (AuthResponse) que contiene el token JWT. */
      return AuthResponse.builder()
          .token(token)
          .build();
    }

    public AuthResponse register(RegisterRequest request) {
    /*Crea una nueva instancia de usuario con los datos del registro. */
       User user = User.builder()
            .username(request.getUsername())
    /* Encripta la contraseña proporcionada por el usuario antes de guardarla en la base de datos */
            .password(passwordEncoder.encode(request.getPassword()))
            .firstname(request.getFirstname())
            .lastname(request.getLastname())
            .country(request.getCountry())
            .role(UserRole.STUDENT)
            .build();
      /*Guarda al nuevo usuario en la base de datos. */
        userRepository.save(user);
      /* Devuelve una respuesta que incluye el token JWT para el usuario recien registrado. */
        return AuthResponse.builder()
        .token(jwtService.getToken(user))
        .build();
    }

}
