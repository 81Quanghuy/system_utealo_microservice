package vn.iostar.userservice.entity;

import java.io.Serial;
import java.io.Serializable;


import jakarta.persistence.*;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "profiles")
public class Profile implements Serializable{

	@Serial
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "profile_id")
    private String profileId;
    
    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    @Column(name = "biography",columnDefinition = "nvarchar(255)")
    private String bio;

    @Builder.Default
    @Column(name = "avatar")
    private String avatar = "";

    @Builder.Default
    @Column(name = "background")
    private String background = "";
    
}
