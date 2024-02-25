package vn.iotstart.userservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "email-verification")
public class EmailVerification implements Serializable {
	@Id
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	private String email;
	private String otp;
	private LocalDateTime expirationTime;
}
