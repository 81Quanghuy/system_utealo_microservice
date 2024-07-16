package vn.iostar.scheduleservice.constant;

import io.jsonwebtoken.security.Keys;
import lombok.SneakyThrows;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.StreamUtils;

import javax.crypto.SecretKey;

public class AppConstant {
    /**
     * Date format
     */
    public static final String FULL_DATE_TIME_FORMAT = "ddMMyyyyHHmmssSSSSSS";
    public static final String LOCAL_DATE_FORMAT = "dd-MM-yyyy";
    public static final String LOCAL_DATE_TIME_FORMAT = "dd-MM-yyyy HH:mm:ss:SSSSSS";

    public static final String GET_SCHEDULE = "get-schedule";
    public static final String GET_SCHEDULE_OTHER_USER = "get-schedule-other-user";
    public static final String GET_SCHEDULE_ALL = "get-schedule-all";
    @SneakyThrows
    public static SecretKey getSecretKey() {
        ClassPathResource resource = new ClassPathResource("static/secret.key");
        byte[] keyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return Keys.hmacShaKeyFor(keyBytes);
    }


}
