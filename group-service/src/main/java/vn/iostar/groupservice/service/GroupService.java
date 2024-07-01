package vn.iostar.groupservice.service;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.constant.AdminInGroup;
import vn.iostar.groupservice.dto.*;
import vn.iostar.groupservice.dto.request.GroupConfigRequest;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.request.UpdateDetailRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.dto.response.GenericResponseAdmin;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.model.GroupResponse;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface GroupService {

    ResponseEntity<GenericResponse> getPostGroupByUserId(String authorizationHeader);

    ResponseEntity<GenericResponse> createGroup(GroupCreateRequest postGroup, String userId);

    ResponseEntity<GenericResponse> updatePostGroupByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

    ResponseEntity<GenericResponse> updatePhotoByPostIdAndUserId(PostGroupDTO postGroup, String currentUserId);

    ResponseEntity<GenericResponse> deletePostGroup(String postGroupId, String currentUserId);

    ResponseEntity<GenericResponse> clockGroup(String groupId, String currentUserId);

    ResponseEntity<GenericResponse> getPostGroupById(String currentUserId, String postGroupId);

    ResponseEntity<GenericResponse> getGroupSharePosts(String currentUserId, String postGroupId, Pageable pageable);

    ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, Pageable pageable);

    ResponseEntity<GenericResponse> getGroupPosts(String currentUserId, Integer postGroupId, Pageable pageable);

    List<FilesOfGroupDTO> findLatestFilesByGroupId(Integer groupId);

    Page<PhotosOfGroupDTO> findLatestPhotosByGroupId(String groupId, Pageable pageable);
    Optional<Group> findById(String id);

    ResponseEntity<GenericResponse> getPostGroupJoinByUserId(String currentUserId);

    ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(String currentUserId);

    ResponseEntity<GenericResponse> findByPostGroupNameContainingIgnoreCase(String search, String currentUserId);

    Page<SearchPostGroup> findAllGroups(int page, int itemsPerPage);

    ResponseEntity<GenericResponseAdmin> getAllGroups(String authorizationHeader, int page, int itemsPerPage);

    ResponseEntity<GenericResponse> deletePostGroupByAdmin(String postGroupId, String authorizationHeader);

    Map<String, Long> countGroupsByMonthInYear();

    // Đếm số lượng user trong ngày hôm nay
    long countGroupsToday();

    // Đếm số lượng user trong 7 ngày
    public long countGroupsInWeek();

    // Đếm số lượng user trong 1 tháng
    long countGroupsInMonthFromNow();

    // Đếm số lượng user trong 1 năm
    long countGroupsInOneYearFromNow();

    // Đếm số lượng user trong 9 tháng
    long countGroupsInNineMonthsFromNow();

    // Đếm số lượng user trong 6 tháng
    long countGroupsInSixMonthsFromNow();

    // Đếm số lượng user trong 3 tháng
    long countGroupsInThreeMonthsFromNow();

    // Thống kê bài post trong ngày hôm nay
    List<SearchPostGroup> getGroupsToday();

    // Thống kê bài post trong 7 ngày
    List<SearchPostGroup> getGroupsIn7Days();

    // Thống kê bài post trong 1 tháng
    List<SearchPostGroup> getGroupsInMonth();

    ResponseEntity<GenericResponseAdmin> getPostGroupJoinByUserId(String userId, int page, int itemsPerPage);
    // Tìm kiếm tất cả nhóm và người dùng
    ResponseEntity<GenericResponse> searchGroupAndUserContainingIgnoreCase(String search, String userIdToken);

    ResponseEntity<GenericResponse> getSystemGroups();

    ResponseEntity<GenericResponse> updateGroupFromExcel(List<GroupResponse> groupResponse);

    AdminInGroup checkAdminInGroup(String groupName);

    void deleteMemberInGroup(List<String> userIds);

    ResponseEntity<GenericResponse> addMemberToSystemGroup(GroupResponse groupResponse);
}
