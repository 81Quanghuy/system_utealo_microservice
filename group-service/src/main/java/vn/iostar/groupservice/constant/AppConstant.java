package vn.iostar.groupservice.constant;

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
    public static final String GROUP_NAME_PARENT = "UTE - Phụ huynh";
    public static final String GROUP_NAME_STUDENT = "UTE - Thắc mắc - Trao đổi";
    public static final String GROUP_NAME_TEACHER = "UTE - Giảng viên";
    public static final String GROUP_NAME_STAFF = "UTE - Nhân viên";
    public static final String BACKGROUND_GROUP_LINK = "https://th.bing.com/th/id/R.38fd39e0bf6de9537" +
            "3a438752d76a73d?rik=%2bGpgYyhVTc9Gsg&pid=ImgRaw&r=0";
    public static final String AVATAR_GROUP_STUDENT_LINK =  "https://i1-vnexpress.vnecdn.net/2024/05/" +
            "24/42-1425-1716521154.jpg?w=1020&h=0&q=100&dpr=1&fit=crop&s=5xBaRz3TS1uap5Y-mf-2-g";
    public static final String AVATAR_GROUP_TEACHER_LINK ="https://onca.edu.vn/wp-content/uploads/2023/08/HCMUTE.png";
    public static final String AVATAR_GROUP_STAFF_LINK = "https://th.bing.com/th/id/OIP.ZD4YqFAmo8qn84Tz4HWe8gAAA" +
            "A?rs=1&pid=ImgDetMain";
    public static final String AVATAR_GROUP_PARENT_LINK = "https://th.bing.com/th/id/OIP.YoEMFPl" +
            "8JEaLx548REBaXAHaEN?rs=1&pid=ImgDetMain";
    public static final String REDIS_KEY_GROUP_OWNER = "group_owner";
    public static String POST_GROUP_BY_ID = "post_group_by_id";
    public static String POST_GROUP_JOIN_BY_USER_ID = "post_group_join_by_user_id";

    @SneakyThrows
    public static SecretKey getSecretKey() {
        ClassPathResource resource = new ClassPathResource("static/secret.key");
        byte[] keyBytes = StreamUtils.copyToByteArray(resource.getInputStream());
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
