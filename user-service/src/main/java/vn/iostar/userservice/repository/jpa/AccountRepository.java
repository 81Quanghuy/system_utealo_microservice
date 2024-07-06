package vn.iostar.userservice.repository.jpa;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import vn.iostar.userservice.entity.Account;
import vn.iostar.userservice.entity.User;

@Repository
public interface AccountRepository extends JpaRepository<Account, String>{

    // Tìm kiếm tài khoản theo email
    Optional<Account> findByEmail(String email);

    // Tìm kiếm tài khoản theo số điện thoại
    Optional<Account> findByPhone(String phone);

    // Tìm kiếm tài khoản theo userId
    Optional<Account> findByUserUserId(String userId);

    // Tìm kiếm tài khoản theo email và trạng thái kích hoạt
    Optional<Account> findByEmailAndIsActiveIsTrue(String email);

    // Tìm kiếm tài khoản theo userId và trạng thái kích hoạt
    Optional<Account> findByUserUserIdAndIsActiveIsTrue(String userId);

    // Tìm kiếm tài khoản theo số điện thoại và trạng thái kích hoạt
    Optional<Account> findByPhoneAndIsActiveIsTrue(String emailOrPhone);

    @Modifying
    @Transactional
    @Query("delete from Account a where a.user in :users")
    void deleteAllByUserIn(@Param("users") List<User> users);
}
