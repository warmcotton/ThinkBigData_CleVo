package com.thinkbigdata.clevo.service;

import com.thinkbigdata.clevo.entity.User;
import com.thinkbigdata.clevo.repository.UserRepository;
import com.thinkbigdata.clevo.util.token.CleVoUserDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserDetailServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(() ->
            new UsernameNotFoundException("가입된 이메일 정보가 없습니다.")
        );

        return new CleVoUserDetail(user.getEmail(), user.getRole());
    }
}
