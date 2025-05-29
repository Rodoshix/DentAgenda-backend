package com.dentagenda.repository;

import com.dentagenda.model.Odontologo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OdontologoRepository extends JpaRepository<Odontologo, Long> {

    Optional<Odontologo> findByRut(String rut);

    Optional<Odontologo> findByNombreIgnoreCase(String nombre);
}