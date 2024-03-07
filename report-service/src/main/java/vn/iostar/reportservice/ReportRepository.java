package vn.iostar.reportservice;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.reportservice.entity.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, String>{
}
