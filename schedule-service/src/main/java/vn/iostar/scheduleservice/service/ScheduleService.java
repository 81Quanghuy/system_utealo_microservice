package vn.iostar.scheduleservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.entity.Schedule;

import java.util.Optional;

public interface ScheduleService extends RedisService{
    <S extends Schedule> S save(S entity);
    Optional<Schedule> findById(String id);
    // Lấy thời khóa biểu
    ResponseEntity<GenericResponse> getSchedule(String currentUserId, Pageable pageable);
    // Cập nhật thời khóa biểu
    ResponseEntity<Object> updateSchedule(String scheduleId, ScheduleRequest request, String currentUserId);
    // Tạo thời khóa biểu
    ResponseEntity<Object> createSchedule(String token, ScheduleRequest requestDTO);
    // Tạo thời khóa biểu
    ResponseEntity<Object> createScheduleDetail(String token, ScheduleRequest requestDTO);
    // Thêm ScheduleDetial vào Schedule
    ResponseEntity<Object> addScheduleDetailtoSchdule(String currentUserId, AddScheduleDetailRequest requestDTO);
}
