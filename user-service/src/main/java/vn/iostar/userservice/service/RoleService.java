package vn.iostar.userservice.service;

import vn.iostar.constant.RoleName;
import vn.iostar.userservice.entity.Role;

import java.util.Optional;

public interface RoleService {
    <S extends Role> S save(S entity);

    Optional<Role> findByRoleName(RoleName roleName);
}
