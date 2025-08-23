package com.delivery.sistema.delivery.y.gestion.shared.security;

import com.delivery.sistema.delivery.y.gestion.auth.service.DetallesUsuarioServicio;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component

public class FiltroJwtAutenticacion extends OncePerRequestFilter {

    private final JwtServicio jwtServicio;
    private final DetallesUsuarioServicio detallesUsuarioServicio;

    public FiltroJwtAutenticacion(JwtServicio jwtServicio,
                                  DetallesUsuarioServicio detallesUsuarioServicio) {
        this.jwtServicio = jwtServicio;
        this.detallesUsuarioServicio = detallesUsuarioServicio;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader("Authorization");
        String token = null;
        String correo = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);
            correo = jwtServicio.extraerCorreoDesdeToken(token);
        }

        if (correo != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails usuario = detallesUsuarioServicio.loadUserByUsername(correo);

            if (jwtServicio.esTokenValido(token, usuario.getUsername())) {
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        usuario, null, usuario.getAuthorities());
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        filterChain.doFilter(request, response);
    }






}
