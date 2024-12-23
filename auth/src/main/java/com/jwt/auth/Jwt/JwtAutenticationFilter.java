package com.jwt.auth.Jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
@Component
@RequiredArgsConstructor
public class JwtAutenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final UserDetailsService userDetailsService;
    @Override
    /* Este método contiene la lógica principal del filtro. Es llamado automáticamente por Spring Security para cada solicitud HTTP */
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
            /* Este método llama a getTokenFromRequest para extraer el token JWT del encabezado Authorization de la solicitud HTTP. */
        final String token = getTokenFromRequest(request);
        final String username;
        if (token == null) {
            /* Si no se encuentra un token en la solicitud, el filtro no realiza ninguna acción adicional y
             pasa el control al siguiente filtro en la cadena (filterChain.doFilter). */
            filterChain.doFilter(request, response);
            return;
        }
        /* Si se encuentra un token, se extrae el nombre de usuario utilizando el método getUsernameFromToken del servicio jwtService. */
        username = jwtService.getUsernameFromToken(token);
        /* se verifica que el nombre del usuario se halla extraido correctamente y se comprueba que no halla una autenticacion establecida */
        if(username != null && SecurityContextHolder.getContext().getAuthentication()==null){
            /* Utiliza userDetailsService para cargar los detalles del usuario desde la base de datos */
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            /* Este método verifica que el token sea válido y que coincida con el usuario. Si no es válido,
             el filtro no establece la autenticación y pasa al siguiente filtro. */
            if(jwtService.isTokenValid(token, userDetails)){
                /*  Crea un token de autenticación para el usuario */
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                    userDetails, /* Detalles del usuario autenticado. */
                    null, /*  No se necesita credencial adicional porque el token ya fue validado. */
                    userDetails.getAuthorities()); /* Establece los roles y privilegios del usuario. */
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request)); /* Proporciona detalles adicionales sobre la solicitud HTTP. */
                /*  Establece la autenticación en el contexto de seguridad de Spring para que las siguientes
                 capas de la aplicación puedan identificar al usuario autenticado. */
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        /* Pasa la solicitud y la respuesta al siguiente filtro en la cadena */
        filterChain.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION); /*  Obtiene el valor del encabezado Authorization de la solicitud HTTP. */
        /*  Comprueba que el encabezado no sea nulo ni vacío y se verifica que el token comienze con bearer */
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); /* Elimina el prefijo Bearer para obtener solo el token JWT. */
        }
        return null; /* se retorna null si no se encuentra un token valido */
    }
}
