package vn.iostar.groupservice.repository.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.entity.Event;
import vn.iostar.groupservice.entity.Group;

import java.util.List;

@Repository
public interface EventRepository extends MongoRepository<Event, String> {
    List<Event> findAllByGroupId(String groupId);

    List<Event> findAllByGroupInOrderByCreatedAtDesc(List<Group> groups);
}
