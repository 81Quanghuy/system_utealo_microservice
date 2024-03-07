package vn.iostar.userservice.entity;

import jakarta.persistence.*;
import lombok.*;
import vn.iostar.userservice.constant.RoleName;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "roles")
public class Role extends AbstractMappedEntity implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "role_id")
    private String id;

    @Column(name = "role_code", unique = true)
    private String code;

    @Enumerated(EnumType.STRING)
    @Column(name = "roleName")
    private RoleName roleName;

    @Builder.Default
    @Column(name = "role_description")
    private String description = "";

    @OneToMany(mappedBy = "role",cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users;

}
