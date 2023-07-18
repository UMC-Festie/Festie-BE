package com.umc.FestieBE.domain.ticketing.api;
import com.umc.FestieBE.domain.ticketing.application.TicketingService;
import com.umc.FestieBE.domain.ticketing.dto.TicketingDTO;
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
    public ResponseEntity<Void> createTicketing(@Valid @RequestBody TicketingDTO.TicketingRequest request){
        ticketingService.createTicketing(request);
        return ResponseEntity.ok().build();
    }
}