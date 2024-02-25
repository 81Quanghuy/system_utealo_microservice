package vn.iotstart.userservice.entity;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonBackReference;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.*;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "accounts")
public class Account extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "account_id")
    private String id;

    @Email
    @Column(name = "email")
    private String email;

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
    @Column(name = "is_verified")
    private boolean isVerified = false;

    @Builder.Default
    @Column(name = "last_login_at")
    private Date lastLoginAt = null;

    @Builder.Default
    @Column(name = "locked_at")
    private Date lockedAt = null;

    @Builder.Default
    @Column(name = "locked_reason")
    private String lockedReason = "";

    @OneToMany(mappedBy = "account", fetch = FetchType.LAZY)
    @JsonIgnore
    private List<Token> tokens;
}
