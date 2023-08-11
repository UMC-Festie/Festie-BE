package com.umc.FestieBE.domain.ticketing.api;
import com.umc.FestieBE.domain.ticketing.application.TicketingService;
import com.umc.FestieBE.domain.ticketing.dto.TicketingRequestDTO;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@RequestMapping("/ticketing")
@RequiredArgsConstructor
@RestController
public class TicketingController {
    private final TicketingService ticketingService;

    /** 등록 */
    @PostMapping("")
    public ResponseEntity<Void> createTicketing(@RequestPart TicketingRequestDTO request,
                                                @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail){
        if (images == null) { // 이미지 첨부 안하는 경우 처리
            images = Collections.emptyList();
        }
        ticketingService.createTicketing(request, images, thumbnail);
        return ResponseEntity.ok().build();
    }

    /** 삭제 */
    @DeleteMapping("/{ticketingId}")
    public ResponseEntity<Void> deleteTicketing(@PathVariable Long ticketingId) {
        ticketingService.deleteTicketing(ticketingId);
        return ResponseEntity.ok().build();
    }

    /** 수정 */
    @PutMapping("/{ticketingId}")
    public ResponseEntity<Void> updateTicketing(@PathVariable Long ticketingId,
                                                @RequestPart TicketingRequestDTO request,
                                                @RequestPart(value = "images", required = false) List<MultipartFile> images,
                                                @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail) {
        if (images == null) { // 이미지 첨부 안하는 경우 처리
            images = Collections.emptyList();
        }
        ticketingService.updateTicketing(ticketingId, request, images, thumbnail);
        return ResponseEntity.ok().build();
    }

    /** 상세 조회 */
    @GetMapping("/{ticketingId}")
    public ResponseEntity<TicketingResponseDTO.TicketingDetailResponse> getTicketing(
            @PathVariable("ticketingId") Long ticketingId,
            HttpServletRequest httpServletRequest)
    {
        return ResponseEntity.ok().body(ticketingService.getTicketing(ticketingId, httpServletRequest));
    }

    /** 목록 조회 Pagination (무한스크롤 X) */
    @GetMapping("")
    public List<TicketingResponseDTO.TicketingPaginationResponse> getTicketingList(
            @RequestParam(required = false, defaultValue = "최신순") String sortBy,
            @RequestParam(required = false, defaultValue = "0") Integer page) {
        Pageable pageRequest = PageRequest.of(page, 6);
        return ticketingService.fetchTicketingPage(sortBy, pageRequest);
    }
}
