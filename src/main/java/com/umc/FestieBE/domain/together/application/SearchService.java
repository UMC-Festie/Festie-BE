package com.umc.FestieBE.domain.together.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
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

        // 전체


        // 정보보기 (축제)
        //List<OpenFestival> openFestivalList = openFestivalRepository.findByTitleAndContent(keyword, sort);
        List<OpenFestival> openFestivalList = openFestivalRepository.findByTitleAndContent(keyword);
        searchList.addAll(openFestivalList.stream()
                .map(of -> new SearchResponseDTO.SearchListDetailResponse(of, 0L, 0L)) //TODO 조회수, 좋아요 개수
                .collect(Collectors.toList()));
        // 정보보기 (공연)
        //List<OpenPerformance> openPerformanceList = openPerformanceRepository.findByTitleAndContent(keyword, sort);
        List<OpenPerformance> openPerformanceList = openPerformanceRepository.findByTitleAndContent(keyword);
        searchList.addAll(openPerformanceList.stream()
                .map(op -> new SearchResponseDTO.SearchListDetailResponse(op, 0L, 0L)) //TODO 조회수, 좋아요 개수
                .collect(Collectors.toList()));

        // 정보공유
        List<Festival> festivalList = festivalRepository.findByTitleAndContent(keyword, sort);
        searchList.addAll(festivalList.stream()
                .map(f -> new SearchResponseDTO.SearchListDetailResponse(f, 0L)) //TODO 좋아요 개수
                .collect(Collectors.toList()));

        // 티켓팅
        List<Ticketing> ticketingList = ticketingRepository.findByTitleAndContent(keyword, sort);
        searchList.addAll(ticketingList.stream()
                .map(t -> new SearchResponseDTO.SearchListDetailResponse(t, 0L)) //TODO 좋아요 개수
                .collect(Collectors.toList()));

        // 후기
        List<Review> reviewList = reviewRepository.findByTitleAndContent(keyword, sort);
        searchList.addAll(reviewList.stream()
                .map(r -> new SearchResponseDTO.SearchListDetailResponse(r, 0L)) //TODO 좋아요 개수
                .collect(Collectors.toList()));

        // 같이가요
        List<Together> togetherList = togetherRepository.findByTitleAndContent(keyword, sort);
        searchList.addAll(togetherList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList()));

        return new SearchResponseDTO.SearchListResponse(searchList);
    }
}
