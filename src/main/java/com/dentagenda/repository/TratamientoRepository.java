package com.dentagenda.repository;

import com.dentagenda.model.Cita;
import com.dentagenda.model.Tratamiento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TratamientoRepository extends JpaRepository<Tratamiento, Long> {
    List<Tratamiento> findByPacienteRut(String rut);
    boolean existsByCita(Cita cita);
    Optional<Tratamiento> findByCita_Id(Long citaId);
    Optional<Tratamiento> findByCita(Cita cita);
}