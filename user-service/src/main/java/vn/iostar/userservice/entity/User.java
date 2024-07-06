package vn.iostar.userservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;
import vn.iostar.constant.Gender;
import vn.iostar.userservice.config.entityListener.UserEntityListener;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
//@EntityListeners(UserEntityListener.class)
public class User extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String userId;

    @Column(name = "user_name", columnDefinition = "nvarchar(255)")
    private String userName;

    @Column(name = "address", columnDefinition = "nvarchar(255)")
    private String address;

    @Builder.Default
    @Column(name = "phone")
    private String phone = "";

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Column(name = "day_of_birth")
    private Date dayOfBirth;

    @Column(name = "is_user_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_user_online")
    @Builder.Default
    private Boolean isOnline = false;

    @Column(name = "is_user_vertified")
    @Builder.Default
    private Boolean isVerified = false;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @OneToOne(mappedBy = "user")
    private Account account;

    @OneToOne(mappedBy = "user")
    private Profile profile;

    @OneToMany(mappedBy = "user")
    private List<Token> tokens;

    @OneToMany(mappedBy = "child", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonBackReference
    private List<Relationship> child;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    @JsonBackReference
    private List<Relationship> parent;
}
