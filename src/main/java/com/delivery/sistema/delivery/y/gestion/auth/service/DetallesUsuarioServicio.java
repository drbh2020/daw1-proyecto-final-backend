package com.delivery.sistema.delivery.y.gestion.auth.service;

import com.delivery.sistema.delivery.y.gestion.cliente.model.Cliente;
import com.delivery.sistema.delivery.y.gestion.cliente.repository.ClienteRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service

public class DetallesUsuarioServicio implements UserDetailsService {


    private final ClienteRepository clienteRepo;

    public DetallesUsuarioServicio(ClienteRepository clienteRepo) {

        this.clienteRepo = clienteRepo;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Cliente cliente = clienteRepo.findByEmail(email)
        .orElseThrow(()-> new UsernameNotFoundException("usuario no encontrado con email: " + email));


        return User.builder()
                .username(cliente.getEmail())
                .password(cliente.getPassword())
                .roles(cliente.getRoles().stream()
                        .map(r -> r.getNombre().replace("ROL_", "")) // extraer roles sin el prefijo ROL_
                        .toArray(String[]::new))
                .build();
    }
}