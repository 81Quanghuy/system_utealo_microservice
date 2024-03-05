package vn.iotstart.groupservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iotstart.groupservice.entity.PostGroup;

@Repository
public interface PostGroupRepository extends JpaRepository<PostGroup, Integer> {
}
