package vn.iotstart.userservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ProviderType {

    PROVIDER_LOCAL("local"),
    PROVIDER_FACEBOOK("facebook"),
    PROVIDER_GOOGLE("google");

    private final String code;

}
