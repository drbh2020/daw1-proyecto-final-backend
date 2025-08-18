package com.delivery.sistema.delivery.y.gestion.Repository;


import org.springframework.data.jpa.repository.JpaRepository;
import com.delivery.sistema.delivery.y.gestion.Entity.Cliente;
import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

    Optional<Cliente> findByEmail(String email);

}


