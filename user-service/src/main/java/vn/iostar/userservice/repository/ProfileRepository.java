package vn.iostar.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Profile;


@Repository
public interface ProfileRepository extends JpaRepository<Profile,String> {

}

