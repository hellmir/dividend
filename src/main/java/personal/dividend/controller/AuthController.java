package personal.dividend.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import personal.dividend.sercurity.TokenProvider;
import personal.dividend.service.MemberService;

import static personal.dividend.model.Auth.SignIn;
import static personal.dividend.model.Auth.SignUp;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final MemberService memberService;
    private final TokenProvider tokenProvider;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignUp request) {

        var result = memberService.register(request);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody SignIn request) {

        var member = memberService.authenticate(request);
        var token = tokenProvider.generateToken(member.getUsername(), member.getRoles());

        return ResponseEntity.ok(token);

    }

}
