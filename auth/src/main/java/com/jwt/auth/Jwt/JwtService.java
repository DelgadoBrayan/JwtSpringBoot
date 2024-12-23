package com.jwt.auth.Jwt;

import java.security.Key;
import java.sql.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
@Service
public class JwtService {
    private static final String SECRET_KEY= "pOvL0wB7w/nlXxQ6Dxf0N7qj1HYuMSh4TkC4duRJvFY=";
/*Es una sobrecarga del método getToken que permite generar un token JWT sin incluir datos adicionales (claims).
, pasando un mapa vacío de claims extra. */
    public String getToken(UserDetails user) {
        return getToken(new HashMap<>(), user);
    }

    public String getToken(Map<String, Object> extraClaims, UserDetails user) {
       return Jwts
        .builder() /*Crea un nuevo token JWT. */
        .setClaims(extraClaims)/* Agrega claims adicionales (datos personalizados como roles o permisos)  */
        .setSubject(user.getUsername())/* Define al usuario asociado al token (el subject). */
        .setIssuedAt(new Date(System.currentTimeMillis()))/* Especifica la fecha de creación del token */
        .setExpiration(new Date(System.currentTimeMillis()+1000*60*24)) /* Define la fecha de expiración (24 minutos después de ser emitido). */
        .signWith(getKey(), SignatureAlgorithm.HS256)/* firma el token con secret key y utiliza el algoritmo HS256 */
        .compact(); /*Genera el token JWT como un String. */
    }
    /*Convierte la clave secreta (SECRET_KEY) en un objeto de tipo Key para firmar y verificar tokens. */
    private Key getKey() {
        /* Decodifica la clave secreta, que está en formato Base64. */
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        /*Genera una clave adecuada para el algoritmo HMAC-SHA. */
        return Keys.hmacShaKeyFor(keyBytes);
    }
    /*  Extrae el nombre de usuario (subject) de un token JWT */
    public String getUsernameFromToken(String token) {
        /* Utiliza el método genérico getClaim para obtener el campo subject del token. */
       return getClaim(token, Claims::getSubject);
    }
    /*  */
    public boolean isTokenValid(String token, UserDetails userDetails) {
      final String username = getUsernameFromToken(token); /* Extrae el nombre de usuario del token. */
      return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
      /* Verifica si el token pertenece al usuario autenticado si el token es valido de vuelve true si no false*/
    }

    /* Extrae todos los claims (datos) de un token JWT. */
    private Claims getAllClaims(String token){
        return Jwts
            .parserBuilder() /* Crea un objeto para analizar el token. */
            .setSigningKey(getKey()) /*  Especifica la clave para validar el token. */
            .build()
            .parseClaimsJws(token) /* Verifica y descompone el token.*/
            .getBody(); /* Recupera el cuerpo del token (los claims). */
    }

    /*  Método genérico para obtener un claim específico de un token */
    public <T> T getClaim(String token, Function<Claims,T> claimsResolver){
        final Claims claims = getAllClaims(token); /* obtiene todos los claims */
        return claimsResolver.apply(claims); /* recupera un claim es especifico */
    }
    /* Obtiene la fecha de expiración del token */
    private java.util.Date getExpiration(String token){
        /*  Llama a getClaim para recuperar el campo expiration. */
        return getClaim(token,Claims::getExpiration);
    }

    /*  Obtiene la fecha de expiración.  */
    private boolean isTokenExpired(String token){
        return getExpiration(token).before(new java.util.Date());/* Comprueba si la fecha de expiración es anterior a la actual. */
    }
}
