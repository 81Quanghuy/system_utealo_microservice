package vn.iostar.userservice.config.schedules;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import vn.iostar.userservice.entity.Token;
import vn.iostar.userservice.entity.User;
import vn.iostar.userservice.repository.AccountRepository;
import vn.iostar.userservice.repository.ProfileRepository;
import vn.iostar.userservice.repository.TokenRepository;
import vn.iostar.userservice.repository.UserRepository;

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

    @Scheduled(fixedRate = 60) // 86400000 milliseconds = 1 day
    public void deleteUnverifiedUsers() {
        Date oneDayAgo = new Date(System.currentTimeMillis() - 86400000);
        List<User> unverifiedUsers = userRepository.findByIsVerifiedFalseAndCreatedAtBefore(oneDayAgo);
        profileRepository.deleteAllByUserIn(unverifiedUsers);
        accountRepository.deleteAllByUserIn(unverifiedUsers);
        userRepository.deleteAll(unverifiedUsers);
    }
    @Scheduled(fixedRate = 60) // 86400000 milliseconds = 1 day
    public void deleteExpired(){
        Date now = new Date();
        List<Token> tokens = tokenRepository.findAllByExpiredAtBefore(now);
        tokenRepository.deleteAll(tokens);
    }
}
