package com.delivery.sistema.delivery.y.gestion.Security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component

public class JwtServicio {

    private final Key claveSecreta = Keys.secretKeyFor(SignatureAlgorithm.HS256);
    private final long tiempoExpiracion = 1000 * 60 * 60; // 1 hora

    public String generarToken(String correo) {
        return Jwts.builder()
                .setSubject(correo)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tiempoExpiracion))
                .signWith(claveSecreta)
                .compact();
    }

    public String extraerCorreoDesdeToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(claveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public boolean esTokenValido(String token, String correoUsuario) {
        String correoExtraido = extraerCorreoDesdeToken(token);
        return correoExtraido.equals(correoUsuario) && !estaExpirado(token);
    }

    private boolean estaExpirado(String token) {
        Date fechaExpiracion = Jwts.parserBuilder()
                .setSigningKey(claveSecreta)
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
        return fechaExpiracion.before(new Date());
    }


}
