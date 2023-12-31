package com.umc.FestieBE.domain.review.api;

import com.umc.FestieBE.domain.review.application.ReviewService;
import com.umc.FestieBE.domain.review.dto.ReviewRequestDto;
import com.umc.FestieBE.domain.review.dto.ReviewResponseDto;
import com.umc.FestieBE.domain.ticketing.dto.TicketingResponseDTO;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;
@Slf4j
@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    /**게시글 등록 */
    @PostMapping("/review")
    //ResponseEntity<Void>는 서버가 클라이언트에게 어떠한 데이터를 반환하지 않는 경우에 사용된다(응답 본문이 없다는 것을 뜻함), 예를 들어 성공적인 작업을 나타내는 응답, 아님 삭제요청의 결과들을 나타낼 때 자주 쓰인다.
    public ResponseEntity<Void> createReview(@Valid @RequestPart(value = "request") ReviewRequestDto reviewRequestDto,
                                             @RequestPart(value="images", required = false) List<MultipartFile> images,
                                             @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail,
                                             HttpServletRequest request)

    //images와 thumbnail은 필수로 올려야 하는 것이 아니므로 required= false를 설정하였다
    {
        if (images == null) //이미지를 첨부 안할 때
            images = Collections.emptyList();

        reviewService.createReview(reviewRequestDto, images, thumbnail, request);

        return ResponseEntity.ok().build();
    }

    /** 상세 조회 */
    @GetMapping("/review/{reviewId}")
    public ResponseEntity<ReviewResponseDto.ReviewDetailResponse> getReview(
            @PathVariable("reviewId") Long reviewId,
            HttpServletRequest httpServletRequest)
    {
        return ResponseEntity.ok().body(reviewService.getReview(reviewId, httpServletRequest));
    }

    /** 목록 조회 Pagination (무한스크롤 X) */
    @GetMapping("")
    public ResponseEntity<ReviewResponseDto.ReviewListResponse> getReviewList(
            @RequestParam(required = false) Integer page,
            @RequestParam(required = false, defaultValue = "최신순") String sortBy)
    {
        return ResponseEntity.ok().body(reviewService.getReviewPage(page, sortBy));
    }

    /** 후기 게시물 삭제 **/
    @DeleteMapping("/review/{reviewId}")
    public ResponseEntity<Void> deleteReview(@PathVariable Long reviewId) {
        reviewService.deleteReview(reviewId);
        return ResponseEntity.ok().build();
    }

    /** 후기 게시물 수정 **/
    @PutMapping("/review/{reviewId}")
    public ResponseEntity<Void> updateReview(
            @PathVariable("reviewId") Long reviewId,
            @Valid @RequestPart(value = "data") ReviewRequestDto request,
            @RequestPart(value = "thumbnail", required = false) MultipartFile thumbnail
    ){
        reviewService.updateReview(reviewId, request, thumbnail);
        return ResponseEntity.ok().build();
    }

}


