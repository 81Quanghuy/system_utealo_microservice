package vn.iostar.scheduleservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.scheduleservice.dto.response.ScheduleResponse;
import vn.iostar.scheduleservice.entity.Schedule;
import vn.iostar.scheduleservice.entity.ScheduleDetail;

import java.util.List;

@Repository
public interface ScheduleRepository extends MongoRepository<Schedule, String> {

    List<Schedule> findByUserId(String userId);
    List<Schedule> findByScheduleDetails(ScheduleDetail scheduleDetail);
    Page<Schedule> findAll(Pageable pageable);
    Schedule findByYearAndSemesterAndWeekOfSemester(String year, String semester, String weekOfSemester);
}

