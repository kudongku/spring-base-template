package kr.soulware.teen_mydata.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProfileType {

    LOCAL("local"),
    DEV("dev"),
    PROD("prod");

    private final String value;

}
