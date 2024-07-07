package vn.iostar.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RelationshipResponse {
    private String id;
    private String parentUserId;
    private String childUserId;
    private Boolean isAccepted;

}
