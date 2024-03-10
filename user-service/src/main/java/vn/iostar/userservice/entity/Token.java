package vn.iostar.userservice.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import lombok.*;
import vn.iostar.userservice.constant.TokenType;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "tokens")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class Token extends AbstractMappedEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "token_id")
    private String id;

    @Column(name = "token_value", unique = true,length = 700)
    private String token;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type")
    private TokenType type;

    @Builder.Default
    @Column(name = "is_expired")
    private Boolean isExpired = false;

    @Builder.Default
    @Column(name = "is_revoked")
    private Boolean isRevoked = false;

    @Column(name = "expired_at")
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Date expiredAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;
}
