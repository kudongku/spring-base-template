package kr.soulware.teen_mydata.enums;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Role {

    GUEST(0),
    USER(1),
    ADMIN(2),
    MASTER(3);

    private final int level;

}
