package com.umc.FestieBE.domain.view.dao;


import com.umc.FestieBE.domain.view.domain.View;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;



public interface ViewRepository extends JpaRepository<View, Long> {

    //조회수 id에 따른 viewCount
    @Query("SELECT v.view FROM View v " +
            "WHERE (v.openperformance.id = :openperformanceId OR :openperformanceId IS NULL) " +
            "AND (v.openfestival.id = :openfestivalId OR :openfestivalId IS NULL)")
    Long findByIdWithCount(@Param("openperformanceId") String openperformanceId,
                           @Param("openfestivalId") String openfestivalId);

    @Query("SELECT v FROM View v " +
            "WHERE (v.openperformance.id = :openperformanceId OR :openperformanceId IS NULL) " +
            "AND (v.openfestival.id = :openfestivalId OR :openfestivalId IS NULL)")
    View findByDomain(@Param("openperformanceId") String openperformanceId,
                               @Param("openfestivalId") String openfestivalId);


//    //festival
//    //조회수 id에 따른 viewCount
//    @Query("SELECT v.view FROM View v " +
//            "WHERE v.openfestival.id = :openfestivalId")
//    Long findByfestivalIdWithCount(@Param("openfestivalId") String openfestivalId);
//
//    @Query("SELECT v FROM View v " +
//            "WHERE v.openfestival.id = :openfestivalId")
//    View findByOpenfestival(@Param("openfestivalId") String openfestivalId);

}
