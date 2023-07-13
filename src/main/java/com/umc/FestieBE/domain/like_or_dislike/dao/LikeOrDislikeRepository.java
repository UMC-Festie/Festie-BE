package com.umc.FestieBE.domain.like_or_dislike.dao;

import com.umc.FestieBE.domain.like_or_dislike.domain.LikeOrDislike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeOrDislikeRepository extends JpaRepository<LikeOrDislike,Long> {
}
