package vn.iostar.groupservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.entity.Group;

import java.util.List;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

}
