package vn.iotstart.groupservice.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import vn.iotstart.groupservice.entity.PostGroupRequest;

@Repository
public interface PostGroupRequestRepository extends JpaRepository<PostGroupRequest, String> {

}
