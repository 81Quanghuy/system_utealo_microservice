package vn.iostar.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iostar.userservice.entity.Role;
import vn.iostar.userservice.constant.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {

    // Tìm kiếm role theo roleName, trả về kiểu Optional<Role>
    Optional<Role> findByRoleName(RoleName roleName);

}
