package com.umc.FestieBE.domain.review.application;

import com.umc.FestieBE.domain.festival.dao.FestivalRepository;
import com.umc.FestieBE.domain.festival.domain.Festival;
import com.umc.FestieBE.domain.festival.dto.FestivalLinkReviewResponseDTO;
import com.umc.FestieBE.domain.image.dao.ImageRepository;
import com.umc.FestieBE.domain.image.domain.Image;

import com.umc.FestieBE.domain.like_or_dislike.dao.LikeOrDislikeRepository;
import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;

import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;

import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.review.dao.ReviewRepository;
import com.umc.FestieBE.domain.review.domain.Review;
import com.umc.FestieBE.domain.review.dto.ReviewRequestDto;
import com.umc.FestieBE.domain.review.dto.ReviewResponseDto;
import com.umc.FestieBE.domain.ticketing.domain.Ticketing;
import com.umc.FestieBE.domain.ticketing.dto.TicketingRequestDTO;
import com.umc.FestieBE.domain.together.domain.Together;
import com.umc.FestieBE.domain.together.dto.TogetherRequestDTO;
import com.umc.FestieBE.domain.token.JwtTokenProvider;
import com.umc.FestieBE.domain.user.dao.UserRepository;
import com.umc.FestieBE.domain.user.domain.User;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import com.umc.FestieBE.global.image.AwsS3Service;
import com.umc.FestieBE.global.type.CategoryType;
import com.umc.FestieBE.global.type.FestivalType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import java.util.stream.Collectors;

import java.util.concurrent.atomic.AtomicReference;


import static com.umc.FestieBE.global.exception.CustomErrorCode.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final AwsS3Service awsS3Service;
    private final ImageRepository imageRepository;
    private final LikeOrDislikeRepository likeOrDislikeRepository;
    private final FestivalRepository festivalRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final OpenFestivalRepository openFestivalRepository;


    /** 후기글 등록 **/

    @Transactional
    public void createReview(ReviewRequestDto reviewRequestDto, List<MultipartFile> images, MultipartFile thumbnail, HttpServletRequest request) {
        //유저 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserIdByServlet(request)) //jwtTokenProvider의 getUserIdByServlet를 통해 userId 값을 가져온다
                .orElseThrow(()-> new CustomException(USER_NOT_FOUND)); //만약 일치하는 사용자가 없으면 global에서 customErrorCode의 USER_NOT_FOUND 코드를 가져온다

        // 공연/축제 정보 연동 시 DB 에서 확인
        if (reviewRequestDto.getFestivalId() != null) {
            checkIfFestivalIdExists(reviewRequestDto.getFestivalId(), reviewRequestDto.getBoardType(), reviewRequestDto.getFestivalType());
        }

        // Review 게시글 등록
        FestivalType festivalType = FestivalType.findFestivalType(reviewRequestDto.getFestivalType()); // findFestivalType은 enum의 모든 값을 array로 가져와서 주어진 festivalType와 일치하는 값을 필터링 해준다.
        CategoryType categoryType = CategoryType.findCategoryType(reviewRequestDto.getCategoryType());

        int maxImage = 5;
        if (images.size() > maxImage)
            throw new CustomException(IMAGE_UPLOAD_LIMIT_EXCEEDED); // 최대 이미지 업로드 수는 5개

        String thumbnailUrl = null;
        if(!thumbnail.isEmpty())
            thumbnailUrl = awsS3Service.uploadImgFile(thumbnail);

        Review review = reviewRequestDto.toEntity(user, festivalType, categoryType, thumbnailUrl);
        reviewRepository.save(review);

        //이미지 파일 업로드 후, s3에서 url get
        List<String> imagesUrl = null; // 초기화 되지 않은 상태로 선언이 되었기에 변수에는 아무 값이 없다
        if (!images.isEmpty()) {
            imagesUrl = new ArrayList<>(); //만약 images 리스트에 이미지 파일이 존재한다면, 이미지 파일의 url을 저장하기위해 imagesUrl 리스트를 초기화하고 값을 추가한다.
            for (MultipartFile image : images) {
                String image_Url = awsS3Service.uploadImgFile(image);// uploadImgFile 메소드를 통해 현재 이미지 파일을 업로드 하고, 해당 이미지의 url을 image_Url에 저장한다
                Image img = Image.builder().review(review).imageUrl(image_Url).build();
                imageRepository.save(img);
                // images 리스트가 비어있지 않은 경우에만 업로드 작업이 실행되고, 각 이미지에 대해 해당 이미지의 url(image_Url)을 imageUrl 리스트에 추가한다.
            }
        }


    }

    private void checkIfFestivalIdExists(String festivalId, String boardType, Integer festivalType){
        switch (boardType) {
            case "정보공유":
                festivalRepository.findById(Long.valueOf(festivalId))
                        .orElseThrow(() -> new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND));
                break;

            case "정보보기":
                if (festivalType == 0) { //공연
                    openPerformanceRepository.findById(festivalId);
                } else if (festivalType == 1) { //축제
                    openFestivalRepository.findById(festivalId);
                }
                break;

            default:
                throw new CustomException(INVALID_VALUE, "공연/축제 게시글 유형은 '정보보기' 또는 '정보공유'만 가능합니다.");
        }
    }

    /**
     * 후기 게시물 상세 조회
     **/
    public ReviewResponseDto.ReviewDetailResponse getReview(Long reviewId, HttpServletRequest request) {
        // 조회수 업뎃
        reviewRepository.updateView(reviewId);

        // 후기 게시글 상세 조회
        Review review = reviewRepository.findByIdWithUser(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));
        //작성자 여부 확인-> 작성자가 아니면 수정, 삭제 권한 X
        boolean isWriter = false;
        Long userId = jwtTokenProvider.getUserIdByServlet(request);
        if (userId != null && userId == review.getUser().getId()) {
            isWriter = true;
        }

        // 이미지
        List<Image> imageList = imageRepository.findByReview(review);
        List<String> imageUrlList = imageList.stream()
                .map(Image::getImageUrl) // Image 객체에서 url 필드를 추출
                .collect(Collectors.toList());

        // 유저가 좋아요/싫어요를 눌렀는지 여부 확인
        Integer isLikedOrDisliked = null;
        if (userId != null) {
            List<LikeOrDislike> likeOrDislike = likeOrDislikeRepository.findByTicketingIdAndUserId(reviewId, userId);
            if (!likeOrDislike.isEmpty()) {
                isLikedOrDisliked = likeOrDislike.get(0).getStatus();
                //isLikedOrDisliked 리스트의 첫번째 항목의 상태를 가져온다는 뜻이다.
            }
        }

        // 축제/공연 연동 유무 확인
        Boolean isLinked = false;
        FestivalLinkReviewResponseDTO festivalInfo = null;

        // 공연, 축제 연동 O
        Boolean isDeleted = false;
        String festivalId = review.getFestivalId();
        if (festivalId != null){
            isLinked = true;
          
            if(review.getBoardType().equals("정보공유")){
                Festival linkedInfo = festivalRepository.findById(Long.valueOf(festivalId))
                        .orElseThrow(() -> (new CustomException(CustomErrorCode.FESTIVAL_NOT_FOUND)));
                festivalInfo = new FestivalLinkReviewResponseDTO(linkedInfo, review);
            }else if(review.getBoardType().equals("정보보기")){
                if(review.getFestivalType().getType().equals("공연")){
                    OpenPerformance linkedOpenPerformance = openPerformanceRepository.findById(festivalId)
                            .orElseGet(() -> {
                                return null;
                            });
                    isDeleted = linkedOpenPerformance == null;
                    festivalInfo = new FestivalLinkReviewResponseDTO(linkedOpenPerformance, isDeleted, review);
                }else if(review.getFestivalType().getType().equals("축제")){
                    OpenFestival linkedOpenFestival = openFestivalRepository.findById(festivalId)
                            .orElseGet(() -> {
                                return null;
                            });
                    isDeleted = linkedOpenFestival == null;
                    festivalInfo = new FestivalLinkReviewResponseDTO(linkedOpenFestival, isDeleted, review);
                }
            }
        }
        else { // 공연, 축제 연동 X
            festivalInfo = new FestivalLinkReviewResponseDTO(review);
        }

        return new ReviewResponseDto.ReviewDetailResponse(review, imageUrlList, isLinked, isWriter, festivalInfo, isLikedOrDisliked);
    }

    /** pagination **/
    public ReviewResponseDto.ReviewListResponse getReviewPage(int page,
                                                                         String sortBy) {

        PageRequest pageRequest = PageRequest.of(page, 6);
        Page<Review> reviewPage = reviewRepository.findAllReview(sortBy, pageRequest);
        List<Review> reviewList = reviewPage.getContent();

        int pageNum = reviewPage.getNumber(); // 현재 페이지 수
        boolean hasNext = reviewPage.hasNext(); // 다음 페이지 존재 여부
        boolean hasPrevious = reviewPage.hasPrevious(); // 이전 페이지 존재 여부

        List<ReviewResponseDto.ReviewPageResponse> data = reviewList.stream()
                .map(ReviewResponseDto.ReviewPageResponse::new)
                .collect(Collectors.toList());

        long totalCount = data.size(); // 총 검색 수

        return new ReviewResponseDto.ReviewListResponse(data, totalCount, pageNum, hasNext, hasPrevious);
    }
    /** 후기 게시물 삭제 */
    public void deleteReview(Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        //게시글 삭제 권한
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != review.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "후기 게시글 삭제 권한이 없습니다.");
        }

        // 티켓팅에 연관된 likeOrDislike 삭제
        List<LikeOrDislike> likesOrDislikes = likeOrDislikeRepository.findByTicketingId(reviewId);
        for (LikeOrDislike likeOrDislike : likesOrDislikes) {
            likeOrDislikeRepository.delete(likeOrDislike);
        }

        reviewRepository.delete(review);
    }
    /** 후기 게시물 수정 **/
    @Transactional
    public void updateReview(Long reviewId,
                               ReviewRequestDto request, MultipartFile thumbnail){

        // 같이가요 게시글 조회
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new CustomException(REVIEW_NOT_FOUND));

        // 게시글 수정 권한 확인
        User user = userRepository.findById(jwtTokenProvider.getUserId())
                .orElseThrow(() -> new CustomException(USER_NOT_FOUND));
        if(user.getId() != review.getUser().getId()){
            throw new CustomException(NO_PERMISSION, "후기 게시글 수정 권한이 없습니다.");
        }

        String imgUrl = null;

        // 공연/축제 정보 연동 시 DB 에서 확인
        if (request.getFestivalId() != null) {
            checkIfFestivalIdExists(request.getFestivalId(), request.getBoardType(), request.getFestivalType());
        }

        // 게시글 수정 반영
        if(!thumbnail.isEmpty()){
            imgUrl = awsS3Service.uploadImgFile(thumbnail);
        }
        review.updateReview(request, imgUrl);
    }




}
