package com.korit.library.security;


import com.korit.library.entity.UserMst;
import com.korit.library.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PrincipalOAuth2DetailsService extends DefaultOAuth2UserService {

    private final AccountRepository accountRepository;
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User aAuth2User = super.loadUser(userRequest);
        PrincipalDetails principalDetails = null;



        System.out.println("ClientRegistration" + userRequest.getClientRegistration());
        System.out.println("Attributes >>> " + aAuth2User.getAttributes());

        Map<String, Object> attributes = aAuth2User.getAttributes();
        String email = (String) attributes.get("email");
        String username = email.substring(0, email.indexOf("@"));
        String provider = userRequest.getClientRegistration().getClientName();


        UserMst userMst = accountRepository.findUserByUsername(username);

        if(userMst == null){
            String name = (String) attributes.get("name");
            String password = new BCryptPasswordEncoder().encode(UUID.randomUUID().toString());

            userMst = UserMst.builder()
                    .username(username)
                    .password(password)
                    .name(name)
                    .email(email)
                    .provider(provider)
                    .build();

           accountRepository.saveUser(userMst);
           accountRepository.saveRole(userMst);
           userMst = accountRepository.findUserByUsername(username); // 회원가입 끝나고 다시 들고와줘야함(role 관련 포함)

        } else if(userMst.getProvider() == null) {
            userMst.setProvider(provider); // provider: google
            accountRepository.setUserProvider(userMst);
        }

        principalDetails = new PrincipalDetails(userMst, attributes);


        //        return super.loadUser(userRequest);
        return principalDetails;
    }
}
