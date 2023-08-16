package com.umc.FestieBE.domain.review.api;

import com.umc.FestieBE.domain.review.application.ReviewService;
import com.umc.FestieBE.domain.review.dto.ReviewRequestDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Collections;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    //게시글 등록
    @PostMapping("/review")
    //ResponseEntity<Void>는 서버가 클라이언트에게 어떠한 데이터를 반환하지 않는 경우에 사용된다(응답 본문이 없다는 것을 뜻함), 예를 들어 성공적인 작업을 나타내는 응답, 아님 삭제요청의 결과들을 나타낼 때 자주 쓰인다.
    public ResponseEntity<Void> createReview(@RequestPart("reviewRequestDto") ReviewRequestDto reviewRequestDto,
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

}
