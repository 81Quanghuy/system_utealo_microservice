package vn.iostar.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.iostar.userservice.entity.Role;
import vn.iostar.userservice.constant.RoleName;

import java.util.Optional;
@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Tìm kiếm role theo roleName, trả về kiểu Optional<Role>
    Optional<Role> findByRoleName(RoleName roleName);

}
