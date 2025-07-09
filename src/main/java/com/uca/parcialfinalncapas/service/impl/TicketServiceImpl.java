package com.uca.parcialfinalncapas.service.impl;

import com.uca.parcialfinalncapas.dto.request.TicketCreateRequest;
import com.uca.parcialfinalncapas.dto.request.TicketUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.TicketResponse;
import com.uca.parcialfinalncapas.entities.Ticket;
import com.uca.parcialfinalncapas.entities.User;
import com.uca.parcialfinalncapas.exceptions.TicketNotFoundException;
import com.uca.parcialfinalncapas.repository.TicketRepository;
import com.uca.parcialfinalncapas.repository.UserRepository;
import com.uca.parcialfinalncapas.service.TicketService;
import com.uca.parcialfinalncapas.utils.mappers.TicketMapper;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public ResponseEntity<TicketResponse> createTicket(TicketCreateRequest ticketReq) {
        // Aquí asumes que ticketReq trae los correos, los pasamos directamente al mapper
        Ticket created = ticketRepository.save(
                TicketMapper.toEntityCreate(
                        ticketReq,
                        /* usuarioId */ getUserIdByCorreo(ticketReq.getCorreoUsuario()),
                        /* tecnicoId */ getUserIdByCorreo(ticketReq.getCorreoSoporte())
                )
        );
        return ResponseEntity
                .status(201)
                .body(
                        TicketMapper.toDTO(
                                created,
                                ticketReq.getCorreoUsuario(),
                                ticketReq.getCorreoSoporte()
                        )
                );
    }

    @Override
    public ResponseEntity<List<TicketResponse>> getAllTickets() {
        List<TicketResponse> list = ticketRepository.findAll().stream()
                .map(this::mapToDtoWithEmails)
                .toList();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<List<TicketResponse>> getTicketsByUsuario(String correoUsuario) {
        List<TicketResponse> list = ticketRepository.findAll().stream()
                .map(this::mapToDtoWithEmails)
                .filter(tr -> tr.getCorreoSolicitante().equals(correoUsuario))
                .toList();
        return ResponseEntity.ok(list);
    }

    @Override
    public ResponseEntity<TicketResponse> getTicketById(Long id) {
        Ticket t = ticketRepository.findById(id)
                .orElseThrow(() -> new TicketNotFoundException("Ticket no encontrado con ID: " + id));
        return ResponseEntity.ok(mapToDtoWithEmails(t));
    }

    @Override
    @Transactional
    public ResponseEntity<TicketResponse> updateTicket(TicketUpdateRequest ticketReq) {
        Ticket existing = ticketRepository.findById(ticketReq.getId())
                .orElseThrow(() -> new TicketNotFoundException("Ticket no encontrado: " + ticketReq.getId()));
        Ticket updated = ticketRepository.save(
                TicketMapper.toEntityUpdate(ticketReq, /*nuevoSoporteId*/ getUserIdByCorreo(ticketReq.getCorreoSoporte()), existing)
        );
        return ResponseEntity.ok(mapToDtoWithEmails(updated));
    }

    @Override
    public ResponseEntity<Void> deleteTicket(Long id) {
        ticketRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Helper: mapea un Ticket a DTO incluyendo la resolución de correos
     */
    private TicketResponse mapToDtoWithEmails(Ticket t) {
        String correoSolicitante = userRepository.findById(t.getUsuarioId())
                .map(User::getCorreo)
                .orElse("unknown");
        String correoSoporte = t.getTecnicoAsignadoId() != null
                ? userRepository.findById(t.getTecnicoAsignadoId())
                .map(User::getCorreo)
                .orElse("unknown")
                : null;

        return TicketMapper.toDTO(t, correoSolicitante, correoSoporte);
    }

    /**
     * Helper: obtiene el userId a partir del correo. Lanza excepción si no existe.
     */
    private Long getUserIdByCorreo(String correo) {
        return userRepository.findByCorreo(correo)
                .map(User::getId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario no encontrado con correo: " + correo));
    }
}
