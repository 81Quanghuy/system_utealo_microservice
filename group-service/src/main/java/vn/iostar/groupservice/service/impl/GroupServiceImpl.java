package vn.iostar.groupservice.service.impl;

import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.dto.GroupDto;
import vn.iostar.groupservice.dto.UserDto;
import vn.iostar.groupservice.dto.request.GroupConfigRequest;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.request.UpdateDetailRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.exception.wrapper.ForbiddenException;
import vn.iostar.groupservice.exception.wrapper.NotFoundException;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.repository.GroupRepository;
import vn.iostar.groupservice.service.GroupService;
import vn.iostar.groupservice.service.MapperService;
import vn.iostar.groupservice.service.client.FileClientService;
import vn.iostar.groupservice.service.client.UserClientService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupServiceImpl implements GroupService {

    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final MapperService mapperService;

    private final MongoTemplate mongoTemplate;

    @Override
    public ResponseEntity<GenericResponse> createGroup(String userId, GroupCreateRequest groupCreateRequest) {
        log.info("GroupServiceImpl, createGroup");
        Date now = new Date();

        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .postGroupName(groupCreateRequest.getName())
                .bio(groupCreateRequest.getDescription() == null ?
                        null : groupCreateRequest.getDescription())
                .authorId(userId)

                .isPublic(groupCreateRequest.getIsPublic())
                .isApprovalRequired(groupCreateRequest.getIsAcceptAllRequest())
                .createdAt(now)
                .build();

        group = groupRepository.save(group);
        groupMemberRepository.save(GroupMember.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .group(group)
                .role(GroupMemberRoleType.Admin)
                .createdAt(now)
                .build());

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());
    }
    @Override
    public ResponseEntity<GenericResponse> getGroupById(String userId, String groupId) {
        log.info("GroupServiceImpl, getGroupById");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        GroupDto groupDto = mapperService.mapToGroupDto(group);

        Map<String, Object> result = new HashMap<>();
        result.put("group", groupDto);

        groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .ifPresent(groupMember -> result.put("user", mapperService.mapToGroupMemberDto(groupMember)));

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Truy cập nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupsByUserId(String userId) {
        log.info("GroupServiceImpl, getGroupsByUserId");
        Map<String, List<GroupDto>> result = new HashMap<>();
        List<GroupMemberRoleType> groupMemberRoles = List.of(
                GroupMemberRoleType.Admin,
                GroupMemberRoleType.Member);
        for (GroupMemberRoleType role : groupMemberRoles) {
            result.put(role.name(), groupMemberRepository.findAllByUserIdAndRole(userId, role).stream()
                    .map(groupMember -> mapperService.mapToGroupDto(groupMember.getGroup()))
                    .collect(Collectors.toList()));
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupConfig(String userId, String groupId, GroupConfigRequest groupConfigRequest) {
        log.info("GroupServiceImpl, updateGroupConfig");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi cấu hình nhóm!");
        }
        group.setIsPublic(groupConfigRequest.getIsPublic());

        groupRepository.save(group);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật cấu hình nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupDetail(String userId, String groupId, UpdateDetailRequest updateDetailRequest) {
        log.info("GroupServiceImpl, updateGroupDetail");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi thông tin nhóm!");
        }

        groupRepository.save(group);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật thông tin nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupAvatar(String userId, String groupId, MultipartFile avatar) throws IOException {
        log.info("GroupServiceImpl, updateGroupAvatar");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi ảnh đại diện nhóm!");
        }
        String oldAvatar = group.getAvatarGroup();

        groupRepository.save(group);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh đại diện nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupCover(String userId, String groupId, MultipartFile cover) throws IOException {
        log.info("GroupServiceImpl, updateGroupCover");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền thay đổi ảnh bìa nhóm!");
        }
        String oldCover = group.getBackgroundGroup();


        groupRepository.save(group);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Cập nhật ảnh bìa nhóm thành công!")
                .result(mapperService.mapToGroupDto(group))
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteGroup(String userId, String groupId) {
        log.info("GroupServiceImpl, deleteGroup");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy nhóm!"));
        if (!group.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền xóa nhóm!");
        }
        groupRepository.delete(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Xóa nhóm thành công!")
                .result(null)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<List<GroupDto>> searchGroup(
            Optional<String> query, Optional<Boolean> isClass, Optional<Boolean> isPublic,
            Optional<Integer> grade, Optional<String> subject) {
        log.info("GroupServiceImpl, searchGroup");

        String queryValue = query.orElse("");
        Boolean typeValue = isClass.orElse(null);
        Integer gradeValue = grade.orElse(null);
        String subjectValue = subject.orElse(null);

        Query searchQuery = new Query();

        Criteria queryCriteria = new Criteria().orOperator(
                Criteria.where("group_name").regex(queryValue, "i"),
                Criteria.where("group_description").regex(queryValue, "i")
        );

        searchQuery.addCriteria(queryCriteria);

        if (typeValue != null) {
            searchQuery.addCriteria(Criteria.where("isClass").is(typeValue));
        }

        if (gradeValue != null) {
            searchQuery.addCriteria(Criteria.where("grade").is(gradeValue));
        }

        if (subjectValue != null) {
            searchQuery.addCriteria(Criteria.where("subject").regex(subjectValue, "i"));
        }

        List<Group> groups = mongoTemplate.find(searchQuery, Group.class);

        List<GroupDto> groupDtos = groups.stream()
                .map(mapperService::mapToGroupDto)
                .toList();

        return ResponseEntity.ok(groupDtos);
    }

    @Override
    public ResponseEntity<GenericResponse> valiadateUserInGroup(String userId, String groupId) {
        log.info("GroupServiceImpl, valiadateUserInGroup");
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, groupId)
                .orElse(null);
        if (groupMember == null) {
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .message("Bạn không có quyền truy cập nhóm!")
                    .statusCode(HttpStatus.OK.value())
                    .result(false)
                    .build());
        }
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Bạn có quyền truy cập nhóm!")
                .statusCode(HttpStatus.OK.value())
                .result(true)
                .build());
    }

    @Override
    public ResponseEntity<List<String>> getGroupByUserId(String userId) {

        return ResponseEntity.ok(null);
    }

    @Override
    public ResponseEntity<GenericResponse> getAllGroupsForAdmin(String token, Integer page, Integer size) {
        log.info("GroupServiceImpl, getAllGroupsForAdmin");
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<Group> groups = groupRepository.findAll(pageable);
        Map<String, Object> result = new HashMap<>();
        result.put("groups", groups.stream()
                .map(mapperService::mapToGroupDto)
                .toList());
        result.put("totalPages", groups.getTotalPages());
        result.put("totalElements", groups.getTotalElements());
        result.put("currentPage", groups.getNumber());
        result.put("currentElements", groups.getNumberOfElements());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Lấy danh sách nhóm thành công!")
                .result(result)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getAllClassesForAdmin(String token, Integer page, Integer size) {
        return null;
    }

    @Override
    public ResponseEntity<GenericResponse> createGroupNew(GroupCreateRequest groupCreateRequest) {
        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .postGroupName(groupCreateRequest.getName())
                .bio(groupCreateRequest.getDescription() == null ?
                        null : groupCreateRequest.getDescription())
                .authorId(UUID.randomUUID().toString())
                .isPublic(groupCreateRequest.getIsPublic())
                .isApprovalRequired(groupCreateRequest.getIsAcceptAllRequest())
                .createdAt(new Date())
                .build();
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Tạo nhóm thành công!")
                .result(group.getId())
                .statusCode(HttpStatus.OK.value())
                .build());
    }
}
