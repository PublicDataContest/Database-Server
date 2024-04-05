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
    @JsonProperty("TITLE")
    private String title;

    @JsonProperty("DEPT_NM")
    private String deptNm;

    @JsonProperty("URL")
    private String url;

    @JsonProperty("EXEC_DT")
    private String execDt;

    @JsonProperty("EXEC_LOC")
    private String execLoc;

    @JsonProperty("EXEC_PURPOSE")
    private String execPurpose;

    @JsonProperty("EXEC_AMOUNT")
    private String execAmount;
}
