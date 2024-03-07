package vn.iostar.groupservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum StateType {

    PENDING("pending"),
    ACCEPTED("accepted"),
    REJECTED("rejected");

    private final String code;

}
