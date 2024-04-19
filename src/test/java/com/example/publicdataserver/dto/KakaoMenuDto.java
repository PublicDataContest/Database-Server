package com.example.publicdataserver.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class KakaoMenuDto {
    private String storeId;
    private String menuName;
    private String menuPrice;
}
