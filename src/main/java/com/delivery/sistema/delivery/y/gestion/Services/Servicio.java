package com.delivery.sistema.delivery.y.gestion.Services;

import com.delivery.sistema.delivery.y.gestion.Dto.Registre;
import com.delivery.sistema.delivery.y.gestion.Dto.Respuesta;
import com.delivery.sistema.delivery.y.gestion.Dto.Solicitud;
import com.delivery.sistema.delivery.y.gestion.Entity.Cliente;
import com.delivery.sistema.delivery.y.gestion.Entity.Rol;
import com.delivery.sistema.delivery.y.gestion.Repository.ClienteRepository;
import com.delivery.sistema.delivery.y.gestion.Repository.RolRepository;
import com.delivery.sistema.delivery.y.gestion.Security.JwtServicio;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service

public class Servicio {
    private final ClienteRepository clienteRepo;
    private final RolRepository rolRepo;
    private final JwtServicio jwtServicio;
    private final AuthenticationManager authManager;
    private final PasswordEncoder encoder;

    public Servicio(ClienteRepository clienteRepo, RolRepository rolRepo,
                    JwtServicio jwtServicio, AuthenticationManager authManager,
                    PasswordEncoder encoder) {
        this.clienteRepo = clienteRepo;
        this.rolRepo = rolRepo;
        this.jwtServicio = jwtServicio;
        this.authManager = authManager;
        this.encoder = encoder;
    }

    public Respuesta registrar(Registre req) {
        Cliente cliente = new Cliente();
        cliente.setNombre(req.getNombre());
        cliente.setEmail(req.getEmail());
        cliente.setPassword(encoder.encode(req.getPassword()));
        cliente.setDireccion(req.getDireccion());

        Rol rolCliente = rolRepo.findByNombre("ROL_CLIENTE")
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        cliente.setRoles(Set.of(rolCliente));
        clienteRepo.save(cliente);

        String token = jwtServicio.generarToken(cliente.getEmail());
        return new Respuesta(token);
    }

    public Respuesta login(Solicitud req) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword())
        );
        String token = jwtServicio.generarToken(req.getEmail());
        return new Respuesta(token);
    }
}
