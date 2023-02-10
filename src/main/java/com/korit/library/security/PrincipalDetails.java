package com.korit.library.security;

import com.korit.library.entity.UserMst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;


@RequiredArgsConstructor
@AllArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    @Getter
    private final UserMst user;
    private Map<String, Object> response;


    // 권한을 리스트로 관리하는 부분
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>(); // 권한들이 리스트에 들어감

//        List<RoleDtlDto> roleDtlDtoList = user.getRoleDtlDto();
//
//        for(int i = 0; i < roleDtlDtoList.size(); i++) {
//            RoleDtlDto dtl = roleDtlDtoList.get(i); // ROLE_USER, ROLE_ADMIN
//            RoleMstDto roleMstDto = dtl.getRoleMstDto();
//            String roleName = roleMstDto.getRoleName();
//
//            GrantedAuthority role = new GrantedAuthority() { // 인터페이스 'GrantedAuthority' 를 익명클래스로 구현
//                @Override
//                public String getAuthority() {
//                    return roleName;
//                }
//            };
////            System.out.println(roleName == role.getAuthority());
//
//            authorities.add(role);
//        }

        user.getRoleDtl().forEach(dtl -> {
            authorities.add(() -> dtl.getRoleMst().getRoleName());
        });

        // user.getRoleDtlDto => 'List' 임 그럼 'forEach' 를 돌릴 수 있는데 안에서 꺼낸 것이 dtl 객체임
        //GrantedAuthority 얘가 메소드가 하나밖에 없어서 람다로 적을 수 있음.
        // dtl.getRoleMstDto().getRoleName()); => 여기가 리턴 부분 즉, 얘가 'roleName' 임

//        user.getRoleDtlDto().forEach(dtl -> {
//            authorities.add(
//                    () -> {
//                        return dtl.getRoleMstDto().getRoleName();
//                    }
//            );
//        });

        return authorities;
    }

    @Override
    public String getPassword() { // 시큐리티가 여길 보고 암호를 복호화 함.
        return user.getPassword(); // 여기 'user' 는 데이터베이스에서 찾은 'username' 임
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }


    /*
        계정 만료 여부
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    /*
        계정 잠김 여부(휴면 => 블랙리스트)
     */
    @Override
    public boolean isAccountNonLocked() {
        return true; // 'false' 되면 잠김
    }

    /*
        비밀번호 만료 여부(비밀번호 입력 횟수 초과)
    */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*
        사용자 활성화 여부(휴면 일정기간 이상 로그인 하지 않을 경우 || 이메일 인증을 거치지 않은 경우)
    */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return response;
    }
}
