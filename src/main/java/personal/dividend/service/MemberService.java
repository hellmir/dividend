package personal.dividend.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import personal.dividend.exception.general.sub.AlreadyExistUserException;
import personal.dividend.exception.general.sub.NotCorrectPasswordException;
import personal.dividend.model.Auth;
import personal.dividend.persist.entity.MemberEntity;
import personal.dividend.persist.repository.MemberRepository;

@Slf4j
@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(Auth.SignUp member) {
        boolean exists = memberRepository.existsByUsername(member.getUsername());
        if (exists) {
            throw new AlreadyExistUserException("Already exists username -> " + member.getUsername());
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));
        var result = memberRepository.save(member.toEntity());

        return result;

    }

    public MemberEntity authenticate(Auth.SignIn member) {

        var user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException
                        ("couldn't find user -> " + member.getUsername()));

        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new NotCorrectPasswordException();
        }

        return user;

    }

}
