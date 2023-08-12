package com.umc.FestieBE.domain.mypage.dao;

import com.umc.FestieBE.domain.mypage.domain.Mypage;
import com.umc.FestieBE.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MypageRepository extends JpaRepository<Mypage, Long> {
    Optional<Mypage> findByUser(User user);
}
