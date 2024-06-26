package vn.iostar.scheduleservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.entity.Schedule;
import vn.iostar.scheduleservice.entity.ScheduleDetail;
import vn.iostar.scheduleservice.repository.ScheduleDetailRepository;
import vn.iostar.scheduleservice.repository.ScheduleRepository;
import vn.iostar.scheduleservice.service.ScheduleService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ScheduleServiceImpl extends RedisServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;

    ObjectMapper objectMapper;

    public ScheduleServiceImpl(RedisTemplate<String, Object> redisTemplate, ScheduleRepository scheduleRepository, ScheduleDetailRepository scheduleDetailRepository) {
        super(redisTemplate);
        this.scheduleRepository = scheduleRepository;
        this.scheduleDetailRepository = scheduleDetailRepository;
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
            scheduleDetail.setTemplate(detailRequest.getTemplate());
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
        schedule.setUserId(requestDTO.getUserId());
        schedule.setSemester(requestDTO.getSemester());
        schedule.setYear(requestDTO.getYear());
        schedule.setWeekOfSemester(requestDTO.getWeekOfSemester());

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

            scheduleDetail.setCourseName(detailRequest.getCourseName());
            scheduleDetail.setInstructorName(detailRequest.getInstructorName());
            scheduleDetail.setRoomName(detailRequest.getRoomName());
            scheduleDetail.setDayOfWeek(detailRequest.getDayOfWeek());
            scheduleDetail.setStartTime(detailRequest.getStartTime());
            scheduleDetail.setEndTime(detailRequest.getEndTime());
            scheduleDetail.setStartPeriod(detailRequest.getStartPeriod());
            scheduleDetail.setEndPeriod(detailRequest.getEndPeriod());
            scheduleDetail.setNote(detailRequest.getNote());
            scheduleDetail.setTemplate(detailRequest.getTemplate());

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
            scheduleDetail.setTemplate(detailRequest.getTemplate());
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
}


