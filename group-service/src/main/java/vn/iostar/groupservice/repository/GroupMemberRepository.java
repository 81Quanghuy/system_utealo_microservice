package vn.iostar.groupservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.entity.GroupMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String>{
    List<GroupMember> findByUserId(String userId);
    List<GroupMember> findByUserIdAndIsLocked(String userId, Boolean isLocked);
    Optional<GroupMember> findByUserIdAndGroupId(String userId, String groupId);
    List<GroupMember> findAllByGroupId(String groupId);
    Optional<GroupMember> findByUserIdAndRoleAndGroupId(String userId, GroupMemberRoleType role,String groupId);
    Integer countByGroupId(String groupId);
    List<GroupMember> findAllByGroupIdAndRoleIn(String groupId, List<GroupMemberRoleType> roles);
}
