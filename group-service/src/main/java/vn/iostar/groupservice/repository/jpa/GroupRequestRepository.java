package vn.iostar.groupservice.repository.jpa;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.entity.GroupRequest;

import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRequestRepository extends MongoRepository<GroupRequest, String> {
    List<GroupRequest> findAllByGroupId(String groupId);
    Optional<GroupRequest> findByIdAndIsAccept(String id, Boolean isAccept);

    //Tim kiem tat ca nhom ma user duoc moi
    List<GroupRequest> findAllByInvitedUser(String invitedUser);

    //Tim kiem tat ca nhom ma user da moi
    List<GroupRequest> findAllByInvitingUser(String invitingUser);

    // Lấy request theo group id và invited user
    Optional<GroupRequest> findByGroupIdAndInvitedUserAndIsAccept(String groupId, String invitedUser, Boolean isAccept);
    Optional<GroupRequest> findByGroupIdAndInvitedUser(String groupId, String invitedUser);

    // Lay tat ca yeu cau tham gia nhom theo groupid va IsAccept
    List<GroupRequest> findAllByGroupIdAndIsAccept(String groupId, Boolean isAccept);

    //Lấy yêu cầu tham gia nhóm của chính mình
    Optional<GroupRequest> findByGroupIdAndInvitedUserAndInvitingUser(String groupId, String invitedUser, String invitingUser);

    // Lay yeu cau tham gia nhom co user duoc moi va khong co user moi
    List<GroupRequest> findByInvitedUserNotAndInvitingUser(String invitedUser, String invitingUser);
}
