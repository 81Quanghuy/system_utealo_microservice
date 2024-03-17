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

    private String authorId;
    private String refUrl;
    private String type;
    private String createdAt;

}
