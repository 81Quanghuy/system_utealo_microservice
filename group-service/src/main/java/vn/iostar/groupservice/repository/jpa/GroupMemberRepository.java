package vn.iostar.groupservice.repository.jpa;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;
import vn.iostar.constant.GroupMemberRoleType;
import vn.iostar.groupservice.entity.GroupMember;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String>{
    List<GroupMember> findByUserIdAndIsLockedAndRole(String userId, Boolean isLocked, GroupMemberRoleType role);
    List<GroupMember> findByUserIdAndIsLockedAndRoleIn(String userId, Boolean isLocked, List<GroupMemberRoleType> role);
    List<GroupMember> findByUserIdAndIsLocked(String userId, Boolean isLocked);
    Optional<GroupMember> findByUserIdAndGroupId(String userId, String groupId);
    List<GroupMember> findAllByGroupId(String groupId);
    Optional<GroupMember> findByUserIdAndRoleAndGroupId(String userId, GroupMemberRoleType role, String groupId);
    Integer countByGroupId(String groupId);
    List<GroupMember> findAllByGroupIdAndRoleIn(String groupId, List<GroupMemberRoleType> roles);
    Boolean existsByUserIdAndGroupId(String userId, String groupId);
    List<GroupMember> findByUserId(String userId);
    @Query(value="{ 'group.id' : ?0, 'role' : 'Admin' }", fields="{ 'userId' : 1}")
    List<GroupMember> findUserIdAdminInGroup(String groupId);

    void deleteByUserId(String userId);
}
