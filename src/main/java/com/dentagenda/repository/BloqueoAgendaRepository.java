package com.dentagenda.repository;

import com.dentagenda.model.BloqueoAgenda;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface BloqueoAgendaRepository extends JpaRepository<BloqueoAgenda, Long> {
    List<BloqueoAgenda> findByOdontologoRutAndFecha(String rut, LocalDate fecha);
}