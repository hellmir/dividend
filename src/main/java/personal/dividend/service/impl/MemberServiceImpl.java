package personal.dividend.service.impl;

import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import personal.dividend.dto.MemberEntityResponseDto;
import personal.dividend.exception.general.sub.AlreadyExistUserException;
import personal.dividend.exception.significant.sub.NotCorrectPasswordException;
import personal.dividend.persist.repository.MemberRepository;
import personal.dividend.service.MemberService;

import static org.springframework.transaction.annotation.Isolation.READ_COMMITTED;
import static personal.dividend.model.Auth.SignIn;
import static personal.dividend.model.Auth.SignUp;

@Service
@AllArgsConstructor
public class MemberServiceImpl implements MemberService, UserDetailsService {

    private final PasswordEncoder passwordEncoder;
    private final MemberRepository memberRepository;

    private final ModelMapper modelMapper;

    private static final Logger log = LoggerFactory.getLogger(MemberService.class);

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Load user by username: " + username);

        return memberRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user: " + username));
    }

    @Override
    public MemberEntityResponseDto register(SignUp member) {

        log.info("Beginning to register member: " + member.getUsername());

        boolean exists = memberRepository.existsByUsername(member.getUsername());

        if (exists) {
            throw new AlreadyExistUserException("Already exists username: " + member.getUsername());
        }

        member.setPassword(passwordEncoder.encode(member.getPassword()));

        var result = memberRepository.save(member.toEntity());

        log.info("Member registered successfully: " + result.getUsername());

        return modelMapper.map(result, MemberEntityResponseDto.class);

    }

    @Override
    @Transactional(isolation = READ_COMMITTED, readOnly = true, timeout = 10)
    public MemberEntityResponseDto authenticate(SignIn member) {

        log.info("Beginning to authenticate member: " + member.getUsername());

        var user = memberRepository.findByUsername(member.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException("couldn't find user: " + member.getUsername()));

        if (!passwordEncoder.matches(member.getPassword(), user.getPassword())) {
            throw new NotCorrectPasswordException();
        }

        log.info("Member authenticated successfully: " + user.getUsername());

        return modelMapper.map(user, MemberEntityResponseDto.class);

    }

}
