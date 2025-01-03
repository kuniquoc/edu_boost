package quochung.server.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import quochung.server.payload.*;
import quochung.server.payload.auth.CodeVerifyDTO;
import quochung.server.payload.auth.EmailVerifyDTO;
import quochung.server.payload.auth.SignInDto;
import quochung.server.payload.auth.SignUpDto;
import quochung.server.payload.auth.JwtDto;
import quochung.server.service.AuthService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@Transactional
public class AuthController {
    @Autowired
    AuthService authService;

    @PostMapping("/signup")
    public ResponseEntity<?> signUp(@RequestBody @Validated SignUpDto signUpRequest) {
        try {
            authService.signUp(signUpRequest);
            return ResponseEntity.status(201).body(new MessageDto("Đăng ký người dùng thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(409).body(new MessageDto("Tên người dùng đã tồn tại"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signIn(@RequestBody SignInDto signInRequest) {
        try {
            return ResponseEntity.ok(new MessageDto("Đăng nhập thành công", authService.signIn(signInRequest)));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(404).body(new MessageDto("Không tìm thấy người dùng"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PostMapping("/email-verify")
    public ResponseEntity<?> emailVerify(@RequestBody EmailVerifyDTO emailVerifyDTO) {
        try {
            boolean check = authService.emailVerify(emailVerifyDTO);
            if (!check) {
                return ResponseEntity.status(404).body(new MessageDto("Mã xác thực không đúng"));
            }
            return ResponseEntity.ok(new MessageDto("Xác thực email thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Mã xác thực không đúng"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PostMapping("/code-resend")
    public ResponseEntity<?> codeResend(@RequestBody String email) {
        try {
            authService.codeResend(email);
            return ResponseEntity.ok(new MessageDto("Mã xác thực đã được gửi đến email của bạn"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Không tìm thấy người dùng"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PostMapping("/code-verify")
    public ResponseEntity<?> codeVerify(@RequestBody CodeVerifyDTO codeVerifyDTO) {
        try {
            boolean check = authService.codeVerify(codeVerifyDTO);
            if (!check) {
                return ResponseEntity.status(404).body(new MessageDto("Mã xác thực không đúng"));
            }
            return ResponseEntity.ok(new MessageDto("Xác thực mã thành công"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }
}
