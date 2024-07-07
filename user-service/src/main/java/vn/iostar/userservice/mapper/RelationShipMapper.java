package vn.iostar.userservice.mapper;

import vn.iostar.model.RelationshipResponse;
import vn.iostar.userservice.entity.Relationship;

public class RelationShipMapper {
    public static RelationshipResponse toRelationshipResponse(Relationship entity) {
        if (entity == null) {
            return null;
        }
        return RelationshipResponse.builder()
                .id(entity.getId())
                .parentUserId(entity.getParent().getUserId())
                .childUserId(entity.getChild().getUserId())
                .isAccepted(entity.getIsAccepted())
                .build();
    }
}
