package com.umc.FestieBE.domain.ticketing.api;
import com.umc.FestieBE.domain.ticketing.application.TicketingService;
import com.umc.FestieBE.domain.ticketing.dto.TicketingRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RequestMapping("/ticketing")
@RequiredArgsConstructor
@RestController
public class TicketingController {
    private final TicketingService ticketingService;

    @PostMapping("")
    public ResponseEntity<Void> createTicketing(@Valid @RequestBody TicketingRequestDTO request){
        ticketingService.createTicketing(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{ticketingId}")
    public ResponseEntity<Void> deleteTicketing(@PathVariable Long ticketingId) {
        ticketingService.deleteTicketing(ticketingId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{ticketingId}")
    public ResponseEntity<Void> updateTicketing(@PathVariable Long ticketingId,
                                                @Valid @RequestBody TicketingRequestDTO request) {
        ticketingService.updateTicketing(ticketingId, request);
        return ResponseEntity.ok().build();
    }
}
