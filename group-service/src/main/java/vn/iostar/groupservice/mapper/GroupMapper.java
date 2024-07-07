package vn.iostar.groupservice.mapper;

import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.model.GroupDocument;
import vn.iostar.model.GroupElastic;

public class GroupMapper {

    //Mapping giưữa User và groupDocument
    public static GroupDocument toGroupDocument(Group group) {
        GroupDocument groupDocument = new GroupDocument();
        groupDocument.setId(group.getId());
        groupDocument.setPostGroupName(group.getPostGroupName());
        groupDocument.setBio(group.getBio());
        groupDocument.setCreatedAt(group.getCreatedAt());
        groupDocument.setUpdatedAt(group.getUpdatedAt());
        groupDocument.setIsActive(group.getIsActive());
        groupDocument.setIsSystem(group.getIsSystem());
        groupDocument.setIsPublic(group.getIsPublic());
        groupDocument.setIsApprovalRequired(group.getIsApprovalRequired());
        groupDocument.setAvatarGroup(group.getAvatarGroup());
        groupDocument.setBackgroundGroup(group.getBackgroundGroup());
        groupDocument.setAuthorId(group.getAuthorId());
        return groupDocument;
    }
    public static GroupElastic toGroupElastic(GroupDocument group) {
        GroupElastic groupElastic = new GroupElastic();
        groupElastic.setId(group.getId());
        groupElastic.setPostGroupName(group.getPostGroupName());
        groupElastic.setBio(group.getBio());
        groupElastic.setCreatedAt(group.getCreatedAt());
        groupElastic.setUpdatedAt(group.getUpdatedAt());
        groupElastic.setIsActive(group.getIsActive());
        groupElastic.setIsSystem(group.getIsSystem());
        groupElastic.setIsPublic(group.getIsPublic());
        groupElastic.setIsApprovalRequired(group.getIsApprovalRequired());
        groupElastic.setAvatarGroup(group.getAvatarGroup());
        groupElastic.setBackgroundGroup(group.getBackgroundGroup());
        groupElastic.setAuthorId(group.getAuthorId());
        return groupElastic;
    }
}
