package vn.iostar.groupservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.constant.GroupMemberRoleType;
import vn.iostar.groupservice.entity.GroupMember;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface GroupMemberRepository extends MongoRepository<GroupMember, String> {
    List<GroupMember> findAllByUserId(String userId);
    Optional<GroupMember> findByUserIdAndGroupId(String userId, String groupId);
    List<GroupMember> findAllByGroupId(String groupId);
    Page<GroupMember> findAllByGroupId(String groupId, Pageable pageable);

    //find Group By UserId and Role
    Optional<GroupMember> findByUserIdAndRoleAndGroupId(String userId, GroupMemberRoleType role,String groupId);

    // count member by groupId
    Integer countByGroupId(String groupId);

    void deleteAllByGroupId(String postGroupId);
    List<GroupMember> findAllByGroupIdAndRoleIn(String groupId, List<GroupMemberRoleType> roles);
}
