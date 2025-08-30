package com.delivery.sistema.delivery.y.gestion.auth.service;


import com.delivery.sistema.delivery.y.gestion.auth.dto.RegistroClienteDto;
import com.delivery.sistema.delivery.y.gestion.auth.dto.LoginRequestDto;
import com.delivery.sistema.delivery.y.gestion.auth.dto.LoginResponseDto;
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

    public LoginResponseDto registrar(RegistroClienteDto registro) {
        Cliente cliente = new Cliente();
        cliente.setNombre(registro.getNombre());
        cliente.setEmail(registro.getEmail());
        cliente.setPassword(passwordEncoder.encode(registro.getPassword()));
        cliente.setDireccion(registro.getDireccion());
        clienteRepository.save(cliente);

        String token = jwtServicio.generarToken(registro.getEmail());
        return new LoginResponseDto(token);
    }

    public LoginResponseDto login(LoginRequestDto loginRequestDto) {
        authManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequestDto.getEmail(), loginRequestDto.getPassword())
        );
        String token = jwtServicio.generarToken(loginRequestDto.getEmail());
        return new LoginResponseDto(token);
    }

}
