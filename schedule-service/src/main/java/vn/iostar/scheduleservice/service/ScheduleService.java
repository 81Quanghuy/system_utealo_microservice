package vn.iostar.scheduleservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.entity.Schedule;

import java.util.Optional;

public interface ScheduleService extends RedisService{
    <S extends Schedule> S save(S entity);
    Optional<Schedule> findById(String id);
    // Lấy thời khóa biểu của chính mình
    ResponseEntity<GenericResponse> getMySchedule(String currentUserId, Pageable pageable);
    // Lấy thời kho biểu của người khác (Admin)
    ResponseEntity<GenericResponse> getScheduleOfUser(String currentUserId, String userId, Pageable pageable);
    // Cập nhật thời khóa biểu
    ResponseEntity<Object> updateSchedule(String postId, ScheduleRequest request, String currentUserId) throws Exception;
    // Tạo thời khóa biểu
    ResponseEntity<Object> createSchedule(String token, ScheduleRequest requestDTO) throws Exception;
}
