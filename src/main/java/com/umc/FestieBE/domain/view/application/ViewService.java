package com.umc.FestieBE.domain.view.application;

import com.umc.FestieBE.domain.open_festival.dao.OpenFestivalRepository;
import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.dao.OpenPerformanceRepository;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.view.dao.ViewRepository;
import com.umc.FestieBE.domain.view.domain.View;
import com.umc.FestieBE.global.exception.CustomErrorCode;
import com.umc.FestieBE.global.exception.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ViewService {
    private final ViewRepository viewRepository;
    private final OpenPerformanceRepository openPerformanceRepository;
    private final OpenFestivalRepository openFestivalRepository;

    public void updatePerformViewCount(String openPerformanceId){
        OpenPerformance openPerformance = openPerformanceRepository.findById(openPerformanceId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.OPEN_NOT_FOUND));

        if(openPerformance !=null){
            View existingView = viewRepository.findByDomain(openPerformanceId, null);

            if(existingView !=null){
                existingView.setView(existingView.getView() + 1L );
                viewRepository.save(existingView);
            }else {
                View view = new View();
                view.setOpenperformance(openPerformance);
                view.setView(1L);
                viewRepository.save(view);
            }
        }else {
            throw new CustomException(CustomErrorCode.OPEN_NOT_FOUND);
        }

    }

    public void updateFestivalViewCount(String openFestivalId){
        OpenFestival openFestival = openFestivalRepository.findById(openFestivalId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.OPEN_NOT_FOUND));

        if(openFestival !=null){
            View existingView = viewRepository.findByDomain(null, openFestivalId);

            if(existingView !=null){
                existingView.setView(existingView.getView() + 1L );
                viewRepository.save(existingView);
            }else {
                View view = new View();
                view.setOpenfestival(openFestival);
                view.setView(1L);
                viewRepository.save(view);
            }
        }else {
            throw new CustomException(CustomErrorCode.OPEN_NOT_FOUND);
        }

    }

    public View toEntity(OpenPerformance openPerformance){
        return View.builder()
                .openperformance(openPerformance)
                .build();
    }





}
