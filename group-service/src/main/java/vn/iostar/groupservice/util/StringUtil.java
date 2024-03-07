package vn.iostar.groupservice.util;

public class StringUtil {
    public static String convertToRegex(String query) {
        if (query == null) {
            return "";
        }
        // Chuyển đổi từ khóa tìm kiếm thành biểu thức chính quy MongoDB
        // Loại bỏ dấu tiếng Việt và chuyển đổi tất cả thành chữ thường
        String[] words = query.replaceAll("\\s+", " ").toLowerCase().split(" ");
        StringBuilder regexBuilder = new StringBuilder();

        for (String word : words) {
            regexBuilder.append(".*?");
            for (char c : word.toCharArray()) {
                regexBuilder.append(c);
                regexBuilder.append(".*?");
            }
        }

        return regexBuilder.toString();
    }

}
