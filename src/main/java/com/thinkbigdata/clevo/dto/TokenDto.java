package com.thinkbigdata.clevo.dto;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class TokenDto {
    private String access;
    private String refresh;
}