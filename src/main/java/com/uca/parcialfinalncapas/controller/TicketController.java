package com.uca.parcialfinalncapas.controller;

import com.uca.parcialfinalncapas.dto.request.TicketCreateRequest;
import com.uca.parcialfinalncapas.dto.request.TicketUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.TicketResponse;
import com.uca.parcialfinalncapas.service.TicketService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tickets")
@AllArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /** USER puede crear tickets */
    @PreAuthorize("hasRole('USER')")
    @PostMapping
    public ResponseEntity<TicketResponse> createTicket(
            @Valid @RequestBody TicketCreateRequest req) {
        return ticketService.createTicket(req);
    }


    @PreAuthorize("hasAnyRole('USER','TECH')")
    @GetMapping
    public ResponseEntity<List<TicketResponse>> getTickets(Authentication auth) {
        boolean isTech = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECH"));

        if (isTech) {
            return ticketService.getAllTickets();
        } else {
            return ticketService.getTicketsByUsuario(auth.getName());
        }
    }

    /** USER ve sólo su propio ticket; TECH cualquiera */
    @PreAuthorize("hasAnyRole('USER','TECH')")
    @GetMapping("/{id}")
    public ResponseEntity<TicketResponse> getTicketById(
            @PathVariable Long id,
            Authentication auth) {
        ResponseEntity<TicketResponse> respEntity = ticketService.getTicketById(id);
        TicketResponse resp = respEntity.getBody();

        boolean isTech = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_TECH"));

        if (isTech || (resp != null && resp.getCorreoSolicitante().equals(auth.getName()))) {
            return respEntity;
        }

        throw new AccessDeniedException("No autorizado para ver este ticket");
    }

    /** Sólo TECH puede actualizar */
    @PreAuthorize("hasRole('TECH')")
    @PutMapping
    public ResponseEntity<TicketResponse> updateTicket(
            @Valid @RequestBody TicketUpdateRequest req) {
        return ticketService.updateTicket(req);
    }

    /** Sólo TECH puede eliminar */
    @PreAuthorize("hasRole('TECH')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTicket(@PathVariable Long id) {
        return ticketService.deleteTicket(id);
    }
}
