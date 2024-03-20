package vn.iostar.groupservice.repository;


import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.dto.SearchPostGroup;
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

    @Query("SELECT  NEW vn.iostar.groupservice.dto.SearchPostGroup (pg.postGroupId,pg.postGroupName,pg.avatarGroup,pg.bio,pg.isPublic) FROM groups pg WHERE pg.postGroupName LIKE %?1%")
    List<SearchPostGroup> findByPostGroupNameContainingIgnoreCase(@Param("search") String search);


}
