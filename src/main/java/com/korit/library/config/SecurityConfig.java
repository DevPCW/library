package com.korit.library.config;

import com.korit.library.security.PrincipalOAuth2DetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@EnableWebSecurity // 밑에 새로 오버라이딩 하는 애들을 쓰겠다.
@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final PrincipalOAuth2DetailsService principalOAuth2DetailsService;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() { // Bean 객체 생성 해서 강제로 IoC에 등록
        return new BCryptPasswordEncoder();
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring()
                .requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.httpBasic().disable();
        http.authorizeRequests()
                .antMatchers("/mypage/**", "/security/**")
                .authenticated() // 요청주소에 'mypage' 와 그 뒤에 뭐든 들어오면 인증을 거쳐라
//                .antMatchers("")
//                .hasRole("")
                .anyRequest()
                .permitAll()
                .and()
                .formLogin()
                .loginPage("/account/login") // 로그인 페이지 get 요청
                .loginProcessingUrl("/account/login") // 로그인 인증 post 요청 // 얘를 쓰려면 두가지 객체가 필요함(security 폴더 생성)
//                .successForwardUrl("/mypage") // 로그인 성공하면 무조건 'mypage' 로 이동시킴(이전 요청 다 무시함)
                .failureForwardUrl("/account/login/error") // 로그인 실패 했을 때 무조건 이쪽으로 가라
//                .failureHandler() // 타임리프 써야 함

                // 'security' 에 걸려서 로그인페이지로 이동후 로그인 하면 원래 가려고 했던 곳으로 로그인 성공 시 이동됨
                .and()
                .oauth2Login()
                .userInfoEndpoint()
                .userService(principalOAuth2DetailsService)
                .and()
                .defaultSuccessUrl("/index"); // 우리가 직접 로그인페이지로 들어왔을 때
        
    }
}
