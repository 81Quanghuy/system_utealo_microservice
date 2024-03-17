package vn.iostar.mediaservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Getter
@RequiredArgsConstructor
public enum FileEnum {

    IMAGE("image", "Image"),
    VIDEO("video", "Video"),
    AUDIO("audio", "Audio"),
    DOCUMENT("document", "Document");


    private final String id;
    private final String name;
}
