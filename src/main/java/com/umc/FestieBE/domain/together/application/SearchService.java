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
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static com.umc.FestieBE.global.exception.CustomErrorCode.INVALID_VALUE;


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
    public SearchResponseDTO.SearchListResponse getSearchResultList(String keyword, String boardType, String sort, int page) {
        List<SearchResponseDTO.SearchListDetailResponse> searchList = new ArrayList<>();
        int pageSize = 10;
        PageRequest pageRequest = PageRequest.of(page, pageSize);

        if (boardType.equals("전체")) {
            searchList.addAll(getOpenApiList(keyword, sort));
            searchList.addAll(getFestivalList(keyword, sort));
            searchList.addAll(getReviewList(keyword, sort));
            searchList.addAll(getTicketingList(keyword, sort));
            searchList.addAll(getTogetherList(keyword, sort));

            // 리스트 정렬
            sortSearchList(searchList, sort);

            return getPageData(searchList, page, pageSize);
        } else if (boardType.equals("정보보기")) {
            searchList = getOpenApiList(keyword, sort);
            return getPageData(searchList, page, pageSize);
        } else if (boardType.equals("정보공유")) {
            return getFestivalList(pageRequest, keyword, sort);
        } else if (boardType.equals("후기")) {
            return getReviewList(pageRequest, keyword, sort);
        } else if (boardType.equals("티켓팅")) {
            return getTicketingList(pageRequest, keyword, sort);
        } else if (boardType.equals("같이가요")) {
            return getTogetherList(pageRequest, keyword, sort);
        }
        throw new CustomException(INVALID_VALUE, "존재하지 않는 boardType 입니다. (전체/정보보기/정보공유/후기/티켓팅/같이가요)");
    }

    // '전체' 리스트 정렬
    // updatedAt(게시글 작성(수정) 날짜) 기준으로 정렬, 정보보기의 경우 맨 앞으로
    private void sortSearchList(List<SearchResponseDTO.SearchListDetailResponse> searchList, String sort) {
        Comparator<SearchResponseDTO.SearchListDetailResponse> comparator;

        if (sort.equals("최신순")) {
            comparator = Comparator.comparing(
                    SearchResponseDTO.SearchListDetailResponse::getUpdatedAt,
                    Comparator.nullsFirst(Comparator.reverseOrder())
            );
        } else if (sort.equals("오래된순")) {
            comparator = Comparator.comparing(
                    SearchResponseDTO.SearchListDetailResponse::getUpdatedAt,
                    Comparator.nullsFirst(Comparator.naturalOrder())
            );
        } else if (sort.equals("조회높은순")) {
            comparator = Comparator.comparing(
                    SearchResponseDTO.SearchListDetailResponse::getView,
                    Comparator.nullsFirst(Comparator.reverseOrder())
            );
        } else if (sort.equals("조회낮은순")) {
            comparator = Comparator.comparing(
                    SearchResponseDTO.SearchListDetailResponse::getView,
                    Comparator.nullsFirst(Comparator.naturalOrder())
            );
        } else {
            throw new CustomException(INVALID_VALUE, "존재하지 않는 정렬 조건입니다. (정렬 조건: 최신순/오래된순/조회높은순/조회낮은순)");
        }

        searchList.sort(comparator);
    }

    // 정보보기 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getOpenApiList(String keyword, String sort) {
        List<SearchResponseDTO.SearchListDetailResponse> data = new ArrayList<>();

        // 축제
        List<OpenFestival> openFestivalList = openFestivalRepository.findByTitle(keyword, sort);
        data.addAll(openFestivalList.stream()
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
        data.addAll(openPerformanceList.stream()
                .map(op -> {
                    Long view = 0L;
                    Long likeCount = op.getLikeOrDislikes().stream()
                            .filter(ld -> ld.getStatus() == 1)
                            .count();
                    return new SearchResponseDTO.SearchListDetailResponse(op, view, likeCount);
                }) //TODO 조회수, 좋아요 개수
                .collect(Collectors.toList()));

        return data;
    }

    // 정보공유 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getFestivalList(String keyword, String sort) {
        List<Festival> festivalList = festivalRepository.findByTitleAndContent(keyword, sort);
        return festivalList.stream()
                .map(f -> new SearchResponseDTO.SearchListDetailResponse(f)) //TODO 좋아요 개수
                .collect(Collectors.toList());
    }
    private SearchResponseDTO.SearchListResponse getFestivalList(PageRequest pageRequest, String keyword, String sort) {
        Page<Festival> festivalList = festivalRepository.findByTitleAndContent(pageRequest, keyword, sort);
        List<SearchResponseDTO.SearchListDetailResponse> data = festivalList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList());
        return new SearchResponseDTO.SearchListResponse(
                festivalList.getTotalElements(),
                festivalList.getNumber(),
                festivalList.hasNext(),
                festivalList.hasPrevious(),
                data);
    }

    // 후기 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getReviewList(String keyword, String sort) {
        List<Review> reviewList = reviewRepository.findByTitleAndContent(keyword, sort);
        return reviewList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new) //TODO 좋아요 개수
                .collect(Collectors.toList());
    }
    private SearchResponseDTO.SearchListResponse getReviewList(PageRequest pageRequest, String keyword, String sort) {
        Page<Review> reviewList = reviewRepository.findByTitleAndContent(pageRequest, keyword, sort);
        List<SearchResponseDTO.SearchListDetailResponse> data = reviewList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList());
        return new SearchResponseDTO.SearchListResponse(
                reviewList.getTotalElements(),
                reviewList.getNumber(),
                reviewList.hasNext(),
                reviewList.hasPrevious(),
                data);
    }


    // 티켓팅 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getTicketingList(String keyword, String sort) {
        List<Ticketing> ticketingList = ticketingRepository.findByTitleAndContent(keyword, sort);
        return ticketingList.stream()
                .map(t -> new SearchResponseDTO.SearchListDetailResponse(t)) //TODO 좋아요 개수
                .collect(Collectors.toList());
    }
    private SearchResponseDTO.SearchListResponse getTicketingList(PageRequest pageRequest, String keyword, String sort) {
        Page<Ticketing> ticketingList = ticketingRepository.findByTitleAndContent(pageRequest, keyword, sort);
        List<SearchResponseDTO.SearchListDetailResponse> data = ticketingList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList());
        return new SearchResponseDTO.SearchListResponse(
                ticketingList.getTotalElements(),
                ticketingList.getNumber(),
                ticketingList.hasNext(),
                ticketingList.hasPrevious(),
                data);
    }

    // 같이가요 검색
    private List<SearchResponseDTO.SearchListDetailResponse> getTogetherList(String keyword, String sort) {
        List<Together> togetherList = togetherRepository.findByTitleAndContent(keyword, sort);
        return togetherList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList());
    }
    private SearchResponseDTO.SearchListResponse getTogetherList(PageRequest pageRequest, String keyword, String sort) {
        Page<Together> togetherList = togetherRepository.findByTitleAndContent(pageRequest, keyword, sort);
        List<SearchResponseDTO.SearchListDetailResponse> data = togetherList.stream()
                .map(SearchResponseDTO.SearchListDetailResponse::new)
                .collect(Collectors.toList());
        return new SearchResponseDTO.SearchListResponse(
                togetherList.getTotalElements(),
                togetherList.getNumber(),
                togetherList.hasNext(),
                togetherList.hasPrevious(),
                data);
    }


    // 리스트 페이지네이션 처리
    private SearchResponseDTO.SearchListResponse getPageData(
            List<SearchResponseDTO.SearchListDetailResponse> data, int currentPage, int pageSize) {

        int fromIndex = currentPage * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, data.size());

        boolean hasNextPage = toIndex < data.size();
        boolean hasPreviousPage = currentPage > 0;

        return new SearchResponseDTO.SearchListResponse(
                Long.valueOf(data.size()),
                currentPage,
                hasNextPage,
                hasPreviousPage,
                data.subList(fromIndex, toIndex));
    }

}
