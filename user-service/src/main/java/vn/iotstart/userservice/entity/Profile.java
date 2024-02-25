package vn.iotstart.userservice.entity;

import java.io.Serial;
import java.io.Serializable;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "PROFILES")
public class Profile implements Serializable{

	@Serial
    private static final long serialVersionUID = 1L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int profileId;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId")
    private User user;
    
    @Column(columnDefinition = "nvarchar(255)")
    private String bio;

    private String avatar;
    private String background;
    
}
