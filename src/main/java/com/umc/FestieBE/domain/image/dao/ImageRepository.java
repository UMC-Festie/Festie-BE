package com.umc.FestieBE.domain.image.dao;

import com.umc.FestieBE.domain.image.domain.Image;
import com.umc.FestieBE.domain.review.domain.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRepository extends JpaRepository<Image, Long> {

    List<Image> findByReview(Review review);
}
