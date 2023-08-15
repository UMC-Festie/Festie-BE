package com.umc.FestieBE.domain.mypage.dao;

import com.umc.FestieBE.domain.mypage.domain.Mypage;
import com.umc.FestieBE.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MypageRepository extends JpaRepository<Mypage, Long> {
    @Query("SELECT m FROM Mypage m WHERE m.user = :user")
    Optional<Mypage> findByUser(@Param("user") User user);
}
