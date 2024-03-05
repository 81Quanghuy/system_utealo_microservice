package vn.iotstart.userservice.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import vn.iotstart.userservice.entity.Account;
import vn.iotstart.userservice.repository.AccountRepository;

@Service
public class UserDetailService implements UserDetailsService {

    @Autowired
    AccountRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        Account user = null;
        if (userRepository.findByEmailAndIsActiveIsTrue(emailOrPhone).isPresent()) {
            user = userRepository.findByEmailAndIsActiveIsTrue(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("user is not found"));
        } else {
            user = userRepository.findByPhoneAndIsActiveIsTrue(emailOrPhone)
                    .orElseThrow(() -> new UsernameNotFoundException("user is not found"));
        }
        return new UserDetail(user);
    }

    public UserDetails loadUserByUserId(String id) {
        Account user = userRepository.findByUserUserIdAndIsActiveIsTrue(id)
                .orElseThrow(() -> new UsernameNotFoundException("user is not found"));
        return new UserDetail(user);
    }

}
