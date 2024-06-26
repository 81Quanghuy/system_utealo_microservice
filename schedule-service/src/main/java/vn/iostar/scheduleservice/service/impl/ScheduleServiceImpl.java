package vn.iostar.scheduleservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.catalina.User;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.model.RelationshipResponse;
import vn.iostar.scheduleservice.constant.RoleName;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.dto.response.UserProfileResponse;
import vn.iostar.scheduleservice.entity.Schedule;
import vn.iostar.scheduleservice.entity.ScheduleDetail;
import vn.iostar.scheduleservice.repository.ScheduleDetailRepository;
import vn.iostar.scheduleservice.repository.ScheduleRepository;
import vn.iostar.scheduleservice.service.ScheduleService;
import vn.iostar.scheduleservice.service.client.UserClientService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleServiceImpl extends RedisServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final UserClientService userClientService;

    ObjectMapper objectMapper;

    public ScheduleServiceImpl(RedisTemplate<String, Object> redisTemplate, ScheduleRepository scheduleRepository, ScheduleDetailRepository scheduleDetailRepository, UserClientService userClientService) {
        super(redisTemplate);
        this.scheduleRepository = scheduleRepository;
        this.scheduleDetailRepository = scheduleDetailRepository;
        this.userClientService = userClientService;
    }


    @Override
    public <S extends Schedule> S save(S entity) {
        return scheduleRepository.save(entity);
    }

    @Override
    public Optional<Schedule> findById(String id) {
        return scheduleRepository.findById(id);
    }

    @Override
    public ResponseEntity<GenericResponse> getSchedule(String userId, Pageable pageable) {
        List<Schedule> schedules = scheduleRepository.findByUserId(userId);
        return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved schedules successfully")
                .result(schedules).statusCode(HttpStatus.OK.value()).build());
    }

    @Override
    public ResponseEntity<Object> createSchedule(String userId, ScheduleRequest requestDTO) {

        String scheduleId = UUID.randomUUID().toString();
        Schedule schedule = new Schedule();
        schedule.setId(scheduleId);
        schedule.setUserId(requestDTO.getUserId());
        schedule.setCreaterId(userId);
        schedule.setSemester(requestDTO.getSemester());
        schedule.setYear(requestDTO.getYear());
        schedule.setWeekOfSemester(requestDTO.getWeekOfSemester());

        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        for (ScheduleDetailRequest detailRequest : requestDTO.getScheduleDetails()) {
            String scheduleDetailId = UUID.randomUUID().toString();
            ScheduleDetail scheduleDetail = new ScheduleDetail();
            scheduleDetail.setId(scheduleDetailId);
            scheduleDetail.setCourseName(detailRequest.getCourseName());
            scheduleDetail.setInstructorName(detailRequest.getInstructorName());
            scheduleDetail.setRoomName(detailRequest.getRoomName());
            scheduleDetail.setDayOfWeek(detailRequest.getDayOfWeek());
            scheduleDetail.setStartTime(detailRequest.getStartTime());
            scheduleDetail.setEndTime(detailRequest.getEndTime());
            scheduleDetail.setStartPeriod(detailRequest.getStartPeriod());
            scheduleDetail.setEndPeriod(detailRequest.getEndPeriod());
            scheduleDetail.setNote(detailRequest.getNote());
            scheduleDetail.setBasis(detailRequest.getBasis());
            scheduleDetail.setNumber(detailRequest.getNumber());
            scheduleDetailRepository.save(scheduleDetail);
            scheduleDetails.add(scheduleDetail);
        }

        schedule.setScheduleDetails(scheduleDetails);

        scheduleRepository.save(schedule);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Created schedule successfully")
                .result(schedule)
                .statusCode(HttpStatus.OK.value())
                .build());
    }


    public ResponseEntity<Object> updateSchedule(String scheduleId, ScheduleRequest requestDTO, String currentUserId) {
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(scheduleId);
        if (!optionalSchedule.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Schedule not found")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build());
        }

        Schedule schedule = optionalSchedule.get();
        if (!requestDTO.getUserId().isEmpty()) {
            schedule.setUserId(requestDTO.getUserId());
        }
        if (checkNull(requestDTO.getSemester())) {
            schedule.setSemester(requestDTO.getSemester());
        }
        if (checkNull(requestDTO.getYear())) {
            schedule.setYear(requestDTO.getYear());
        }
        if (!requestDTO.getWeekOfSemester().isEmpty()) {
            schedule.setWeekOfSemester(requestDTO.getWeekOfSemester());
        }

        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        for (ScheduleDetailRequest detailRequest : requestDTO.getScheduleDetails()) {
            Optional<ScheduleDetail> optionalScheduleDetail = scheduleDetailRepository.findById(detailRequest.getScheduleDetailId());
            ScheduleDetail scheduleDetail;
            if (optionalScheduleDetail.isPresent()) {
                scheduleDetail = optionalScheduleDetail.get();
            } else {
                scheduleDetail = new ScheduleDetail();
                scheduleDetail.setId(UUID.randomUUID().toString());
            }
            if (checkNull(detailRequest.getCourseName())) {
                scheduleDetail.setCourseName(detailRequest.getCourseName());
            }
            if (checkNull(detailRequest.getInstructorName())) {
                scheduleDetail.setInstructorName(detailRequest.getInstructorName());
            }
            if (checkNull(detailRequest.getRoomName())) {
                scheduleDetail.setRoomName(detailRequest.getRoomName());
            }
            if (checkNull(detailRequest.getDayOfWeek())) {
                scheduleDetail.setDayOfWeek(detailRequest.getDayOfWeek());
            }
            if (checkNull(detailRequest.getStartTime())) {
                scheduleDetail.setStartTime(detailRequest.getStartTime());
            }
            if (checkNull(detailRequest.getEndTime())) {
                scheduleDetail.setEndTime(detailRequest.getEndTime());
            }
            if (checkNull(detailRequest.getStartPeriod())) {
                scheduleDetail.setStartPeriod(detailRequest.getStartPeriod());
            }
            if (checkNull(detailRequest.getEndPeriod())) {
                scheduleDetail.setEndPeriod(detailRequest.getEndPeriod());
            }
            if (checkNull(detailRequest.getNote())) {
                scheduleDetail.setNote(detailRequest.getNote());
            }
            if (checkNull(detailRequest.getBasis())) {
                scheduleDetail.setBasis(detailRequest.getBasis());
            }
            if (checkNull(detailRequest.getNumber())) {
                scheduleDetail.setNumber(detailRequest.getNumber());
            }

            scheduleDetailRepository.save(scheduleDetail);
            scheduleDetails.add(scheduleDetail);
        }


        schedule.setScheduleDetails(scheduleDetails);

        scheduleRepository.save(schedule);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Updated schedule successfully")
                .result(schedule)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<Object> updateScheduleDetail(String scheduleDetailId, ScheduleDetailRequest request, String currentUserId) {
        Optional<ScheduleDetail> optionalScheduleDetail = scheduleDetailRepository.findById(scheduleDetailId);
        if (optionalScheduleDetail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Schedule detail not found")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build());
        }

        ScheduleDetail scheduleDetail = optionalScheduleDetail.get();
        if (checkNull(request.getCourseName())) {
            scheduleDetail.setCourseName(request.getCourseName());
        }
        if (checkNull(request.getInstructorName())) {
            scheduleDetail.setInstructorName(request.getInstructorName());
        }
        if (checkNull(request.getRoomName())) {
            scheduleDetail.setRoomName(request.getRoomName());
        }
        if (checkNull(request.getDayOfWeek())) {
            scheduleDetail.setDayOfWeek(request.getDayOfWeek());
        }
        if (checkNull(request.getStartTime())) {
            scheduleDetail.setStartTime(request.getStartTime());
        }
        if (checkNull(request.getEndTime())) {
            scheduleDetail.setEndTime(request.getEndTime());
        }
        if (checkNull(request.getStartPeriod())) {
            scheduleDetail.setStartPeriod(request.getStartPeriod());
        }
        if (checkNull(request.getEndPeriod())) {
            scheduleDetail.setEndPeriod(request.getEndPeriod());
        }
        if (checkNull(request.getNote())) {
            scheduleDetail.setNote(request.getNote());
        }
        if (checkNull(request.getBasis())) {
            scheduleDetail.setBasis(request.getBasis());
        }
        if (checkNull(request.getNumber())) {
            scheduleDetail.setNumber(request.getNumber());
        }
        scheduleDetailRepository.save(scheduleDetail);

        // Kiểm tra scheduleDetail có trong schedule nào không và cập nhật lại
        List<Schedule> schedules = scheduleRepository.findByScheduleDetails(optionalScheduleDetail.get());
        for (Schedule schedule : schedules) {
            schedule.getScheduleDetails().remove(scheduleDetail);
            schedule.getScheduleDetails().add(scheduleDetail);
            scheduleRepository.save(schedule);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Updated schedule detail successfully")
                .result(scheduleDetail)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    // Tạo 1 hàm để kiểm tra null truyền 1 tham số string
    private Boolean checkNull(String value) {
        return value != null && !value.isEmpty() && !value.isBlank() && !value.equals("null");
    }


    @Override
    public ResponseEntity<Object> createScheduleDetail(String userId, ScheduleRequest requestDTO) {

        List<ScheduleDetail> scheduleDetails = new ArrayList<>();
        for (ScheduleDetailRequest detailRequest : requestDTO.getScheduleDetails()) {
            String scheduleDetailId = UUID.randomUUID().toString();
            ScheduleDetail scheduleDetail = new ScheduleDetail();
            scheduleDetail.setId(scheduleDetailId);
            scheduleDetail.setCourseName(detailRequest.getCourseName());
            scheduleDetail.setInstructorName(detailRequest.getInstructorName());
            scheduleDetail.setRoomName(detailRequest.getRoomName());
            scheduleDetail.setDayOfWeek(detailRequest.getDayOfWeek());
            scheduleDetail.setStartTime(detailRequest.getStartTime());
            scheduleDetail.setEndTime(detailRequest.getEndTime());
            scheduleDetail.setStartPeriod(detailRequest.getStartPeriod());
            scheduleDetail.setEndPeriod(detailRequest.getEndPeriod());
            scheduleDetail.setNote(detailRequest.getNote());
            scheduleDetail.setBasis(detailRequest.getBasis());
            scheduleDetail.setNumber(detailRequest.getNumber());
            scheduleDetailRepository.save(scheduleDetail);
            scheduleDetails.add(scheduleDetail);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Created schedule detail successfully")
                .result(scheduleDetails)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<Object> addScheduleDetailtoSchdule(String currentUserId, AddScheduleDetailRequest requestDTO) {
        Optional<ScheduleDetail> optionalScheduleDetail = scheduleDetailRepository.findById(requestDTO.getScheduleDetailId());
        if (!optionalScheduleDetail.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Schedule detail not found")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build());
        }
        Optional<Schedule> optionalSchedule = scheduleRepository.findById(requestDTO.getScheduleId());
        if (!optionalSchedule.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Schedule not found")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build());
        }

        Schedule schedule = optionalSchedule.get();
        ScheduleDetail scheduleDetail = optionalScheduleDetail.get();
        schedule.getScheduleDetails().add(scheduleDetail);
        scheduleRepository.save(schedule);

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Added schedule detail to schedule successfully")
                .result(schedule)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getScheduleofOtherUser(String currentUserId,String userId, Pageable pageable) {
        UserProfileResponse currentUser = userClientService.getUser(currentUserId);
        if (currentUser.getRoleName().equals(RoleName.Admin)) {
            List<Schedule> schedules = scheduleRepository.findByUserId(userId);
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved schedules successfully")
                    .result(schedules).statusCode(HttpStatus.OK.value()).build());
        }
        else if(currentUser.getRoleName().equals(RoleName.PhuHuynh)){
            RelationshipResponse relationship = userClientService.getRelationship(currentUser.getUserId(), userId);
            if (relationship != null && relationship.getIsAccepted()) {
                List<Schedule> schedules = scheduleRepository.findByUserId(userId);
                return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved schedules successfully")
                        .result(schedules).statusCode(HttpStatus.OK.value()).build());
            } else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                        .body(GenericResponse.builder()
                                .success(false)
                                .message("You are not allowed to access this resource")
                                .statusCode(HttpStatus.FORBIDDEN.value())
                                .build());
            }

        } else {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("You are not allowed to access this resource")
                            .statusCode(HttpStatus.FORBIDDEN.value())
                            .build());
        }
    }
}


