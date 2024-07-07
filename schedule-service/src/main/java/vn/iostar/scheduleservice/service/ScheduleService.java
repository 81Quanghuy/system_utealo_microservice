package vn.iostar.scheduleservice.service;

import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.FileRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.GenericResponseAdmin;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.entity.Schedule;

import java.io.IOException;
import java.text.ParseException;
import java.util.Optional;

public interface ScheduleService extends RedisService{
    <S extends Schedule> S save(S entity);
    Optional<Schedule> findById(String id);
    // Lấy thời khóa biểu
    ResponseEntity<GenericResponse> getSchedule(String currentUserId, Pageable pageable);
    // Cập nhật thời khóa biểu
    ResponseEntity<Object> updateSchedule(String scheduleId, ScheduleRequest request, String currentUserId);
    // Cập nhật thời khóa biểu chi tiết
    ResponseEntity<Object> updateScheduleDetail(String scheduleDetailId, ScheduleDetailRequest request, String currentUserId);
    // Tạo thời khóa biểu
    ResponseEntity<Object> createSchedule(String token, ScheduleRequest requestDTO);
    // Tạo danh sách thời khóa biểu chi tiết
    ResponseEntity<Object> createScheduleDetailList(String token, ScheduleRequest requestDTO);
    // Tạo thời khóa biểu chi tiết
    ResponseEntity<Object> createScheduleDetail(String token, ScheduleDetailRequest requestDTO);
    // Thêm ScheduleDetial vào Schedule
    ResponseEntity<Object> addScheduleDetailtoSchdule(String currentUserId, AddScheduleDetailRequest requestDTO);
    // Lấy thời khóa biểu của người khác
    ResponseEntity<GenericResponse> getScheduleofOtherUser(String currentUserId,String userId, Pageable pageable);
    // Import thời khóa biểu chi tiết từ file excel
    ResponseEntity<Object> importScheduleDetails(FileRequest file)  throws IOException, ParseException;
    // Import thời khóa biểu từ file excel
    ResponseEntity<Object> importSchedule(FileRequest file)  throws IOException, ParseException;
    // Lấy tất cả thời khóa biểu chi tiết trong hệ thống có phân trang
    ResponseEntity<GenericResponseAdmin> getAllScheduleDetails(int page, int items);
    // Lấy tất cả thời khóa biểu trong hệ thống có phân trang
    ResponseEntity<Object> getAllSchedules(int page, int items);
    // Xóa môn học ra khỏi Schedule
    ResponseEntity<Object> deleteScheduleDetail(String scheduleDetailId);
}
