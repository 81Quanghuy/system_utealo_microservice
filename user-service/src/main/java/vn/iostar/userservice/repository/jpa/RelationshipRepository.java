package vn.iostar.userservice.repository.jpa;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Relationship;
import vn.iostar.userservice.entity.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface RelationshipRepository extends JpaRepository<Relationship,String> {

    Optional<Relationship> findByParentUserId(String id);

    List<Relationship> findByParentAndIsAcceptedTrue(User id);

    Optional<Relationship> findByParentUserIdAndChildUserId(String parentUserId, String childUserId);

    List<Relationship> findByChildUserIdAndIsAcceptedTrue(String id);

    @Query("SELECT r FROM Relationship r WHERE r.parent IN :users")
    void deleteAllByParentIn(@Param("users") List<User> users);

    List<Relationship> findByChildUserIdAndIsAcceptedFalse(String userId);

    // get all relationship
    Page<Relationship> findAllByIsAcceptedFalse(Pageable pageable);
}
