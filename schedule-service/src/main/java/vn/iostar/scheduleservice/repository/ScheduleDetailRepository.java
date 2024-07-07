package vn.iostar.scheduleservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.scheduleservice.entity.ScheduleDetail;

import java.util.List;


@Repository
public interface ScheduleDetailRepository extends MongoRepository<ScheduleDetail, String> {

    // Lấy tất cả thời khóa biểu chi tiết trong hệ thống
    Page<ScheduleDetail> findAll(Pageable pageable);
}
