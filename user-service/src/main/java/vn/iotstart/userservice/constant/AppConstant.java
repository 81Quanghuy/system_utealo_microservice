package vn.iotstart.userservice.constant;

import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.crypto.SecretKey;
import java.io.IOException;

public class AppConstant {
    /**
     * Date format
     */
    public static final String FULL_DATE_TIME_FORMAT = "ddMMyyyyHHmmssSSSSSS";
    public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss:SSSSSS";
    public static final String LOCAL_DATE_TIME_FORMAT_WITHOUT_MILLIS = "dd-MM-yyyy HH:mm:ss";

    @SneakyThrows
    public static SecretKey getSecretKey() {
        ClassPathResource resource = new ClassPathResource("static/secret.key");
        byte[] keyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
