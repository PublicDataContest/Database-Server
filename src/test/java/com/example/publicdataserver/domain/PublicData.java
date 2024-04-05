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

    private String title; // 제목
    private String deptNm; // 부서명
    private String url; // 문서url
    private String execDt; // 집행일시
    private String execLoc; // 집행장소
    private String execPurpose; // 집행목적
    private String execAmount; // 집행금액
}
