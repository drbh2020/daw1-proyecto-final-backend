package com.delivery.sistema.delivery.y.gestion.shared.repository;

import com.delivery.sistema.delivery.y.gestion.shared.model.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {

    Optional<Configuracion> findByConfigKey(String configKey);

    @Query("SELECT c.configValue FROM Configuracion c WHERE c.configKey = :configKey")
    Optional<String> findValueByConfigKey(@Param("configKey") String configKey);

    @Query("SELECT c FROM Configuracion c WHERE c.configKey LIKE %:prefijo%")
    List<Configuracion> findByConfigKeyContaining(@Param("prefijo") String prefijo);

    boolean existsByConfigKey(String configKey);

    @Query("SELECT c FROM Configuracion c ORDER BY c.configKey ASC")
    List<Configuracion> findAllOrderByConfigKey();

    @Query("SELECT COUNT(c) FROM Configuracion c")
    Long countAllConfiguraciones();

    void deleteByConfigKey(String configKey);
}