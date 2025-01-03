package quochung.server.service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import quochung.server.payload.auth.CodeVerifyDTO;
import quochung.server.payload.auth.EmailVerifyDTO;
import quochung.server.payload.auth.JwtDto;
import quochung.server.payload.auth.SignInDto;
import quochung.server.payload.auth.SignUpDto;
import quochung.server.util.EmailUtil;
import quochung.server.util.JwtUtils;
import quochung.server.util.RandomStringGenerator;
import quochung.server.repository.RoleRepository;
import quochung.server.repository.UserRepository;
import quochung.server.repository.VerificationCodeRepository;
import quochung.server.model.Role;
import quochung.server.model.User;
import quochung.server.model.VerificationCode;

@Service
public class AuthService {

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private JwtUtils jwtUtils;

    @Autowired
    private VerificationCodeRepository verificationCodeRepository;

    public void signUp(SignUpDto signUpRequest) {
        if (userRepository.existsByUsername(signUpRequest.getUsername())) {
            throw new UsernameNotFoundException("Tên người dùng đã tồn tại");
        }

        User user = new User();
        user.setUsername(signUpRequest.getUsername());
        user.setPassword(passwordEncoder.encode(signUpRequest.getPassword()));

        Role userRole = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Không tìm thấy quyền người dùng"));

        user.getRoles().add(userRole);

        userRepository.save(user);
    }

    public JwtDto signIn(SignInDto signInRequest) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(signInRequest.getUsername(),
                        signInRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        return new JwtDto(jwtUtils.generateToken((UserDetailsImplement) authentication.getPrincipal()),
                ((UserDetailsImplement) authentication.getPrincipal()).getAuthorities());
    }

    public boolean emailVerify(EmailVerifyDTO emailVerifyDTO) throws MessagingException {
        // verify code
        // Lấy mã xác minh từ cơ sở dữ liệu
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(emailVerifyDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy mã xác thực"));

        if (!verificationCode.getCode().equals(emailVerifyDTO.getCode())) {
            return false; // Code is incorrect
        }

        if (LocalDateTime.now().isAfter(verificationCode.getExpiresAt())) {
            return false; // Code has expired
        }

        // Xác thực thành công
        // Xóa mã xác minh khỏi cơ sở dữ liệu
        verificationCodeRepository.deleteByEmail(emailVerifyDTO.getEmail());

        // Cập nhật trạng thái xác thực của người dùng
        User user = userRepository.findByEmail(emailVerifyDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        user.setEmailVerified(true);
        userRepository.save(user);

        return true;
    }

    public void codeResend(String email) throws MessagingException {
        if (!userRepository.existsByEmail(email)) {
            throw new UsernameNotFoundException("Không tìm thấy người dùng");
        }
        // send code to email

        // Tạo mã xác minh ngẫu nhiên
        SecureRandom random = new SecureRandom();
        String code = String.valueOf(random.nextInt(900000) + 100000);

        // Tạo đối tượng VerificationCode
        VerificationCode verificationCode = new VerificationCode();
        verificationCode.setEmail(email);
        verificationCode.setCode(code);
        verificationCode.setCreatedAt(LocalDateTime.now());
        verificationCode.setExpiresAt(LocalDateTime.now().plus(10, ChronoUnit.MINUTES)); // Mã sẽ hết hạn sau 10 phút

        // Lưu mã vào cơ sở dữ liệu
        verificationCodeRepository.save(verificationCode);

        // Gửi mã qua email
        emailUtil.sendCodeEmail(email, code);
    }

    public boolean codeVerify(CodeVerifyDTO codeVerifyDTO) throws MessagingException {
        // verify code
        // Lấy mã xác minh từ cơ sở dữ liệu
        VerificationCode verificationCode = verificationCodeRepository.findByEmail(codeVerifyDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy mã xác minh"));

        if (!verificationCode.getCode().equals(codeVerifyDTO.getCode())) {
            return false; // Code is incorrect
        }

        if (LocalDateTime.now().isAfter(verificationCode.getExpiresAt())) {
            return false; // Code has expired
        }

        // Xác thực thành công
        // Xóa mã xác minh khỏi cơ sở dữ liệu
        verificationCodeRepository.deleteByEmail(codeVerifyDTO.getEmail());

        // Cập nhật trạng thái xác thực của người dùng
        User user = userRepository.findByEmail(codeVerifyDTO.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("Không tìm thấy người dùng"));
        String randomPassword = RandomStringGenerator.generateRandomString(8);
        user.setPassword(passwordEncoder.encode(randomPassword));
        userRepository.save(user);

        // Gửi mật khẩu ngẫu nhiên qua email
        emailUtil.sendPasswordEmail(codeVerifyDTO.getEmail(), randomPassword);

        return true;
    }
}
