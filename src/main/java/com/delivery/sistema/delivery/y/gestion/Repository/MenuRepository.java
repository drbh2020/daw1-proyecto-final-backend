package com.delivery.sistema.delivery.y.gestion.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.delivery.sistema.delivery.y.gestion.Entity.Menu;

public interface MenuRepository extends JpaRepository<Menu, Long> {
}
