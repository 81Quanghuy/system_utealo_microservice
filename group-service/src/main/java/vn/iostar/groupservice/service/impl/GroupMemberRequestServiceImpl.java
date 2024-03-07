package vn.iostar.groupservice.service.impl;

import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.constant.StateType;
import vn.iostar.groupservice.dto.GroupMemberRequestDto;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.entity.Group;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.exception.wrapper.ForbiddenException;
import vn.iostar.groupservice.exception.wrapper.NotFoundException;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.repository.GroupMemberRequestRepository;
import vn.iostar.groupservice.repository.GroupRepository;
import vn.iostar.groupservice.service.GroupRequestService;
import vn.iostar.groupservice.service.MapperService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class GroupMemberRequestServiceImpl implements GroupRequestService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupMemberRequestRepository groupMemberRequestRepository;
    private final MapperService mappingService;

    @Override
    public ResponseEntity<GenericResponse> getAllGroupMemberRequests(String userId, String groupId, Optional<String> stateCode) {
        log.info("GroupMemberRequestServiceImpl, ResponseEntity<GenericResponse> getAllGroupMemberRequests");
        Group group = groupRepository.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Nhóm không tồn tại"));
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, group.getId())
                .orElseThrow(() -> new NotFoundException("Bạn không thuộc nhóm này"));
        if (groupMember.getRole().equals(GroupMemberRoleType.Member)) {
            throw new ForbiddenException("Bạn không có quyền truy cập");
        }
        StateType stateCodeValue = stateCode.map(StateType::valueOf).orElse(null);
        List<GroupMemberRequestDto> groupMemberRequestDtos;
        if (stateCodeValue == null) {
            groupMemberRequestDtos = groupMemberRequestRepository
                    .findAllByGroupId(groupId)
                    .stream()
                    .map(mappingService::mapToGroupMemberRequestDto)
                    .toList();
        }
        else {
            groupMemberRequestDtos = groupMemberRequestRepository
                    .findAllByGroupIdAndState(groupId, stateCodeValue)
                    .stream()
                    .map(mappingService::mapToGroupMemberRequestDto)
                    .toList();
        }
        return ResponseEntity.ok(
                GenericResponse.builder()
                        .success(true)
                        .statusCode(200)
                        .message("Lấy danh sách yêu cầu thành công")
                        .result(groupMemberRequestDtos)
                        .build()
        );
    }
}
