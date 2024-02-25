package vn.iotstart.userservice.util;

import java.util.Map;

public class RoleUtil {
    public static String getRoleName(String role) {
        Map<String, String> roleMap = Map.of(
                "ADMIN", "Quản trị viên",
                "STUDENT", "Học sinh",
                "TEACHER", "Giáo viên",
                "PARENT", "Phụ huynh"
        );
        return roleMap.get(role);
    }
}
