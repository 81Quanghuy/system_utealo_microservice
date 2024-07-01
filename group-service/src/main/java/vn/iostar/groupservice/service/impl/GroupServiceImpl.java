package vn.iostar.groupservice.service.impl;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import vn.iostar.constant.AdminInGroup;
import vn.iostar.constant.GroupMemberRoleType;
import vn.iostar.constant.RoleName;
import vn.iostar.groupservice.constant.AppConstant;
import vn.iostar.groupservice.dto.*;
import vn.iostar.groupservice.dto.request.GroupCreateRequest;
import vn.iostar.groupservice.dto.response.*;
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
import vn.iostar.groupservice.service.client.FriendClientService;
import vn.iostar.groupservice.service.client.PostClientService;
import vn.iostar.groupservice.service.client.UserClientService;
import vn.iostar.model.GroupResponse;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
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
    private final UserClientService userClientService;
    private final FriendClientService friendClientService;
    private final PostClientService postClientService;

    @Override
    public ResponseEntity<GenericResponse> getPostGroupByUserId(String authorizationHeader) {
        log.info("GroupServiceImpl, getPostGroupByUserId");
        String token = authorizationHeader.substring(7);
        String userId = jwtService.extractUserId(token);
        List<GroupMember> groupMembers = groupMemberRepository.findByUserIdAndIsLocked(userId, false);
        return getGenericResponseResponseEntity(groupMembers);
    }

    @Override
    @Transactional
    public ResponseEntity<GenericResponse> createGroup(GroupCreateRequest postGroup, String userId) {
        log.info("GroupServiceImpl, createGroup");
        Optional<Group> groupExist = groupRepository.findByPostGroupName(postGroup.getPostGroupName());
        if (groupExist.isPresent()) {
            throw new ForbiddenException("Tên nhóm đã tồn tại!");
        }
        Group group = Group.builder()
                .id(UUID.randomUUID().toString())
                .postGroupName(postGroup.getPostGroupName())
                .bio(postGroup.getBio() == null ? "" : postGroup.getBio())
                .isSystem(postGroup.getIsSystem() != null && postGroup.getIsSystem())
                .isPublic(postGroup.getIsPublic())
                .authorId(userId)
                .isApprovalRequired(postGroup.getIsApprovalRequired())
                .isActive(true)
                .createdAt(new Date()).build();
        groupRepository.save(group);

        // Add author to group
        GroupMember groupMember = GroupMember.builder()
                .id(UUID.randomUUID().toString())
                .userId(userId)
                .group(group)
                .isLocked(false)
                .memberRequestId(userId).
                createdAt(new Date())
                .updatedAt(new Date())
                .role(GroupMemberRoleType.Admin)
                .build();
        groupMemberRepository.save(groupMember);

        //add group request to user
        if (postGroup.getUserId() != null && !postGroup.getUserId().isEmpty()) {
            for (String userRequest : postGroup.getUserId()) {
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

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Tạo nhóm thành công!").result(group.getId()).statusCode(HttpStatus.OK.value()).build());
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
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Cập nhật nhóm thành công!").result(group.getId()).statusCode(HttpStatus.OK.value()).build());
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
            group.setAvatarGroup(updateImage(avatarOld, postGroup.getAvatar(), true));
            groupRepository.save(group);
        } else if (postGroup.getBackground() != null) {
            String backgroundOld = group.getBackgroundGroup();
            group.setBackgroundGroup(updateImage(backgroundOld, postGroup.getBackground(), false));
            groupRepository.save(group);
        }
        group.setUpdatedAt(new Date());
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Cập nhật ảnh nhóm thành công!").result(group.getId()).statusCode(HttpStatus.OK.value()).build());
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

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Xóa nhóm thành công!").result(group.getId()).statusCode(HttpStatus.OK.value()).build());

    }

    @Override
    public ResponseEntity<GenericResponse> clockGroup(String groupId, String currentUserId) {
        log.info("GroupServiceImpl, clockGroup");

        Group group = CheckNullGroup(groupId, currentUserId);
        group.setIsActive(!group.getIsActive());
        group.setUpdatedAt(new Date());
        groupRepository.save(group);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Cập nhật trạng thái nhóm thành công!").result(group.getId()).statusCode(HttpStatus.OK.value()).build());
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
        PostGroupResponse postGroupResponse = mapperService.mapToPostGroupResponse(group, countMember, listAdminAndDeputyId);
        postGroupResponse.setRoleGroup(checkUserInGroup(currentUserId, postGroupId));
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Lấy thông tin nhóm thành công!").result(postGroupResponse).statusCode(HttpStatus.OK.value()).build());
    }
    public String checkUserInGroup(String userId,String postGroupId) {
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, postGroupId).orElse(null);
        if (groupMember != null) {
            if (groupMember.getRole().equals(GroupMemberRoleType.Admin)) {
                return "Admin";
            } else if (groupMember.getRole().equals(GroupMemberRoleType.Deputy)) {
                return "Deputy";
            }
            return "Member";
        }
        Optional<GroupRequest> groupRequest = groupRequestRepository.findByGroupIdAndInvitedUserAndIsAccept(postGroupId,userId,true);
        if (groupRequest.isPresent()) {
            if (Boolean.TRUE.equals(groupRequest.get().getIsAccept())) {
                return "Waiting Accept";
            }
            return "Accept Invited";
        }
        return "None";
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
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Lấy danh sách bài viết của nhóm thành công!").result(null).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getPostOfPostGroup(String currentUserId, Pageable pageable) {
        log.info("GroupServiceImpl, getPostOfPostGroup");
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Lấy danh sách bài viết của nhóm thành công!").result(null).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getGroupPosts(String currentUserId, Integer postGroupId, Pageable pageable) {
        log.info("GroupServiceImpl, getGroupPosts");
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Lấy danh sách bài viết của nhóm thành công!").result(null).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public List<FilesOfGroupDTO> findLatestFilesByGroupId(Integer groupId) {
        log.info("GroupServiceImpl, findLatestFilesByGroupId");
        return null; // FileService chưa làm
    }

    @Override
    public Page<PhotosOfGroupDTO> findLatestPhotosByGroupId(String groupId, Pageable pageable) {
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
        group.setIsActive(postGroup.getIsActive() != null ? postGroup.getIsActive() : group.getIsActive());
        group.setBio(postGroup.getBio());
        group.setUpdatedAt(new Date());
        return group;
    }

    /**
     * Cập nhật ảnh vào cloudinary và xóa ảnh cũ
     *
     * @param oldImage oldImage
     * @param newImage newImage
     * @return String url ảnh mới
     */
    public String updateImage(String oldImage, MultipartFile newImage, boolean isAvatar) {
        String result = null;
        if (isAvatar) {
            try {
                result = fileClientService.uploadGroupAvatar(newImage);
                if (oldImage != null) {
                    fileClientService.deleteGroupAvatar(oldImage);
                }
            } catch (IOException e) {
                log.error("Error when update image: {}", e.getMessage());
            }
        } else {
            try {
                result = fileClientService.uploadGroupCover(newImage);
                if (oldImage != null) {
                    fileClientService.deleteGroupCover(oldImage);
                }
            } catch (IOException e) {
                log.error("Error when update image: {}", e.getMessage());
            }
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

    @Override
    public Optional<Group> findById(String id) {
        return groupRepository.findById(id);
    }

    @Override
    public ResponseEntity<GenericResponse> getPostGroupJoinByUserId(String currentUserId) {
        log.info("GroupServiceImpl, getPostGroupJoinByUserId");
        List<GroupMember> groupMembers = groupMemberRepository.findByUserIdAndIsLockedAndRole(currentUserId, false, GroupMemberRoleType.Member);
        return getGenericResponseResponseEntity(groupMembers);
    }

    @Override
    public ResponseEntity<GenericResponse> getPostGroupOwnerByUserId(String currentUserId) {
        log.info("GroupServiceImpl, getPostGroupOwnerByUserId");
        List<GroupMember> groupMembers = groupMemberRepository.findByUserIdAndIsLockedAndRoleIn(currentUserId, false, List.of(GroupMemberRoleType.Admin, GroupMemberRoleType.Deputy));
        return getGenericResponseResponseEntity(groupMembers);
    }

    @Override
    public ResponseEntity<GenericResponse> findByPostGroupNameContainingIgnoreCase(String search, String currentUserId) {
        log.info("GroupServiceImpl, findByPostGroupNameContainingIgnoreCase");
        List<SearchPostGroup> groups = groupRepository.findByPostGroupNameContainingIgnoreCase(search);
        //map to response
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Tìm kiếm nhóm thành công!").result(groups).statusCode(HttpStatus.OK.value()).build());
    }

    @NotNull
    private ResponseEntity<GenericResponse> getGenericResponseResponseEntity(List<GroupMember> groupMembers) {
        List<GroupPostResponse> groupPostResponses = new ArrayList<>();
        for (GroupMember member : groupMembers) {
            groupPostResponses.add(mapperService.mapToGroupPostResponse(member));
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Lấy danh sách nhóm thành công!").result(groupPostResponses).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponseAdmin> getAllGroups(String authorizationHeader, int page, int itemsPerPage) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(GenericResponseAdmin.builder().success(false)
                    .message("No access").statusCode(HttpStatus.FORBIDDEN.value()).build());
        }

        Page<SearchPostGroup> groups = findAllGroups(page, itemsPerPage);
        long totalGroups = groupRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalGroups);
        pagination.setPages((int) Math.ceil((double) totalGroups / itemsPerPage));

        if (groups.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(true)
                    .message("Empty").result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(GenericResponseAdmin.builder().success(true).message("Retrieved List of Groups Successfully")
                            .result(groups).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }


    @Override
    public Page<SearchPostGroup> findAllGroups(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<SearchPostGroup> postGroups = groupRepository.findAllPostGroups(pageable);

        Page<SearchPostGroup> simplifiedGroupPosts = postGroups.map(group -> {
            Optional<Group> postGroupOptional = findById(group.getId());
            if (postGroupOptional.isPresent()) {
                group.setCountMember(groupMemberRepository.countByGroupId(group.getId()));
            }
            return group;
        });
        return simplifiedGroupPosts;
    }

    @Override
    public ResponseEntity<GenericResponse> deletePostGroupByAdmin(String postGroupId, String authorizationHeader) {
        String token = authorizationHeader.substring(7);
        String currentUserId = jwtService.extractUserId(token);
        UserProfileResponse user = userClientService.getUser(currentUserId);
        RoleName roleName = user.getRoleName();
        if (!roleName.name().equals("Admin")) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponse.builder().success(false)
                    .message("No have access").statusCode(HttpStatus.NOT_FOUND.value()).build());
        }
        Optional<Group> posGroupOptional = findById(postGroupId);
        Page<SearchPostGroup> groups = findAllGroups(1, 10);
        if (posGroupOptional.isPresent()) {
            //xóa member trong nhóm
            List<GroupMember> groupMember = groupMemberRepository.findAllByGroupId(postGroupId);
            groupMemberRepository.deleteAll(groupMember);
            List<GroupRequest> groupRequest = groupRequestRepository.findAllByGroupId(postGroupId);
            groupRequestRepository.deleteAll(groupRequest);
            groupRepository.delete(posGroupOptional.get());
            return ResponseEntity.ok()
                    .body(new GenericResponse(true, "Delete Successful!", groups, HttpStatus.OK.value()));
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new GenericResponse(false, "Cannot found post group!", null, HttpStatus.NOT_FOUND.value()));
        }

    }

    @Override
    public Map<String, Long> countGroupsByMonthInYear() {
        LocalDateTime now = LocalDateTime.now();
        int currentYear = now.getYear();

        // Tạo một danh sách các tháng
        List<Month> months = Arrays.asList(Month.values());
        Map<String, Long> groupCountsByMonth = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để duy trì thứ tự

        for (Month month : months) {
            LocalDateTime startDate = LocalDateTime.of(currentYear, month, 1, 0, 0);
            LocalDateTime endDate = startDate.plusMonths(1).minusSeconds(1);

            Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
            Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

            long grpupCount = groupRepository.countByCreatedAtBetween(startDateAsDate, endDateAsDate);
            groupCountsByMonth.put(month.toString(), grpupCount);
        }

        return groupCountsByMonth;
    }

    // Chuyển sang giờ bắt đầu của 1 ngày là 00:00:00
    public Date getStartOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTime();
    }

    // Chuyển sang giờ kết thức của 1 ngày là 23:59:59
    public Date getEndOfDay(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
        return calendar.getTime();
    }

    // Đếm số lượng user trong ngày hôm nay
    @Override
    public long countGroupsToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        return groupRepository.countByCreatedAtBetween(startDate, endDate);
    }

    // Đếm số lượng user trong 7 ngày
    @Override
    public long countGroupsInWeek() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime weekAgo = now.minus(1, ChronoUnit.WEEKS);
        Date startDate = Date.from(weekAgo.atZone(ZoneId.systemDefault()).toInstant());
        Date endDate = Date.from(now.atZone(ZoneId.systemDefault()).toInstant());
        return groupRepository.countByCreatedAtBetween(startDate, endDate);
    }

    // Đếm số lượng user trong 1 tháng
    @Override
    public long countGroupsInMonthFromNow() {
        // Lấy thời gian hiện tại
        LocalDateTime now = LocalDateTime.now();

        // Thời gian bắt đầu là thời điểm hiện tại trừ 1 tháng
        LocalDateTime startDate = now.minusMonths(1);

        // Thời gian kết thúc là thời điểm hiện tại
        LocalDateTime endDate = now;

        // Chuyển LocalDateTime sang Date (với ZoneId cụ thể, ở đây là
        // ZoneId.systemDefault())
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());

        // Truy vấn số lượng user trong khoảng thời gian này
        return groupRepository.countByCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 3 tháng
    @Override
    public long countGroupsInThreeMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(3);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return groupRepository.countByCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 6 tháng
    @Override
    public long countGroupsInSixMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(6);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return groupRepository.countByCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 9 tháng
    @Override
    public long countGroupsInNineMonthsFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusMonths(9);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return groupRepository.countByCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    // Đếm số lượng user trong 1 năm
    @Override
    public long countGroupsInOneYearFromNow() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startDate = now.minusYears(1);
        LocalDateTime endDate = now;
        Date startDateAsDate = Date.from(startDate.atZone(ZoneId.systemDefault()).toInstant());
        Date endDateAsDate = Date.from(endDate.atZone(ZoneId.systemDefault()).toInstant());
        return groupRepository.countByCreatedAtBetween(startDateAsDate, endDateAsDate);
    }

    @Override
    public List<SearchPostGroup> getGroupsToday() {
        Date startDate = getStartOfDay(new Date());
        Date endDate = getEndOfDay(new Date());
        List<SearchPostGroup> groups = groupRepository.findPostGroupByCreateDateBetween(startDate, endDate);
        return groups;
    }

    @Override
    public List<SearchPostGroup> getGroupsIn7Days() {
        Date startDate = getStartOfDay(getNDaysAgo(6));
        Date endDate = getEndOfDay(new Date());
        List<SearchPostGroup> groups = groupRepository.findPostGroupByCreateDateBetween(startDate, endDate);
        return groups;
    }

    @Override
    public List<SearchPostGroup> getGroupsInMonth() {
        Date startDate = getStartOfDay(getNDaysAgo(30));
        Date endDate = getEndOfDay(new Date());
        List<SearchPostGroup> groups = groupRepository.findPostGroupByCreateDateBetween(startDate, endDate);
        return groups;
    }

    public Date getNDaysAgo(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -days);
        return calendar.getTime();
    }

    @Override
    public ResponseEntity<GenericResponseAdmin> getPostGroupJoinByUserId(String userId, int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage); // Tính toán trang và số lượng phần tử trên trang

        Page<GroupPostResponse> groupPostPage = groupRepository.findPostGroupByUserId(userId, pageable);

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(groupPostPage.getTotalElements());
        pagination.setPages(groupPostPage.getTotalPages());

        if (groupPostPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(false)
                    .message("No Groups Found for User").statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.ok(GenericResponseAdmin.builder().success(true)
                    .message("Retrieved List of Joined Groups Successfully").result(groupPostPage.getContent())
                    .pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    public ResponseEntity<GenericResponse> searchGroupAndUserContainingIgnoreCase(String search, String userIdToken) {
        UserProfileResponse user = userClientService.getUser(userIdToken);
        List<SearchPostGroup> postGroups = groupRepository.findByPostGroupNameIgnoreCaseContaining(search);
        List<SearchPostGroup> simplifiedGroupPosts = new ArrayList<>();
        // Lặp qua danh sách SearchPostGroup và thiết lập giá trị checkUserInGroup
        for (SearchPostGroup group : postGroups) {
            Optional<Group> postGroupOptional = findById(group.getId());
            if (postGroupOptional.isPresent()) {
                String checkUser = checkUserInGroup(user.getUserId(), group.getId());
                if (checkUser.equals("Admin") || checkUser.equals("Member")) {
                    group.setCheckUserInGroup("isMember");
                } else {
                    group.setCheckUserInGroup("isNotMember");
                }
                simplifiedGroupPosts.add(group);
            }
            group.setCountMember(groupMemberRepository.countByGroupId(group.getId()));
            group.setCountFriendJoinnedGroup(0);

        }
        List<SearchUser> users = userClientService.getUsersByName(search);
        List<SearchUser> simplifiedUsers = new ArrayList<>();
        // Lặp qua danh sách SearchUser và thiết lập giá trị getStatusByUserId
        for (SearchUser userItem : users) {
            ResponseEntity<GenericResponse> check = friendClientService.getStatusByUserId(userIdToken, userItem.getUserId());
            if (check.equals("Bạn bè")) {
                userItem.setCheckStatusFriend("isFriend");
            } else {
                userItem.setCheckStatusFriend("isNotFriend");
            }
            simplifiedUsers.add(userItem);
            UserProfileResponse userOptional = userClientService.getUser(userItem.getUserId());
            if (userOptional != null) {
                userItem.setNumberFriend(0);
                userItem.setAddress(userOptional.getAddress());
                userItem.setAvatar(userOptional.getAvatar());
                userItem.setBackground(userOptional.getBackground());
                userItem.setBio(userOptional.getAbout());
            }

        }
        List<PostsResponse> simplifiedUserPosts = new ArrayList<>();
        simplifiedUserPosts = postClientService.getPosts(search);
        List<Object> combinedList = new ArrayList<>();
        combinedList.addAll(postGroups);
        combinedList.addAll(users);
        combinedList.addAll(simplifiedUserPosts);

        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully")
                .result(combinedList).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> getSystemGroups() {
        List<Group> groups = groupRepository.findAllByIsSystem(true);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Get successfully")
                .result(groups).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateGroupFromExcel(List<GroupResponse> groupResponses) {
        log.info("GroupServiceImpl, updateGroupFromExcel");
        for (GroupResponse groupResponse : groupResponses) {
            Group group = groupRepository.findByPostGroupName(groupResponse.getName()).orElse(null);
            if (group == null) {
                group = new Group();
                group.setId(UUID.randomUUID().toString());
                group.setPostGroupName(groupResponse.getName());
                if (groupResponse.getDescription() != null) {
                    group.setBio(groupResponse.getDescription());
                }
                group.setIsSystem(false);
                group.setIsPublic(true);
                group.setIsApprovalRequired(false);
                group.setIsActive(true);
                group.setCreatedAt(new Date());
                group.setUpdatedAt(new Date());
                group.setAuthorId(groupResponse.getUserId());
                groupRepository.save(group);
                GroupMember groupMember = GroupMember.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(groupResponse.getUserId())
                        .group(group)
                        .isLocked(false)
                        .memberRequestId(groupResponse.getUserId())
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .role(groupResponse.getRole() != null ? groupResponse.getRole() : GroupMemberRoleType.Admin)
                        .build();
                groupMemberRepository.save(groupMember);

            } else {
                GroupMember groupMember = GroupMember.builder()
                        .id(UUID.randomUUID().toString())
                        .userId(groupResponse.getUserId())
                        .group(group)
                        .isLocked(false)
                        .memberRequestId(groupResponse.getUserId())
                        .createdAt(new Date())
                        .updatedAt(new Date())
                        .role(groupResponse.getRole() != null ? groupResponse.getRole() : GroupMemberRoleType.Member)
                        .build();
                groupMemberRepository.save(groupMember);
            }
            // Thêm vào các nhóm của hê thống theo role của user
            if (groupResponse.getRoleUser().equals(RoleName.SinhVien)){
                addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_STUDENT);
            } else if (groupResponse.getRoleUser().equals(RoleName.NhanVien)) {
                addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_STAFF);
            } else if (groupResponse.getRoleUser().equals(RoleName.PhuHuynh)) {
                addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_PARENT);
            } else if (groupResponse.getRoleUser().equals(RoleName.GiangVien)) {
                addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_TEACHER);
            }
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true)
                .message("Tạo nhóm thành công!")
                .result(null).statusCode(HttpStatus.OK.value()).build());
    }

    public void addMemberToSystemGroup(String userId, String groupName) {
        Group group = groupRepository.findByPostGroupName(groupName).orElse(null);
        if (group != null) {
            GroupMember groupMember = GroupMember.builder()
                    .id(UUID.randomUUID().toString())
                    .userId(userId)
                    .group(group)
                    .isLocked(false)
                    .memberRequestId(userId)
                    .createdAt(new Date())
                    .updatedAt(new Date())
                    .role(GroupMemberRoleType.Member)
                    .build();
            groupMemberRepository.save(groupMember);
        }
    }
    @Override
    public AdminInGroup checkAdminInGroup(String groupName) {
        log.info("GroupServiceImpl, checkAdminInGroup");
        Optional<Group> group = groupRepository.findByPostGroupName(groupName);
        if (group.isPresent()) {
            List<GroupMember> groupMembers = groupMemberRepository.findAllByGroupId(group.get().getId());
            for (GroupMember groupMember : groupMembers) {
                if (groupMember.getRole().equals(GroupMemberRoleType.Admin)) {
                    return AdminInGroup.ADMIN;
                }
            }
            return AdminInGroup.NOT_ADMIN;
        } else{
            return AdminInGroup.NOT_FOUND;
        }
    }
    @Override
    public void deleteMemberInGroup(List<String> userIds) {
        log.info("GroupServiceImpl, deleteMemberInGroup");
        for (String userId : userIds) {
            groupMemberRepository.deleteByUserId(userId);
        }
    }

    @Override
    public ResponseEntity<GenericResponse> addMemberToSystemGroup(GroupResponse groupResponse) {
        log.info("GroupServiceImpl, addMemberToSystemGroup");
        if(groupResponse.getRoleUser().equals(RoleName.SinhVien)){
            addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_STUDENT);
        } else if(groupResponse.getRoleUser().equals(RoleName.NhanVien)){
            addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_STAFF);
        } else if(groupResponse.getRoleUser().equals(RoleName.PhuHuynh)){
            addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_PARENT);
        } else if(groupResponse.getRoleUser().equals(RoleName.GiangVien)){
            addMemberToSystemGroup(groupResponse.getUserId(), AppConstant.GROUP_NAME_TEACHER);
        }
        return ResponseEntity.ok(GenericResponse.builder().success(true)
                .message("Thêm thành viên vào nhóm hệ thống thành công!")
                .result(null).statusCode(HttpStatus.OK.value()).build());
    }
}

