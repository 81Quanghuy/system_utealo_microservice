package vn.iostar.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Relationship;
import vn.iostar.userservice.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship,String> {

    Optional<Relationship> findByParentUserId(String id);

    List<Relationship> findByParent(User id);

    Optional<Relationship> findByParentUserIdAndChildUserId(String parentUserId, String childUserId);
}
