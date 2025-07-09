package com.uca.parcialfinalncapas.service;

import com.uca.parcialfinalncapas.dto.request.TicketCreateRequest;
import com.uca.parcialfinalncapas.dto.request.TicketUpdateRequest;
import com.uca.parcialfinalncapas.dto.response.TicketResponse;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface TicketService {
    ResponseEntity<TicketResponse> createTicket(TicketCreateRequest ticket);
    ResponseEntity<List<TicketResponse>> getAllTickets();
    ResponseEntity<List<TicketResponse>> getTicketsByUsuario(String correoUsuario);
    ResponseEntity<TicketResponse> getTicketById(Long id);
    ResponseEntity<TicketResponse> updateTicket(TicketUpdateRequest ticket);
    ResponseEntity<Void> deleteTicket(Long id);
}
