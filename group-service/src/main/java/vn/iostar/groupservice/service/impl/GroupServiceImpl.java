package vn.iostar.groupservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.dto.FilesOfGroupDTO;
import vn.iostar.groupservice.dto.PhotosOfGroupDTO;
import vn.iostar.groupservice.dto.PostGroupDTO;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.PostGroupResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.entity.GroupRequest;
import vn.iostar.groupservice.exception.wrapper.ForbiddenException;
import vn.iostar.groupservice.exception.wrapper.NotFoundException;
import vn.iostar.groupservice.jwt.service.JwtService;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.repository.GroupRepository;
import vn.iostar.groupservice.repository.GroupRequestRepository;
import vn.iostar.groupservice.service.GroupService;
import vn.iostar.groupservice.service.MapperService;
import vn.iostar.groupservice.service.client.FileClientService;

import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MapperService mapperService;
    private final GroupRequestRepository groupRequestRepository;
    private final JwtService jwtService;
    private final FileClientService fileClientService;

    @Override
    public ResponseEntity<GenericResponse> getPostGroupByUserId(String authorizationHeader) {
        log.info("GroupServiceImpl, getPostGroupByUserId");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        List<Group> groups = groupRepository.findAllByAuthorIdAndIsActive(userId,true);
        // sap xep theo role cua user trong nhom : admin, Deputy, Member
        groups.sort(Comparator.comparing(group -> {
            Optional<GroupMember> groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId());
            return groupMember.map(member -> member.getRole().ordinal()).orElseGet(() -> 0);
        }));
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(groups.stream()
                        .map(mapperService::mapToGroupDto)
                        .toList())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> createGroup(GroupCreateRequest postGroup, String userId) {
        log.info("GroupServiceImpl, createGroup");
        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .postGroupName(postGroup.getPostGroupName())
                .bio(postGroup.getBio() == null ?
                        "" : postGroup.getBio())
                .authorId(userId)
                .isSystem(postGroup.getIsSystem())
                .isPublic(postGroup.getIsPublic())
                .isApprovalRequired(postGroup.getIsApprovalRequired())
                .isActive(postGroup.getIsActive())
                .createdAt(new Date())
                .build();
        groupRepository.save(group);

        // Add author to group
        GroupMember groupMember = GroupMember.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .group(group)
                .role(GroupMemberRoleType.Admin)
                .build();
        groupMemberRepository.save(groupMember);

        //add group request to user
        if(!postGroup.getUserRequestId().isEmpty()){
            for (String userRequest : postGroup.getUserRequestId()) {
                GroupRequest groupRequest = GroupRequest.builder()
                        .id(UUID.randomUUID().toString())
                        .group(group)
                        .invitedUser(userRequest)
                        .invitingUser(userId)
                        .isAccept(false)
                        .group(group)
                        .createdAt(new Date())
                        .build();
                groupRequestRepository.save(groupRequest);
            }
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updatePostGroupByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId) {
       log.info("GroupServiceImpl, updatePostGroupByPostIdAndUserId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isEmpty()) {
            throw new NotFoundException("Không tìm thấy nhóm này!");
        }
        Group group = updateGroup(postGroup, currentUserId, optionalGroup.get());
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updatePhotoByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId) {
        log.info("GroupServiceImpl, updatePhotoByPostIdAndUserId");
        Optional<Group> optionalGroup = groupRepository.findById(postGroup.getPostGroupId());
        if (optionalGroup.isEmpty()) {
            throw new NotFoundException("Không tìm thấy nhóm này!");
        }
        Group group = optionalGroup.get();
        if (!group.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này!");
        }
        if (postGroup.getAvatar() != null) {
            String avatarOld = group.getAvatarGroup();
            group.setAvatarGroup(updateImage(avatarOld, postGroup.getAvatar()));
            groupRepository.save(group);
        } else if (postGroup.getBackground() != null) {
            String backgroundOld = group.getBackgroundGroup();
            group.setBackgroundGroup(updateImage(backgroundOld, postGroup.getBackground()));
            groupRepository.save(group);
        }
        group.setUpdatedAt(new Date());
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deletePostGroup(String postGroupId, String currentUserId) {
        log.info("GroupServiceImpl, deletePostGroup");
        Group group = CheckNullGroup(postGroupId, currentUserId);
        //xóa member trong nhóm
        List<GroupMember> groupMember = groupMemberRepository.findAllByGroupId(postGroupId);
        groupMemberRepository.deleteAll(groupMember);
        List<GroupRequest> groupRequest = groupRequestRepository.findAllByGroupId(postGroupId);
        groupRequestRepository.deleteAll(groupRequest);
        groupRepository.delete(group);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Xóa nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());

    }

    @Override
    public ResponseEntity<GenericResponse> clockGroup(String groupId, String currentUserId) {
        log.info("GroupServiceImpl, clockGroup");

        Group group = CheckNullGroup(groupId, currentUserId);
        group.setIsActive(!group.getIsActive());
        group.setUpdatedAt(new Date());
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật trạng thái nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, String postGroupId) {
        log.info("GroupServiceImpl, getPostGroupById");
        Optional<Group> optionalGroup = groupRepository.findById(postGroupId);
        if (optionalGroup.isEmpty()) {
            throw new NotFoundException("Không tìm thấy nhóm này!");
        }
        Group group = optionalGroup.get();
        if (!group.getIsActive()) {
            throw new NotFoundException("Nhóm này đã bị khóa!");
        }
        Integer countMember = groupMemberRepository.countByGroupId(postGroupId);
        // Lay danh sach id cua Admin va Deputy trong nhom
        List<GroupMember> listAdminAndDeputy = groupMemberRepository.findAllByGroupIdAndRoleIn(postGroupId, List.of(GroupMemberRoleType.Admin, GroupMemberRoleType.Deputy));
        List<String> listAdminAndDeputyId = new ArrayList<>();
        for (GroupMember groupMember : listAdminAndDeputy) {
            listAdminAndDeputyId.add(groupMember.getUserId());
        }
        PostGroupResponse postGroupResponse = mapperService.mapToPostGroupResponse(group,countMember, listAdminAndDeputyId);
                return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy thông tin nhóm thành công!")
                .result(postGroupResponse)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, String postGroupId, Pageable pageable) {
        log.info("GroupServiceImpl, getGroupSharePosts");
        Optional<Group> optionalGroup = groupRepository.findById(postGroupId);
        if (optionalGroup.isEmpty()) {
            throw new NotFoundException("Không tìm thấy nhóm này!");
        }
        Group group = optionalGroup.get();
        if (!group.getIsActive()) {
            throw new NotFoundException("Nhóm này đã bị khóa!");
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách bài viết của nhóm thành công!")
                .result(null)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, Pageable pageable) {
        log.info("GroupServiceImpl, getPostOfPostGroup");
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách bài viết của nhóm thành công!")
                .result(null)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupPosts(String currentUserId, Integer postGroupId, Pageable pageable) {
        log.info("GroupServiceImpl, getGroupPosts");
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách bài viết của nhóm thành công!")
                .result(null)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public List<FilesOfGroupDTO> findLatestFilesByGroupId(Integer groupId) {
        log.info("GroupServiceImpl, findLatestFilesByGroupId");
        return null; // FileService chưa làm
    }

    @Override
    public Page<PhotosOfGroupDTO> findLatestPhotosByGroupId(Integer groupId, Pageable pageable) {
        log.info("GroupServiceImpl, findLatestPhotosByGroupId");
        return null; // FileService chưa làm
    }

    @NotNull
    private static Group updateGroup(PostGroupDTO postGroup, String currentUserId, Group group) {

        if (!group.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này!");
        }
        group.setPostGroupName(postGroup.getPostGroupName());
        group.setIsPublic(postGroup.getIsPublic());
        group.setIsApprovalRequired(postGroup.getIsApprovalRequired());
        group.setIsActive(postGroup.getIsActive());
        group.setBio(postGroup.getBio());
        group.setUpdatedAt(new Date());
        return group;
    }

    /**
     * Cập nhật ảnh vào cloudinary và xóa ảnh cũ
     * @param oldImage oldImage
     * @param newImage newImage
     * @return String url ảnh mới
     */
    public String updateImage(String oldImage, MultipartFile newImage) {
        String result = null;
        try {
            result = fileClientService.uploadPhoto(newImage);
            if (oldImage != null) {
                fileClientService.deletePhoto(oldImage);
            }
        } catch (IOException e) {
            log.error("Error when update image: {}", e.getMessage());
        }
        return result;
    }
    public Group CheckNullGroup(String groupId, String currentUserId) {
        Optional<Group> optionalGroup = groupRepository.findById(groupId);
        if (optionalGroup.isEmpty()) {
            throw new NotFoundException("Không tìm thấy nhóm này!");
        }
        Group group = optionalGroup.get();
        if (!group.getAuthorId().equals(currentUserId)) {
            throw new ForbiddenException("Bạn không có quyền thực hiện hành động này!");
        }
        return group;
    }
}
