package vn.iostar.scheduleservice.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import vn.iostar.model.RelationshipResponse;
import vn.iostar.scheduleservice.constant.RoleName;
import vn.iostar.scheduleservice.dto.GenericResponse;
import vn.iostar.scheduleservice.dto.PaginationInfo;
import vn.iostar.scheduleservice.dto.request.AddScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.FileRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleDetailRequest;
import vn.iostar.scheduleservice.dto.request.ScheduleRequest;
import vn.iostar.scheduleservice.dto.response.BadRequestException;
import vn.iostar.scheduleservice.dto.response.GenericResponseAdmin;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.dto.response.UserProfileResponse;
import vn.iostar.scheduleservice.entity.Schedule;
import vn.iostar.scheduleservice.entity.ScheduleDetail;
import vn.iostar.scheduleservice.repository.ScheduleDetailRepository;
import vn.iostar.scheduleservice.repository.ScheduleRepository;
import vn.iostar.scheduleservice.service.ScheduleService;
import vn.iostar.scheduleservice.service.client.UserClientService;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ScheduleServiceImpl extends RedisServiceImpl implements ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final ScheduleDetailRepository scheduleDetailRepository;
    private final UserClientService userClientService;
    final String indexCell = "Tại dòng ";

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
    public ResponseEntity<Object> createScheduleDetailList(String userId, ScheduleRequest requestDTO) {

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
    public ResponseEntity<Object> createScheduleDetail(String userId, ScheduleDetailRequest requestDTO) {
        String scheduleDetailId = UUID.randomUUID().toString();
        ScheduleDetail scheduleDetail = new ScheduleDetail();
        scheduleDetail.setId(scheduleDetailId);
        scheduleDetail.setCourseName(requestDTO.getCourseName());
        scheduleDetail.setInstructorName(requestDTO.getInstructorName());
        scheduleDetail.setRoomName(requestDTO.getRoomName());
        scheduleDetail.setDayOfWeek(requestDTO.getDayOfWeek());
        scheduleDetail.setStartTime(requestDTO.getStartTime());
        scheduleDetail.setEndTime(requestDTO.getEndTime());
        scheduleDetail.setStartPeriod(requestDTO.getStartPeriod());
        scheduleDetail.setEndPeriod(requestDTO.getEndPeriod());
        scheduleDetail.setNote(requestDTO.getNote());
        scheduleDetail.setBasis(requestDTO.getBasis());
        scheduleDetail.setNumber(requestDTO.getNumber());
        scheduleDetailRepository.save(scheduleDetail);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Created schedule detail successfully")
                .result(scheduleDetail)
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
        Schedule schedule = scheduleRepository.findByYearAndSemesterAndWeekOfSemester(requestDTO.getYear(), requestDTO.getSemester(), requestDTO.getWeekOfSemester());
        if (schedule == null) {
            schedule = new Schedule();
            schedule.setId(UUID.randomUUID().toString());
            schedule.setYear(requestDTO.getYear());
            schedule.setSemester(requestDTO.getSemester());
            schedule.setWeekOfSemester(requestDTO.getWeekOfSemester());
            List<String> userIds = new ArrayList<>();
            userIds.add(requestDTO.getUserId());
            schedule.setUserId(userIds);
            ScheduleDetail scheduleDetail = optionalScheduleDetail.get();
            List<ScheduleDetail> scheduleDetails = new ArrayList<>();
            scheduleDetails.add(scheduleDetail);
            schedule.setScheduleDetails(scheduleDetails);
            scheduleRepository.save(schedule);
            return ResponseEntity.ok(GenericResponse.builder()
                    .success(true)
                    .message("Tạo thời khóa biểu thành công")
                    .result(schedule)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }

        ScheduleDetail scheduleDetail = optionalScheduleDetail.get();
        for (ScheduleDetail detail : schedule.getScheduleDetails()) {
            if (detail.getId().equals(scheduleDetail.getId())) {
                return ResponseEntity.ok(GenericResponse.builder()
                        .success(false)
                        .message("Môn học đã tồn tại trong thời khóa biểu")
                        .statusCode(300)
                        .build());

            }
        }
        schedule.getScheduleDetails().add(scheduleDetail);
        scheduleRepository.save(schedule);
        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Thêm môn học vào thời khóa biểu thành công")
                .result(schedule)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

    @Override
    public ResponseEntity<GenericResponse> getScheduleofOtherUser(String currentUserId, String userId, Pageable pageable) {
        UserProfileResponse currentUser = userClientService.getUser(currentUserId);
        if (currentUser.getRoleName().equals(RoleName.Admin)) {
            List<Schedule> schedules = scheduleRepository.findByUserId(userId);
            return ResponseEntity.ok(GenericResponse.builder().success(true).message("Retrieved schedules successfully")
                    .result(schedules).statusCode(HttpStatus.OK.value()).build());
        } else if (currentUser.getRoleName().equals(RoleName.PhuHuynh)) {
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

    @Override
    public ResponseEntity<Object> importScheduleDetails(FileRequest fileRequest) throws IOException, ParseException {
        MultipartFile multipartFile = fileRequest.getFile();
        InputStream inputStream = multipartFile.getInputStream();
        int countMember = 0;

        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            List<ScheduleDetail> scheduleDetails = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // Kiểm tra nếu hàng không tồn tại
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    break; // Dừng vòng lặp nếu gặp hàng không tồn tại
                }

                ScheduleDetailRequest scheduleDetailRequest = mapRowToScheduleDetail(sheet, i, inputStream);
                if (scheduleDetailRequest == null) {
                    continue; // Bỏ qua hàng này nếu row là null
                }

                ScheduleDetail scheduleDetail = new ScheduleDetail();
                String scheduleDetailId = UUID.randomUUID().toString();
                scheduleDetail.setId(scheduleDetailId);
                if (scheduleDetailRequest.getCourseName() != null) {
                    scheduleDetail.setCourseName(scheduleDetailRequest.getCourseName());
                }
                if (scheduleDetailRequest.getInstructorName() != null) {
                    scheduleDetail.setInstructorName(scheduleDetailRequest.getInstructorName());
                }
                if (scheduleDetailRequest.getRoomName() != null) {
                    scheduleDetail.setRoomName(scheduleDetailRequest.getRoomName());
                }
                if (scheduleDetailRequest.getDayOfWeek() != null) {
                    scheduleDetail.setDayOfWeek(scheduleDetailRequest.getDayOfWeek());
                }
                if (scheduleDetailRequest.getStartTime() != null) {
                    scheduleDetail.setStartTime(scheduleDetailRequest.getStartTime());
                }
                if (scheduleDetailRequest.getEndTime() != null) {
                    scheduleDetail.setEndTime(scheduleDetailRequest.getEndTime());
                }
                if (scheduleDetailRequest.getStartPeriod() != null) {
                    scheduleDetail.setStartPeriod(scheduleDetailRequest.getStartPeriod());
                }
                if (scheduleDetailRequest.getEndPeriod() != null) {
                    scheduleDetail.setEndPeriod(scheduleDetailRequest.getEndPeriod());
                }
                if (scheduleDetailRequest.getNote() != null) {
                    scheduleDetail.setNote(scheduleDetailRequest.getNote());
                }
                if (scheduleDetailRequest.getBasis() != null) {
                    scheduleDetail.setBasis(scheduleDetailRequest.getBasis());
                }
                if (scheduleDetailRequest.getNumber() != null) {
                    scheduleDetail.setNumber(scheduleDetailRequest.getNumber());
                }
                scheduleDetails.add(scheduleDetail);
                countMember++;
            }

            // Đóng file Excel
            workbook.close();

            // Lưu danh sách scheduleDetails vào database
            scheduleDetailRepository.saveAll(scheduleDetails);
        }

        inputStream.close();
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder().success(true).message("Có " + countMember + " dòng đã được thêm vào hệ thống!!!").statusCode(HttpStatus.OK.value()).build()
        );
    }

    @Override
    public ResponseEntity<Object> importSchedule(FileRequest fileRequest) throws IOException, ParseException {
        MultipartFile multipartFile = fileRequest.getFile();
        InputStream inputStream = multipartFile.getInputStream();
        int countMember = 0;
        Schedule schedule = new Schedule();
        String scheduleId = UUID.randomUUID().toString();
        schedule.setId(scheduleId);
        if (fileRequest.getUserId() == null) {
            schedule.setUserId(new ArrayList<>());
        } else {
            schedule.setUserId(fileRequest.getUserId());
        }
        schedule.setSemester(fileRequest.getSemester());
        schedule.setYear(fileRequest.getYear());
        schedule.setWeekOfSemester(fileRequest.getWeekOfSemester());
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            XSSFSheet sheet = workbook.getSheetAt(0); // Lấy sheet đầu tiên
            List<ScheduleDetail> scheduleDetails = new ArrayList<>();

            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                // Kiểm tra nếu hàng không tồn tại
                XSSFRow row = sheet.getRow(i);
                if (row == null) {
                    break; // Dừng vòng lặp nếu gặp hàng không tồn tại
                }

                ScheduleDetailRequest scheduleDetailRequest = mapRowToScheduleDetail(sheet, i, inputStream);
                if (scheduleDetailRequest == null) {
                    continue; // Bỏ qua hàng này nếu row là null
                }

                ScheduleDetail scheduleDetail = new ScheduleDetail();
                String scheduleDetailId = UUID.randomUUID().toString();
                scheduleDetail.setId(scheduleDetailId);
                if (scheduleDetailRequest.getCourseName() != null) {
                    scheduleDetail.setCourseName(scheduleDetailRequest.getCourseName());
                }
                if (scheduleDetailRequest.getInstructorName() != null) {
                    scheduleDetail.setInstructorName(scheduleDetailRequest.getInstructorName());
                }
                if (scheduleDetailRequest.getRoomName() != null) {
                    scheduleDetail.setRoomName(scheduleDetailRequest.getRoomName());
                }
                if (scheduleDetailRequest.getDayOfWeek() != null) {
                    scheduleDetail.setDayOfWeek(scheduleDetailRequest.getDayOfWeek());
                }
                if (scheduleDetailRequest.getStartTime() != null) {
                    scheduleDetail.setStartTime(scheduleDetailRequest.getStartTime());
                }
                if (scheduleDetailRequest.getEndTime() != null) {
                    scheduleDetail.setEndTime(scheduleDetailRequest.getEndTime());
                }
                if (scheduleDetailRequest.getStartPeriod() != null) {
                    scheduleDetail.setStartPeriod(scheduleDetailRequest.getStartPeriod());
                }
                if (scheduleDetailRequest.getEndPeriod() != null) {
                    scheduleDetail.setEndPeriod(scheduleDetailRequest.getEndPeriod());
                }
                if (scheduleDetailRequest.getNote() != null) {
                    scheduleDetail.setNote(scheduleDetailRequest.getNote());
                }
                if (scheduleDetailRequest.getBasis() != null) {
                    scheduleDetail.setBasis(scheduleDetailRequest.getBasis());
                }
                if (scheduleDetailRequest.getNumber() != null) {
                    scheduleDetail.setNumber(scheduleDetailRequest.getNumber());
                }
                scheduleDetails.add(scheduleDetail);
                countMember++;
            }

            schedule.setScheduleDetails(scheduleDetails);
            // Đóng file Excel
            workbook.close();

            // Lưu danh sách scheduleDetails vào database
            scheduleDetailRepository.saveAll(scheduleDetails);
            // Lưu schedule vào database
            scheduleRepository.save(schedule);
        }

        inputStream.close();
        return ResponseEntity.status(HttpStatus.OK).body(
                GenericResponse.builder().success(true).message("Có " + countMember + " dòng đã được thêm vào hệ thống!!!").statusCode(HttpStatus.OK.value()).build()
        );
    }

    @Override
    public ResponseEntity<GenericResponseAdmin> getAllScheduleDetails(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<ScheduleDetail> scheduleDetailsPage = scheduleDetailRepository.findAll(pageable);
        long totalScheduleDetails = scheduleDetailRepository.count();
        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalScheduleDetails);
        pagination.setPages((int) Math.ceil((double) totalScheduleDetails / itemsPerPage));
        if (scheduleDetailsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(true).message("Empty").result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(GenericResponseAdmin.builder().success(true).message("Lấy danh sách thời khóa biểu chi tiết thành công").result(scheduleDetailsPage).pagination(pagination).statusCode(HttpStatus.OK.value()).build());
        }
    }

    // Lấy tất cả thời khóa biểu chi tiết trong hệ thống không có phân trang
    @Override
    public ResponseEntity<Object> getAllScheduleDetails() {
        List<ScheduleDetail> scheduleDetailsPage = scheduleDetailRepository.findAll();
        if (scheduleDetailsPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder().success(true).message("Empty").result(null).statusCode(HttpStatus.NOT_FOUND.value()).build());
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(GenericResponseAdmin.builder().success(true).message("Lấy danh sách thời khóa biểu chi tiết thành công").result(scheduleDetailsPage).statusCode(HttpStatus.OK.value()).build());
        }
    }

    @Override
    public ResponseEntity<Object> getAllSchedules(int page, int itemsPerPage) {
        Pageable pageable = PageRequest.of(page - 1, itemsPerPage);
        Page<Schedule> schedulesPage = scheduleRepository.findAll(pageable);
        long totalSchedules = scheduleRepository.count();

        PaginationInfo pagination = new PaginationInfo();
        pagination.setPage(page);
        pagination.setItemsPerPage(itemsPerPage);
        pagination.setCount(totalSchedules);
        pagination.setPages((int) Math.ceil((double) totalSchedules / itemsPerPage));

        if (schedulesPage.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(GenericResponseAdmin.builder()
                    .success(true)
                    .message("Empty")
                    .result(null)
                    .statusCode(HttpStatus.NOT_FOUND.value())
                    .build());
        } else {
            List<ScheduleResponse> scheduleResponses = schedulesPage.stream().map(schedule -> {
                List<String> userNames = getUserNamesFromIds(schedule.getUserId());
                return new ScheduleResponse(schedule, userNames);
            }).collect(Collectors.toList());

            return ResponseEntity.status(HttpStatus.OK).body(GenericResponseAdmin.builder()
                    .success(true)
                    .message("Lấy danh sách thời khóa biểu thành công")
                    .result(scheduleResponses)
                    .pagination(pagination)
                    .statusCode(HttpStatus.OK.value())
                    .build());
        }
    }

    public List<String> getUserNamesFromIds(List<String> userIds) {
        List<String> userNames = new ArrayList<>();
        for (String userId : userIds) {
//            UserProfileResponse userProfileResponse = userClientService.getUser(userId);
//            userNames.add(userProfileResponse.getUserName());
        }
        return userNames;
    }

    private ScheduleDetailRequest mapRowToScheduleDetail(XSSFSheet sheet, int i, InputStream inputStream) throws IOException {

        XSSFRow row = sheet.getRow(i);
        if (row == null) {
            inputStream.close();
            throw new BadRequestException("Dòng " + i + " không tồn tại.");
        }

        ScheduleDetailRequest scheduleDetailRequest = new ScheduleDetailRequest();
        DataFormatter dataFormatter = new DataFormatter();
        Cell cell = sheet.getRow(i).getCell(0);
        if (cell != null && cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setCourseName(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(0));
        }
        cell = sheet.getRow(i).getCell(1);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setInstructorName(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setInstructorName(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(1));
        }
        cell = sheet.getRow(i).getCell(2);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setRoomName(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setRoomName(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(2));

        }
        cell = sheet.getRow(i).getCell(3);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setDayOfWeek(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setDayOfWeek(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(3));
        }
        cell = sheet.getRow(i).getCell(4);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setStartTime(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setStartTime(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(4));
        }
        cell = sheet.getRow(i).getCell(5);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setEndTime(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setEndTime(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(5));
        }
        cell = sheet.getRow(i).getCell(6);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setStartPeriod(null);
        } else if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC) {
            String cellValue = dataFormatter.formatCellValue(cell);
            scheduleDetailRequest.setStartPeriod(cellValue);
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(6));
        }
        cell = sheet.getRow(i).getCell(7);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setStartPeriod(null);
        } else if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC) {
            String cellValue = dataFormatter.formatCellValue(cell);
            scheduleDetailRequest.setEndPeriod(cellValue);
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(7));
        }

        cell = sheet.getRow(i).getCell(8);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setNote(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setNote(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(8));
        }
        cell = sheet.getRow(i).getCell(9);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setBasis(null);
        } else if (cell.getCellType() == CellType.STRING) {
            scheduleDetailRequest.setBasis(cell.getStringCellValue());
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(9));
        }
        cell = sheet.getRow(i).getCell(10);
        if (isCellEmpty(cell)) {
            scheduleDetailRequest.setStartPeriod(null);
        } else if (cell.getCellType() == CellType.STRING || cell.getCellType() == CellType.NUMERIC) {
            String cellValue = dataFormatter.formatCellValue(cell);
            scheduleDetailRequest.setNumber(cellValue);
        } else {
            inputStream.close();
            throw new BadRequestException(indexCell + i + notFormat(10));
        }

        return scheduleDetailRequest;
    }

    private String notFormat(int i) {
        return " cột thứ " + i + " không đúng định dạng !!!";
    }

    public static boolean isCellEmpty(final Cell cell) {
        if (cell == null) {
            return true;
        } else if (cell.getCellType() == CellType.BLANK) {
            return true;
        } else return cell.getCellType() == CellType.STRING && cell.getStringCellValue().trim().isEmpty();
    }

    // Xóa môn học ra khỏi Schedule
    @Override
    public ResponseEntity<Object> deleteScheduleDetail(String scheduleDetailId) {
        Optional<ScheduleDetail> optionalScheduleDetail = scheduleDetailRepository.findById(scheduleDetailId);
        if (optionalScheduleDetail.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(GenericResponse.builder()
                            .success(false)
                            .message("Không tìm thấy môn học")
                            .statusCode(HttpStatus.NOT_FOUND.value())
                            .build());
        }

        ScheduleDetail scheduleDetail = optionalScheduleDetail.get();
        List<Schedule> schedules = scheduleRepository.findByScheduleDetails(scheduleDetail);
        for (Schedule schedule : schedules) {
            schedule.getScheduleDetails().remove(scheduleDetail);
            scheduleRepository.save(schedule);
        }

        return ResponseEntity.ok(GenericResponse.builder()
                .success(true)
                .message("Xóa môn học thành công")
                .result(scheduleDetail)
                .statusCode(HttpStatus.OK.value())
                .build());
    }

}


