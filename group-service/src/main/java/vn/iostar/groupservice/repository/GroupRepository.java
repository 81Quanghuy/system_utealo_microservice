package vn.iostar.groupservice.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.Aggregation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.groupservice.dto.SearchPostGroup;
import vn.iostar.groupservice.dto.response.GroupPostResponse;
import vn.iostar.groupservice.entity.Group;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface GroupRepository extends MongoRepository<Group, String> {

    // find group by user id
    List<Group> findAllByAuthorIdAndIsActive(String authorId, Boolean isActive);

    //find group by name
    Optional<Group> findByPostGroupName(String name);

    List<Group> findAllByIsActive(Boolean isActive);

    @Query("SELECT  NEW vn.iostar.groupservice.dto.SearchPostGroup (pg.id,pg.postGroupName,pg.avatarGroup,pg.bio,pg.isPublic) FROM groups pg WHERE pg.postGroupName = ?1")
    List<SearchPostGroup> findByPostGroupNameContainingIgnoreCase(@Param("search") String search);

    @Query("SELECT NEW vn.iostar.dto.SearchPostGroup(pg.id, pg.postGroupName, pg.avatarGroup, pg.bio, pg.isPublic) FROM PostGroup pg ORDER BY pg.createDate DESC")
    Page<SearchPostGroup> findAllPostGroups(Pageable pageable);

    long countByCreatedAtBetween(Date startDateAsDate, Date endDateAsDate);

    @Query("SELECT NEW vn.iostar.dto.SearchPostGroup(pg.id, pg.postGroupName, pg.avatarGroup, pg.bio, pg.isPublic) FROM PostGroup pg WHERE pg.createDate BETWEEN ?1 AND ?2")
    List<SearchPostGroup> findPostGroupByCreateDateBetween(@Param("startDate") Date startDate, @Param("endDate") Date endDate);





}
