package vn.iostar.userservice.mapper;

import vn.iostar.model.UserElastic;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.model.UserDocument;

import java.util.ArrayList;
import java.util.List;

public class UserMapper {

    //Mapping giưữa User và UserDocument
    public static UserDocument toUserDocument(User user) {
        UserDocument userDocument = new UserDocument();
        userDocument.setUserId(user.getUserId());
        userDocument.setUserName(user.getUserName());
        if(user.getProfile()!=null){
            if (user.getProfile().getAvatar() != null)
                userDocument.setAvatar(user.getProfile().getAvatar());
            if (user.getProfile().getBackground() != null)
                userDocument.setBackground(user.getProfile().getBackground());
        }
        if (user.getAccount()!= null){
            if (user.getAccount().getEmail() != null) {
                userDocument.setEmail(user.getAccount().getEmail());
            }
            if (user.getAccount().getPhone() != null) {
                userDocument.setPhone(user.getAccount().getPhone());
            }
        }
        userDocument.setRoleName(user.getRole().getRoleName());
        userDocument.setIsActive(user.getIsActive());
        userDocument.setIsOnline(user.getIsOnline());
        userDocument.setIsVerified(user.getIsVerified());
        userDocument.setCreatedAt(user.getCreatedAt());
        userDocument.setUpdatedAt(user.getUpdatedAt());
        return userDocument;
    }
    public static UserElastic toUserElastic(UserDocument user) {
        UserElastic userElastic = new UserElastic();
        userElastic.setUserId(user.getUserId());
        userElastic.setUserName(user.getUserName());
        userElastic.setAvatar(user.getAvatar());
        userElastic.setBackground(user.getBackground());
        userElastic.setEmail(user.getEmail());
        userElastic.setPhone(user.getPhone());
        userElastic.setRoleName(user.getRoleName());
        userElastic.setIsActive(user.getIsActive());
        userElastic.setIsOnline(user.getIsOnline());
        userElastic.setIsVerified(user.getIsVerified());
        userElastic.setCreatedAt(user.getCreatedAt());
        userElastic.setUpdatedAt(user.getUpdatedAt());
        return userElastic;
    }
}
