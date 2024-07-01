package vn.iostar.userservice.config.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.iostar.userservice.entity.Token;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.repository.*;
import vn.iostar.userservice.service.client.GroupClient;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Component
@EnableScheduling
@RequiredArgsConstructor
public class ScheduledTaskConfig {
    private final UserRepository userRepository;
    private final AccountRepository accountRepository;
    private final ProfileRepository profileRepository;
    private final TokenRepository tokenRepository;
    private final PasswordResetOtpRepository passwordResetOtpRepository;
    private final RelationshipRepository relationshipRepository;
    private final GroupClient groupClient;

    @Scheduled(fixedRate = 60) // 86400000 milliseconds = 1 day
    public void deleteUnverifiedUsers() {
        Date oneDayAgo = new Date(System.currentTimeMillis() - 86400000);
        List<User> unverifiedUsers = userRepository.findByIsVerifiedFalseAndCreatedAtBefore(oneDayAgo);
        relationshipRepository.deleteAllByParentIn(unverifiedUsers);
        tokenRepository.deleteAllByUserIn(unverifiedUsers);
        passwordResetOtpRepository.deleteAllByUserIn(unverifiedUsers);
        profileRepository.deleteAllByUserIn(unverifiedUsers);
        accountRepository.deleteAllByUserIn(unverifiedUsers);
        userRepository.deleteAll(unverifiedUsers);
        // lấy chuỗi id của user chưa xác thực
        List<String> userIds = unverifiedUsers.stream().map(User::getUserId).toList();
        groupClient.deleteMemberInGroup(userIds);
    }
    @Scheduled(fixedRate = 60) // 86400000 milliseconds = 1 day
    public void deleteExpired(){
        Date now = new Date();
        List<Token> tokens = tokenRepository.findAllByExpiredAtBefore(now);
        tokenRepository.deleteAll(tokens);
    }
    //thêm user vào group
    @Scheduled(fixedRate = 60) // 86400000 milliseconds = 1 day
    public void addUserToGroup(){

    }
}
