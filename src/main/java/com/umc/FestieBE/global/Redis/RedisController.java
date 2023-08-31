package com.umc.FestieBE.global.Redis;

import com.umc.FestieBE.domain.applicant_info.application.ApplicantInfoService;
import com.umc.FestieBE.domain.festival.application.FestivalService;
import com.umc.FestieBE.domain.open_festival.application.OpenFestivalService;
import com.umc.FestieBE.domain.open_performance.application.OpenPerformanceService;
import com.umc.FestieBE.domain.together.application.TogetherService;
import com.umc.FestieBE.domain.user.domain.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class RedisController {
    @Autowired
    private RedisTemplate<String, String> redisTemplate;

    @Autowired
    private OpenFestivalService openFestivalService;
    @Autowired
    private OpenPerformanceService openPerformanceService;

    @Autowired
    private ApplicantInfoService applicantInfoService;
    @Autowired
    private FestivalService festivalService;
    @Autowired
    private TogetherService togetherService;


    /** 정보보기 (축제) 최근 내역 */
    @GetMapping("/getRecentOpenFestivals")
    public ResponseEntity<List<Map<String, String>>> getRecentOpenFestivals(@AuthenticationPrincipal User user) {
        List<Map<String, String>> recentOpenFestivals = openFestivalService.getRecentOpenFestivals(user.getId());
        return new ResponseEntity<>(recentOpenFestivals, HttpStatus.OK);
    }

    /** 정보보기 (공연) 최근 내역 */
    @GetMapping("/getRecentOpenPerformances")
    public ResponseEntity<List<Map<String, String>>> getRecentOpenPerformances(@AuthenticationPrincipal User user) {
        List<Map<String, String>> recentOpenPerformances = openPerformanceService.getRecentOpenPerformances(user.getId());
        return new ResponseEntity<>(recentOpenPerformances, HttpStatus.OK);
    }

    /** 정보공유 (새로운 공연/축제) 최근내역 */
    @GetMapping("/getRecentFestivals")
    public ResponseEntity<List<Map<String, String>>> getRecentFestivals(@AuthenticationPrincipal User user) {
        List<Map<String, String>> recentFestivals = festivalService.getRecentFestivals(user.getId(), "축제");
        return new ResponseEntity<>(recentFestivals, HttpStatus.OK);
    }

    /** 정보공유 (새로운 공연/축제) 최근내역 */
    @GetMapping("/getRecentPerformances")
    public ResponseEntity<List<Map<String, String>>> getRecentPerformances(@AuthenticationPrincipal User user) {
        List<Map<String, String>> recentFestivals = festivalService.getRecentFestivals(user.getId(), "공연");
        return new ResponseEntity<>(recentFestivals, HttpStatus.OK);
    }

    /** 최근 내역 테스트용 */
    // Redis에 있는 정보공유 관련 캐시 삭제 (테스트할때 Redis 명령창에서 삭제 하지 말고 DeleteMapping에서 바로 삭제하면서 테스트 가능!)
    @DeleteMapping("/deleteCache/{cacheType}")
    public ResponseEntity<String> deleteRecentFestivalCache(@AuthenticationPrincipal User user,
                                                            @PathVariable("cacheType") String cacheType) {
        String cacheKey;

        switch (cacheType) {
            case "정보보기-축제":
                cacheKey = "recentOpenFestivals:" + user.getId();
                break;
            case "정보보기-공연":
                cacheKey = "recentOpenPerformances:" + user.getId();
                break;
            case "정보보기":
                cacheKey = "recentOpenAPIs:" + user.getId();
                break;
            case "정보공유":
                cacheKey = "recentFestivals:" + user.getId();
                break;
            case "같이가요":
                cacheKey = "recentTogethers:" + user.getId();
                break;
            default:
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최근내역 삭제 시, 캐시 타입 불일치 오류");
        }

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