package vn.iostar.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iostar.userservice.entity.Role;
import vn.iotstart.userservice.constant.RoleName;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(RoleName roleName);
}
