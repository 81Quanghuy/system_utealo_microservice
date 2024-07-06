package vn.iostar.userservice.repository.jpa;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Profile;
import vn.iostar.userservice.entity.User;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
@Repository
public interface ProfileRepository extends JpaRepository<Profile,String> {
    Optional<Profile> findByUser(User user);
    // xóa tất cả profile của user

    @Modifying
    @Transactional
    @Query("delete from Profile p where p.user in :users")
    void deleteAllByUserIn(@Param("users") List<User> users);
}

