package vn.iostar.groupservice.service.impl;

import vn.iostar.groupservice.constant.AppConstant;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.dto.EventDto;
import vn.iostar.groupservice.dto.request.CreateEventRequest;
import vn.iostar.groupservice.dto.request.ModifyEventRequest;
import vn.iostar.groupservice.dto.response.GenericResponse;
import vn.iostar.groupservice.entity.Event;
import vn.iostar.groupservice.entity.GroupMember;
import vn.iostar.groupservice.exception.wrapper.ForbiddenException;
import vn.iostar.groupservice.exception.wrapper.NotFoundException;
import vn.iostar.groupservice.repository.EventRepository;
import vn.iostar.groupservice.repository.GroupMemberRepository;
import vn.iostar.groupservice.repository.GroupRepository;
import vn.iostar.groupservice.service.EventService;
import vn.iostar.groupservice.service.MapperService;
import vn.iostar.groupservice.util.DateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {
    private final GroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final EventRepository eventRepository;
    private final MapperService mappingService;
    @Override
    public ResponseEntity<GenericResponse> createEvent(String userId, CreateEventRequest createEventRequest) throws ParseException {
        log.info("EventServiceImpl, createEvent");
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, createEventRequest.getGroupId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền tạo sự kiện cho nhóm này"));
        Event event = eventRepository.save(Event.builder()
                .id(UUID.randomUUID().toString())
                .group(groupMember.getGroup())
                .authorId(userId)
                .title(createEventRequest.getEventName())
                .description(createEventRequest.getEventDescription())
                .startedAt(DateUtil.string2Date(createEventRequest.getStartDate(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .endedAt(DateUtil.string2Date(createEventRequest.getEndDate(), AppConstant.LOCAL_DATE_TIME_FORMAT))
                .createdAt(new Date())
                .build());
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Tạo sự kiện thành công")
                .result(mappingService.mapToEventDto(event))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getEvents(String userId, String groupId) {
        log.info("EventServiceImpl, getEvents");
        List<EventDto> eventDtos = eventRepository.findAllByGroupId(groupId)
                .stream()
                .map(mappingService::mapToEventDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách sự kiện thành công")
                .result(eventDtos)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> updateEvent(String userId, String eventId, ModifyEventRequest modifyEventRequest) throws ParseException {
        log.info("EventServiceImpl, updateEvent");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Sự kiện không tồn tại"));
        if (!event.getAuthorId().equals(userId)) {
            throw new ForbiddenException("Bạn không có quyền chỉnh sửa sự kiện này");
        }
        event.setTitle(modifyEventRequest.getEventName());
        event.setDescription(modifyEventRequest.getEventDescription());
        event.setStartedAt(DateUtil.string2Date(modifyEventRequest.getStartDate(), AppConstant.LOCAL_DATE_TIME_FORMAT));
        event.setEndedAt(DateUtil.string2Date(modifyEventRequest.getEndDate(), AppConstant.LOCAL_DATE_TIME_FORMAT));
        eventRepository.save(event);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Cập nhật sự kiện thành công")
                .result(mappingService.mapToEventDto(event))
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> deleteEvent(String userId, String eventId) {
        log.info("EventServiceImpl, deleteEvent");
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Sự kiện không tồn tại"));
        GroupMember groupMember = groupMemberRepository.findByUserIdAndGroupId(userId, event.getGroup().getId())
                .orElseThrow(() -> new ForbiddenException("Bạn không có quyền xóa sự kiện này"));
        if (groupMember.getRole().equals(GroupMemberRoleType.Member)) {
            throw new ForbiddenException("Bạn không có quyền xóa sự kiện này");
        }
        eventRepository.delete(event);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Xóa sự kiện thành công")
                .result(null)
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getHomeEvents(String userId) {
        log.info("EventServiceImpl, getHomeEvents");
        // get all group which user is member then get events
        List<GroupMember> groupMembers = groupMemberRepository.findAllByUserId(userId);
        List<EventDto> events = eventRepository.findAllByGroupInOrderByCreatedAtDesc(groupMembers.stream().map(GroupMember::getGroup).toList())
                .stream()
                .map(mappingService::mapToEventDto)
                .toList();
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .statusCode(200)
                .message("Lấy danh sách sự kiện thành công")
                .result(events)
                .build());
    }
}
