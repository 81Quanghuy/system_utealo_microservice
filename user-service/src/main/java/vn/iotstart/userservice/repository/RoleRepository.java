package vn.iotstart.userservice.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.iotstart.userservice.constant.RoleName;
import vn.iotstart.userservice.entity.Role;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByRoleName(RoleName roleName);
}
