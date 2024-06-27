package vn.iostar.userservice.entity;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "accounts")
public class Account extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
    private String id;

    @Email
    @Column(name = "email")
    private String email;

    @Builder.Default
    @Column(name = "phone")
    private String phone = "";

    @JsonBackReference
    @Column(name = "password")
    private String password;

    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private User user;

    @Builder.Default
    @Column(name = "is_account_active")
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_account_verified")
    private Boolean isVerified = false;

    @Column(name = "created_at")
    private Date createdAt;

    @Column(name = "updated_at")
    private Date updatedAt;

    @Column(name = "last_login_at")
    private Date lastLoginAt;

    @Builder.Default
    @Column(name = "is_account_verified_by_student")
    private Boolean isVerifiedByStudent = true;
}
