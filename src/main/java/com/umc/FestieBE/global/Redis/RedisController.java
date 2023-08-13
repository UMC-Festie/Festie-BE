package com.umc.FestieBE.global.Redis;

import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.ticketing.application.TicketingService;
import com.umc.FestieBE.domain.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RedisController {
    // TODO 7일된 캐시 또는 8개 이상 캐시 쌓이면 기존 캐시 삭제 로직 
    
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private FestivalService festivalService;

    @GetMapping("/getRecentFestivals")
    public ResponseEntity<List<Map<String, String>>> getRecentFestivals(@AuthenticationPrincipal User user) {
        List<Map<String, String>> recentFestivals = festivalService.getRecentFestivals(user.getId());
        return new ResponseEntity<>(recentFestivals, HttpStatus.OK);
    }

    // Redis에 있는 캐시 삭제 (테스트용)
    @DeleteMapping("/deleteRecentFestivalCacheForTest")
    public ResponseEntity<String> deleteRecentFestivalCache(@AuthenticationPrincipal User user) {
        String cacheKey = "recentFestivals:" + user.getId();

        try {
            Boolean deleted = redisTemplate.delete(cacheKey);
            if (deleted != null && deleted) {
                return ResponseEntity.ok("Cache 삭제 성공");
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body("Cache 삭제 실패: 삭제할 Cache 내역 없음");
            }
        } catch (Exception e) {
            e.printStackTrace(); // 예외 정보를 콘솔에 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("해당 캐시 내역 삭제 중 오류 발생: " + e.getMessage());
        }
    }
}