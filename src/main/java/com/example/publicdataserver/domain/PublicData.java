package com.example.publicdataserver.domain;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String deptNm; // 부서명
    private String execDt; // 집행일시
    private String execLoc; // 집행장소
    private String targetNm; // 인원 수
    private String execAmount; // 집행금액
    private String execMonth; // 집행 월
}
