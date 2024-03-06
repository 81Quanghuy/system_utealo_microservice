package vn.iostar.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.iostar.userservice.entity.Account;
import vn.iostar.userservice.repository.AccountRepository;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    AccountRepository accountRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        Account account = null;
        if (accountRepository.findByEmailAndIsActiveIsTrue(emailOrPhone).isPresent()) {
            account = accountRepository.findByEmailAndIsActiveIsTrue(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("user is not found"));
        } else {
            account = accountRepository.findByPhoneAndIsActiveIsTrue(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("user is not found"));
        }
        return new UserDetail(account);
    }

    public UserDetails loadUserByUserId(String id) {
        Account account = accountRepository.findByUserUserIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new UsernameNotFoundException("user is not found"));
        return new UserDetail(account);
    }

}
