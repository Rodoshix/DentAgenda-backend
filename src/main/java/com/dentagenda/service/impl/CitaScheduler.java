package com.dentagenda.service.impl;

import com.dentagenda.model.Cita;
import com.dentagenda.model.EstadoCita;
import com.dentagenda.repository.CitaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class CitaScheduler {

    @Autowired
    private CitaRepository citaRepository;

    @Scheduled(fixedRate = 300000) // Cada 5 minutos
    public void actualizarEstadoCitas() {
        LocalDateTime ahora = LocalDateTime.now();

        List<Cita> citasPendientes = citaRepository.findByEstadoIn(List.of(EstadoCita.PENDIENTE, EstadoCita.CONFIRMADA));

        for (Cita cita : citasPendientes) {
            LocalDateTime horaCita = cita.getFechaHora();

            if (horaCita.plusMinutes(40).isBefore(ahora)) {
                if (cita.getEstado() == EstadoCita.CONFIRMADA) {
                    cita.setEstado(EstadoCita.ATENDIDA);
                } else {
                    cita.setEstado(EstadoCita.NO_ASISTIO);
                }
                citaRepository.save(cita);
            }
        }
    }
}