package com.umc.FestieBE.domain.like_or_dislike.api;

import com.umc.FestieBE.domain.like_or_dislike.application.LikeOrDislikeService;
import com.umc.FestieBE.domain.like_or_dislike.dto.LikeOrDislikeRequestDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
public class LikeOrDislikeController {
    private final LikeOrDislikeService likeOrDislikeService;

    @PostMapping("/likes")
    public ResponseEntity<Void> createLikeOrDislike(@Valid @RequestBody LikeOrDislikeRequestDTO request){
        likeOrDislikeService.createLikeOrDislike(request);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/likes/{likeOrDislikeId}")
    public ResponseEntity<Void> cancelLikeOrDislike(@PathVariable("likeOrDislikeId") Long likeOrDislikeId) {
        likeOrDislikeService.cancelLikeOrDislike(likeOrDislikeId);
        return ResponseEntity.ok().build();
    }
}
