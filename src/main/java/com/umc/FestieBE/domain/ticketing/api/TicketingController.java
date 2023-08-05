package com.umc.FestieBE.domain.ticketing.api;
import com.umc.FestieBE.domain.ticketing.application.TicketingService;
import com.umc.FestieBE.domain.ticketing.dto.TicketingRequestDTO;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RequestMapping("/ticketing")
@RequiredArgsConstructor
@RestController
public class TicketingController {
    private final TicketingService ticketingService;

    @PostMapping("")
    public ResponseEntity<Void> createTicketing(@RequestPart TicketingRequestDTO request,
                                                @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail){
        ticketingService.createTicketing(request, images, thumbnail);
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

    @GetMapping("/{ticketingId}")
    public ResponseEntity<TicketingResponseDTO> getTicketing(@PathVariable("ticketingId") Long ticketingId) {
        return ResponseEntity.ok().body(ticketingService.getTicketing(ticketingId));
    }
}
