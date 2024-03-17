package vn.iostar.mediaservice.dto.request;

import lombok.Data;

import java.util.List;

@Data
public class DeleteRequest {

    private List<String> refUrls;

}
