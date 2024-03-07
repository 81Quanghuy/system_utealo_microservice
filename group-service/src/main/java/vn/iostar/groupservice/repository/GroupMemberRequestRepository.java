package vn.iostar.groupservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.constant.StateType;
import vn.iostar.groupservice.entity.GroupRequest;

import java.util.List;

@Repository
public interface GroupMemberRequestRepository extends MongoRepository<GroupRequest, String> {
    List<GroupRequest> findAllByGroupId(String groupId);
    List<GroupRequest> findAllByGroupIdAndState(String groupId, StateType stateType);
}
