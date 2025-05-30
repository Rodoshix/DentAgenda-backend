package com.dentagenda.repository;

import com.dentagenda.model.Recepcionista;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RecepcionistaRepository extends JpaRepository<Recepcionista, Long> {
    Optional<Recepcionista> findByRut(String rut);
}