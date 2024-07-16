package vn.iostar.userservice.mapper;

import vn.iostar.model.RelationShipResponseAll;
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
    public static RelationShipResponseAll toRelationShipResponseAll(Relationship entity) {
        if (entity == null) {
            return null;
        }
        return RelationShipResponseAll.builder()
                .id(entity.getId())
                .parentUserId(entity.getParent().getUserId())
                .childUserId(entity.getChild().getUserId())
                .isAccepted(entity.getIsAccepted())
                .parentUserName(entity.getParent().getUserName())
                .parentUserAvatar(entity.getParent().getProfile().getAvatar())
                .childUserName(entity.getChild().getUserName())
                .childUserAvatar(entity.getChild().getProfile().getAvatar())
                .parentUserEmail(entity.getParent().getAccount().getEmail())
                .childUserEmail(entity.getChild().getAccount().getEmail())
                .build();
    }
}
