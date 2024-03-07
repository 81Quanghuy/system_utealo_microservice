package vn.iostar.groupservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupType {
    CLASS("class"),
    DISCUSSION("discussion");

    private final String code;
}
