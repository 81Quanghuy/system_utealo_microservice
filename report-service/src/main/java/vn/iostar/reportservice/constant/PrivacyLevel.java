package vn.iostar.reportservice.constant;

public enum PrivacyLevel {
    PUBLIC, // Bài viết công khai
    FRIENDS, // Chỉ bạn bè có thể thấy
    GROUP_MEMBERS, // Chỉ thành viên trong nhóm có thể thấy
    PRIVATE, // Bài viết riêng tư
    ADMIN, // Những bài đăng của admin để tất cả mọi người thấy
    CONTRIBUTE,// Những bài đóng góp của người dùng
    BUG, // Những lỗi mà người dùng phát hiện
    REPORT
}
