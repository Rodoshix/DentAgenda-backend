package com.dentagenda.repository;

import com.dentagenda.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {
    List<Tratamiento> findByPacienteRut(String rut);
}