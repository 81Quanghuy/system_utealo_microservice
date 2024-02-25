package com.trvankiet.app.repository;

import com.trvankiet.app.constant.Gender;
import com.trvankiet.app.constant.RoleBasedAuthority;
import com.trvankiet.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepositorys extends JpaRepository<User, String> {

    @Query("SELECT u FROM User u " +
            "WHERE (:query IS NULL OR CONCAT(u.lastName, ' ', u.firstName, ' ', u.phone, ' ', u.email) LIKE %:query%) " +
            "AND (:role IS NULL OR u.role = :role) " +
            "AND (:gender IS NULL OR u.gender = :gender) " +
            "AND (:school IS NULL OR u.school = :school) " +
            "AND (:grade IS NULL OR u.grade = :grade) " +
            "AND (:subjects IS NULL OR EXISTS (SELECT 1 FROM u.subjects s WHERE s IN :subjects))")
    List<User> searchUsers(@Param("query") String query,
                           @Param("role") RoleBasedAuthority role,
                           @Param("gender") Gender gender,
                           @Param("school") String school,
                           @Param("grade") Integer grade,
                           @Param("subjects") List<String> subjects);


}
