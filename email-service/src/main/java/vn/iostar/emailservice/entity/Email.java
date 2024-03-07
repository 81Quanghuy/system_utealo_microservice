package vn.iostar.emailservice.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.io.Serializable;
import java.time.LocalDateTime;

@Document(collection = "emails")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class Email implements Serializable {
    @Id
    @Field(name = "email_id")
    private String id;

    private String email;
    private String otp;

    private LocalDateTime expirationTime;
}
