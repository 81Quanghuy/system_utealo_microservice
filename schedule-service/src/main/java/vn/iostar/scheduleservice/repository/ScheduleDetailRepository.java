package vn.iostar.scheduleservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.scheduleservice.entity.ScheduleDetail;


@Repository
public interface ScheduleDetailRepository extends MongoRepository<ScheduleDetail, String> {


}
