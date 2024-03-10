package vn.iostar.reportservice.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.reportservice.entity.Report;

@Repository
public interface ReportRepository extends MongoRepository<Report, String>{
}
