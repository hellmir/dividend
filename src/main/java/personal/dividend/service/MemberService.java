package personal.dividend.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import personal.dividend.dto.MemberEntityResponseDto;

import static personal.dividend.model.Auth.SignIn;
import static personal.dividend.model.Auth.SignUp;

public interface MemberService {

    UserDetails loadUserByUsername(String username) throws UsernameNotFoundException;

    MemberEntityResponseDto register(SignUp member);

    MemberEntityResponseDto authenticate(SignIn member);

}
