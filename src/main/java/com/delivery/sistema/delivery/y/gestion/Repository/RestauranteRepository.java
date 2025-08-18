package com.delivery.sistema.delivery.y.gestion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delivery.sistema.delivery.y.gestion.Entity.Restaurante;

public interface RestauranteRepository extends JpaRepository<Restaurante, Long> {
}
