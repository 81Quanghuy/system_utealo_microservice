package vn.iotstart.userservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

import lombok.*;
import org.hibernate.annotations.Nationalized;
import vn.iotstart.userservice.constant.Gender;
import vn.iotstart.userservice.constant.RoleName;

import java.io.Serializable;
import java.util.*;

@Entity
@Table(name = "users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class User extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private String id;

    @Column( name = "user_name",columnDefinition = "nvarchar(255)")

    private String userName;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private RoleName role;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender")
    private Gender gender;

    @Builder.Default
    @Column(name = "phone")
    private String phone = "";

    @Column(name = "dob")
    private Date dob;

    private Boolean isOnline = false;

    /**
     * For Student
     */
    @OneToMany
    @JoinColumn(name = "child_id")
    @JsonBackReference
    @ToString.Exclude
    private List<Relationship> relationships = new ArrayList<>();

    @Builder.Default
    @ManyToMany(mappedBy = "students")
    @JsonBackReference
    @ToString.Exclude
    private List<User> parents = new ArrayList<>();

    /**
     * For Parent
     */
    @OneToMany
    @JoinColumn(name = "parent_id")
    @JsonBackReference
    @ToString.Exclude
    private List<Relationship> children = new ArrayList<>();

    @Builder.Default
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "parent_student",
            joinColumns = @JoinColumn(name = "parent_id"),
            inverseJoinColumns = @JoinColumn(name = "student_id")
    )
    @JsonBackReference
    @ToString.Exclude
    private List<User> students = new ArrayList<>();

}
