package com.dentagenda.service.impl;

import com.dentagenda.dto.BloquearHorarioDTO;
import com.dentagenda.model.BloqueoAgenda;
import com.dentagenda.model.Odontologo;
import com.dentagenda.repository.BloqueoAgendaRepository;
import com.dentagenda.repository.OdontologoRepository;
import com.dentagenda.service.BloqueoAgendaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BloqueoAgendaServiceImpl implements BloqueoAgendaService {

    @Autowired
    private BloqueoAgendaRepository bloqueoAgendaRepository;

    @Autowired
    private OdontologoRepository odontologoRepository;

    @Override
    public BloqueoAgenda bloquearHorario(BloquearHorarioDTO dto) {
        Odontologo odontologo = odontologoRepository.findByRut(dto.getRutOdontologo())
                .orElseThrow(() -> new RuntimeException("Odontólogo no encontrado"));

        BloqueoAgenda bloqueo = new BloqueoAgenda();
        bloqueo.setFecha(dto.getFecha());
        bloqueo.setHoraInicio(dto.getHoraInicio());
        bloqueo.setHoraFin(dto.getHoraFin());
        bloqueo.setMotivo(dto.getMotivo());
        bloqueo.setOdontologo(odontologo);

        return bloqueoAgendaRepository.save(bloqueo);
    }
}
