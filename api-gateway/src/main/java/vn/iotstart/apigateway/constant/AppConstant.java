package vn.iotstart.apigateway.constant;

import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.crypto.SecretKey;

public class AppConstant {

    public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String ZONED_DATE_TIME_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String INSTANT_FORMAT = "dd-MM-yyyy__HH:mm:ss:SSSSSS";
    public static final String USER_SERVICE_HOST = "http://user-service";

    @SneakyThrows
    public static SecretKey getSecretKey() {
        ClassPathResource resource = new ClassPathResource("static/secret.key");
        byte[] keyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
