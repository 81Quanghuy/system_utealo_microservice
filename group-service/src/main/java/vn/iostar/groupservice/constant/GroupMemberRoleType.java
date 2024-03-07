package vn.iostar.groupservice.constant;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum GroupMemberRoleType {
    Member, //Thành viên
    Admin, // Nhóm trưởng
    Deputy, // Nhóm phó
}
