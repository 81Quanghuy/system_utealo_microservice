package vn.iostar.mediaservice.dto.response;

import lombok.Data;

import java.util.List;

@Data
public class ListMediaResponse {
    private List<String> mediaIds;
    private List<String> type;
}
