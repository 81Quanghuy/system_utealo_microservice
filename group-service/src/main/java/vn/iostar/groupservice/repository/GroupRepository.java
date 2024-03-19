package vn.iostar.groupservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.entity.Group;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    // find group by user id
    List<Group> findAllByAuthorIdAndIsActive(String authorId, Boolean isActive);

    //find group by name
    Optional<Group> findByPostGroupName(String name);

    List<Group> findAllByIsActive(Boolean isActive);
}
