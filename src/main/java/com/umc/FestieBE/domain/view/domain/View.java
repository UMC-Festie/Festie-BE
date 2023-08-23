package com.umc.FestieBE.domain.view.domain;

import com.umc.FestieBE.domain.open_festival.domain.OpenFestival;
import com.umc.FestieBE.domain.open_performance.domain.OpenPerformance;
import com.umc.FestieBE.domain.user.domain.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@Table(name = "views")
public class View {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "view_id")
    private Long id;

    @Column(nullable = false)
    private Long view;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_performance_id")
    private OpenPerformance openperformance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "open_festival_id")
    private OpenFestival openfestival;

    public View() {
    }
}
