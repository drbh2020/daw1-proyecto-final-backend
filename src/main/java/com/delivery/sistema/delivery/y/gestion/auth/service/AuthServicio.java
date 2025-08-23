package com.delivery.sistema.delivery.y.gestion.auth.service;


import com.delivery.sistema.delivery.y.gestion.auth.dto.Registre;
import com.delivery.sistema.delivery.y.gestion.auth.dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.auth.dto.Respuesta;
import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import com.delivery.sistema.delivery.y.gestion.shared.security.JwtServicio;
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

    public Respuesta registrar(Registre registro) {
        Cliente cliente = new Cliente();
        cliente.setNombre(registro.getNombre());
        cliente.setEmail(registro.getEmail());
        cliente.setPassword(passwordEncoder.encode(registro.getPassword()));
        cliente.setDireccion(registro.getDireccion());
        clienteRepository.save(cliente);

        String token = jwtServicio.generarToken(registro.getEmail());
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
