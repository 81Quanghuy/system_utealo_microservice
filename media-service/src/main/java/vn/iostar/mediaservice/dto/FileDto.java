package vn.iostar.mediaservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class FileDto implements Serializable {

    private String id;
    private String authorId;
    private String refUrl;
    private String name;
    private Long size;
    private String messageId;
    private Boolean isMessage;
    private String type;
    private String createdAt;
    private String groupId;

}
