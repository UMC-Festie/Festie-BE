package com.umc.FestieBE.domain.together.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.dao.ReviewRepository;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.ticketing.dao.TicketingRepository;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.together.dao.TogetherRepository;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.SearchResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class SearchService {

    private final FestivalRepository festivalRepository;
    private final OpenFestivalRepository openFestivalRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final ReviewRepository reviewRepository;
    private final TicketingRepository ticketingRepository;
    private final TogetherRepository togetherRepository;


    /**
     * 통합 검색
     */
    public SearchResponseDTO.SearchListResponse getSearchResultList(String keyword, String boardType, String sort){
        List<SearchResponseDTO.SearchListDetailResponse> searchList =  new ArrayList<>();

        if (boardType.equals("전체")) {
            searchList.addAll(getOpenApiList(keyword, sort));
            searchList.addAll(getFestivalList(keyword, sort));
            searchList.addAll(getReviewList(keyword, sort));
            searchList.addAll(getTicketingList(keyword, sort));
            searchList.addAll(getTogetherList(keyword, sort));
        } else if (boardType.equals("정보보기")) {
            searchList.addAll(getOpenApiList(keyword, sort));
        } else if (boardType.equals("정보공유")) {
            searchList.addAll(getFestivalList(keyword, sort));
        } else if (boardType.equals("후기")) {
            searchList.addAll(getReviewList(keyword, sort));
        } else if (boardType.equals("티켓팅")) {
            searchList.addAll(getTicketingList(keyword, sort));
        } else if (boardType.equals("같이가요")){
            searchList.addAll(getTogetherList(keyword, sort));
        }

        return new SearchResponseDTO.SearchListResponse(searchList);
    }

    // 정보보기 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getOpenApiList(String keyword, String sort){
        List<SearchResponseDTO.SearchListDetailResponse> searchList = new ArrayList<>();

        // 축제
        List<OpenFestival> openFestivalList = openFestivalRepository.findByTitle(keyword, sort);
        searchList.addAll(openFestivalList.stream()
                .map(of -> {
                    Long view = 0L;
                    Long likeCount = of.getLikeOrDislikes().stream()
                            .filter(ld -> ld.getStatus() == 1)
                            .count();
                    return new SearchResponseDTO.SearchListDetailResponse(of, view, likeCount);
                }) //TODO 조회수, 좋아요 개수
                .collect(Collectors.toList()));
        // 공연
        List<OpenPerformance> openPerformanceList = openPerformanceRepository.findByTitle(keyword, sort);
        searchList.addAll(openPerformanceList.stream()
                .map(op -> {
                    Long view = 0L;
                    Long likeCount = op.getLikeOrDislikes().stream()
                            .filter(ld -> ld.getStatus() == 1)
                            .count();
                    return new SearchResponseDTO.SearchListDetailResponse(op, view, likeCount);
                }) //TODO 조회수, 좋아요 개수
                .collect(Collectors.toList()));

        return searchList;
    }

    // 정보공유 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getFestivalList(String keyword, String sort){
        List<Festival> festivalList = festivalRepository.findByTitleAndContent(keyword, sort);
        return festivalList.stream()
                .map(f -> new SearchResponseDTO.SearchListDetailResponse(f)) //TODO 좋아요 개수
                .collect(Collectors.toList());
    }

    // 후기 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getReviewList(String keyword, String sort){
        List<Review> reviewList = reviewRepository.findByTitleAndContent(keyword, sort);
        return reviewList.stream()
                .map(r -> {
                    Long likeCount = r.getLikeOrDislikes().stream()
                            .filter(ld -> ld.getStatus() == 1)
                            .count();
                    return new SearchResponseDTO.SearchListDetailResponse(r, likeCount);
                }) //TODO 좋아요 개수
                .collect(Collectors.toList());
    }

    // 티켓팅 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getTicketingList(String keyword, String sort){
        List<Ticketing> ticketingList = ticketingRepository.findByTitleAndContent(keyword, sort);
        return ticketingList.stream()
                .map(t -> new SearchResponseDTO.SearchListDetailResponse(t)) //TODO 좋아요 개수
                .collect(Collectors.toList());
    }

    // 같이가요 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getTogetherList(String keyword, String sort){
        List<Together> togetherList = togetherRepository.findByTitleAndContent(keyword, sort);
        return togetherList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList());
    }


}
