package quochung.server.controller;

import jakarta.mail.MessagingException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;

import quochung.server.payload.*;
import quochung.server.payload.auth.CodeVerifyDTO;
import quochung.server.payload.auth.EmailVerifyDTO;
import quochung.server.payload.auth.SignInDto;
import quochung.server.payload.auth.SignUpDto;
import quochung.server.service.AuthService;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

        private final AuthService authService;

        @PostMapping("/signup")
        public ResponseEntity<Object> signUp(@RequestBody SignUpDto signUpRequest) {
                authService.signUp(signUpRequest);
                return ResponseEntity.status(201).body(new MessageDto("Đăng ký người dùng thành công"));
        }

        @PostMapping("/signin")
        public ResponseEntity<Object> signIn(@RequestBody SignInDto signInRequest) {
                return ResponseEntity.ok(new MessageDto("Đăng nhập thành công", authService.signIn(signInRequest)));
        }

        @PostMapping("/email-verify")
        public ResponseEntity<Object> emailVerify(@RequestBody EmailVerifyDTO emailVerifyDTO)
                        throws MessagingException {
                boolean check = authService.emailVerify(emailVerifyDTO);
                if (!check) {
                        return ResponseEntity.status(404).body(new MessageDto("Mã xác thực không đúng"));
                }
                return ResponseEntity.ok(new MessageDto("Xác thực email thành công"));
        }

        @PostMapping("/code-resend")
        public ResponseEntity<Object> codeResend(@RequestBody String email) throws MessagingException {
                authService.codeResend(email);
                return ResponseEntity.ok(new MessageDto("Mã xác thực đã được gửi đến email của bạn"));
        }

        @PostMapping("/code-verify")
        public ResponseEntity<Object> codeVerify(@RequestBody CodeVerifyDTO codeVerifyDTO) throws MessagingException {
                boolean check = authService.codeVerify(codeVerifyDTO);
                if (!check) {
                        return ResponseEntity.status(404).body(new MessageDto("Mã xác thực không đúng"));
                }
                return ResponseEntity.ok(new MessageDto("Xác thực mã thành công"));
        }
}
