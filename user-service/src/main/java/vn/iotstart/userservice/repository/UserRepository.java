package vn.iotstart.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iotstart.userservice.constant.RoleName;
import vn.iotstart.userservice.dto.ListUsers;
import vn.iotstart.userservice.dto.response.UserResponse;
import vn.iotstart.userservice.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findByPhone(String phone);

    Optional<User> findByAccountEmail(String email);

    List<User> findByRoleRoleName(RoleName roleName);

    // Lấy danh sách tất cả user
    @Query("SELECT NEW vn.iotstart.userservice.dto.response.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth) FROM User u")
    Page<UserResponse> findAllUsers(Pageable pageable);

    @Query("SELECT NEW vn.iotstart.userservice.dto.ListUsers (u.userId, u.userName) FROM User u")
    List<ListUsers> findAllUsersIdAndName();

    // Lấy những bài user đăng ký tài khoản trong khoảng thời gian
    @Query("SELECT NEW vn.iotstart.userservice.dto.response.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth,a.isActive) FROM User u JOIN u.account a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<UserResponse> findUsersByAccountCreatedAtBetween(@Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    // Đếm số lượng user trong khoảng thời gian
    long countUsersByAccountCreatedAtBetween(Date startDateAsDate, Date endDateAsDate);

    // Đếm lượng người dùng online
    long countByIsOnlineTrue();

    // Tìm kiếm người dùng theo username, email, phone của người dùng

    @Query("SELECT new vn.iotstart.userservice.dto.response.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth,a.isActive) FROM User u "
            + "JOIN u.account a " + "WHERE u.userName LIKE %:query% " + "OR u.phone LIKE %:query% "
            + "OR a.email LIKE %:query% ")
    List<UserResponse> searchUser(String query);
}

