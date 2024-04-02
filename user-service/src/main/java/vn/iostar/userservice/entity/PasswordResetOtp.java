package vn.iostar.userservice.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
public class PasswordResetOtp implements Serializable {

	@Serial
    private static final long serialVersionUID = 1L;

	private static final int EXPIRATION = 5;

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    private String otp;

    @OneToOne( fetch = FetchType.EAGER)
    @JoinColumn(nullable = false, name = "user_id")
    private User user;

    private Date expiryDate;

    public PasswordResetOtp() {
        super();
    }

    public PasswordResetOtp(final String otp) {
        super();

        this.otp = otp;
        this.expiryDate = calculateExpiryDate();
    }

    public PasswordResetOtp(final String otp, final User user) {
        super();

        this.otp = otp;
        this.user = user;
        this.expiryDate = calculateExpiryDate();
    }


    private Date calculateExpiryDate() {
        final Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, PasswordResetOtp.EXPIRATION);
        return new Date(cal.getTime().getTime());
    }

    public void updateOtp(final String otp) {
        this.otp = otp;
        this.expiryDate = calculateExpiryDate();
    }


}
