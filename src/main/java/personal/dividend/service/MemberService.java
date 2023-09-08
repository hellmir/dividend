package personal.dividend.service;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import personal.dividend.exception.general.sub.AlreadyExistUserException;
import personal.dividend.exception.significant.sub.NotCorrectPasswordException;
import personal.dividend.persist.entity.MemberEntity;
import personal.dividend.persist.repository.MemberRepository;

import static personal.dividend.model.Auth.SignIn;
import static personal.dividend.model.Auth.SignUp;

@Service
@AllArgsConstructor
public class MemberService implements UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("load user -> " + username);

        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + username));
    }

    public MemberEntity register(SignUp member) {

        log.info("register user -> " + member.getUsername());

        boolean exists = memberRepository.existsByUsername(member.getUsername());

        if (exists) {
            throw new AlreadyExistUserException("Already exists username -> " + member.getUsername());
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        var result = memberRepository.save(member.toEntity());

        return result;

    }

    public MemberEntity authenticate(SignIn member) {

        log.info("authenticate user -> " + member.getUsername());

        var user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user -> " + member.getUsername()));
        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new NotCorrectPasswordException();
        }

        return user;

    }

}
