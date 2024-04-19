package com.example.publicdataserver.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class PublicDataDto {
    @JsonProperty("DEPT_NM")
    private String deptNm; // 부서명

    @JsonProperty("EXEC_DT")
    private String execDt; // 집행일시

    @JsonProperty("EXEC_LOC")
    private String execLoc; // 집행장소

    @JsonProperty("TARGET_NM")
    private String targetNm; // 인원 수

    @JsonProperty("EXEC_AMOUNT")
    private String execAmount; // 집행금액
}
