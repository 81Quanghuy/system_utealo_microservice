package vn.iostar.postservice.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class CommentDto implements Serializable {
    private String id;
    private String authorId;
    private String authorName;
    private String authorAvatar;
    private String content;
    @JsonProperty("subComments")
    private List<CommentDto> subCommentDtos;
    private String createdAt;
    private String updatedAt;
}
