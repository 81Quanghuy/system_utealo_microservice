package vn.iostar.userservice.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.iostar.constant.RoleName;
import vn.iostar.userservice.dto.ListUsers;
import vn.iostar.userservice.dto.SearchUser;
import vn.iostar.userservice.dto.response.UserResponse;
import vn.iostar.userservice.entity.User;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, String> {

    // Tìm kiếm người dùng theo số điện thoại
    Optional<User> findByPhone(String phone);

    // Tìm kiếm người dùng theo email
    Optional<User> findByAccountEmail(String email);

    List<User> findAllByUserId(String userId);

    // Tìm kiếm người dùng theo vai trò
    List<User> findByRoleRoleName(RoleName roleName);

    // Lấy danh sách tất cả nguời dùng
    @Query("SELECT NEW vn.iostar.userservice.dto.response.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth) FROM User u")
    Page<UserResponse> findAllUsers(Pageable pageable);

    // Lấy mã người dùng và tên người dùng
    @Query("SELECT NEW vn.iostar.userservice.dto.ListUsers (u.userId, u.userName) FROM User u")
    List<ListUsers> findAllUsersIdAndName();

    // Lấy những bài người dùng đăng ký tài khoản trong khoảng thời gian
    @Query("SELECT NEW vn.iostar.userservice.dto.response.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth,a.isActive) FROM User u JOIN u.account a WHERE a.createdAt BETWEEN :startDate AND :endDate")
    List<UserResponse> findUsersByAccountCreatedAtBetween(@Param("startDate") Date startDate,
                                                          @Param("endDate") Date endDate);

    // Đếm số lượng người dùng trong khoảng thời gian
    long countUsersByAccountCreatedAtBetween(Date startDateAsDate, Date endDateAsDate);

    // Đếm lượng người dùng online
    long countByIsOnlineTrue();

    // Tìm kiếm người dùng theo tên người dùng, số điện thoại, email
    @Query("SELECT new vn.iostar.userservice.dto.response.UserResponse(u.userId, u.userName, u.address,u.phone, u.gender, u.dayOfBirth,a.isActive) FROM User u "
            + "JOIN u.account a " + "WHERE u.userName LIKE %:query% " + "OR u.phone LIKE %:query% "
            + "OR a.email LIKE %:query% ")
    List<UserResponse> searchUser(String query);

    @Query("SELECT NEW vn.iostar.userservice.dto.SearchUser(u.userId, u.userName) " + "FROM User u "
            + "WHERE u.userName LIKE %:search%")
    List<SearchUser> findUsersByName(@Param("search") String search);

    // lấy tất cả userId
    @Query("SELECT u.userId FROM User u where u.userId is not null and u.isActive = true and u.isOnline = true")
    List<String> findAllUserId();

    List<User> findByIsVerifiedFalseAndCreatedAtBefore(Date date);
}

