package com.korit.library.security;

import com.korit.library.aop.annotation.ParamsAspect;
import com.korit.library.repository.AccountRepository;
import com.korit.library.web.dto.UserDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;


    @ParamsAspect
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserDto user = accountRepository.findUserByUsername(username); // 데이터베이스에서 username 찾아옴

        if(user == null) {
            throw new UsernameNotFoundException("회원정보를 확인 할 수 없음");
        }

//        log.info("로그인 시도 요청 들어옴?");

        return new PrincipalDetails(user);
    }
}
