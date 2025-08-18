package com.delivery.sistema.delivery.y.gestion.Services;


import com.delivery.sistema.delivery.y.gestion.Dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.Dto.Respuesta;
import com.delivery.sistema.delivery.y.gestion.Entity.Cliente;
import com.delivery.sistema.delivery.y.gestion.Repository.ClienteRepository;
import com.delivery.sistema.delivery.y.gestion.Security.JwtServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor

public class AuthServicio {

    private final ClienteRepository clienteRepository;
    private final JwtServicio jwtServicio;
    private final AuthenticationManager authManager;
    private final PasswordEncoder passwordEncoder;

    public Respuesta registrar(Solicitud solicitud) {
        Cliente cliente = new Cliente();
        cliente.setNombre(solicitud.getNombre());
        cliente.setEmail(solicitud.getEmail());
        cliente.setPassword(passwordEncoder.encode(solicitud.getPassword()));
        cliente.setDireccion(solicitud.getDireccion());
        clienteRepository.save(cliente);

        String token = jwtServicio.generarToken(cliente.getEmail());
        return new Respuesta(token);
    }

    public Respuesta login(Solicitud solicitud) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(solicitud.getEmail(), solicitud.getPassword())
        );
        String token = jwtServicio.generarToken(solicitud.getEmail());
        return new Respuesta(token);
    }

}
