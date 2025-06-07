package quochung.server.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import lombok.RequiredArgsConstructor;
import quochung.server.payload.MessageDto;
import quochung.server.payload.user.PasswordUpdateDto;
import quochung.server.payload.user.UserProfileDto;
import quochung.server.service.UserDetailsServiceImplement;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController {

    private final UserDetailsServiceImplement userDetailsServiceImplement;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/role")
    public ResponseEntity<Object> getRole() {
        return ResponseEntity.ok(new MessageDto("Lấy role thành công", userDetailsServiceImplement.getRole()));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/profile")
    public ResponseEntity<Object> getUserProfile() throws UsernameNotFoundException {
            return ResponseEntity.ok(new MessageDto("Thông tin người dùng được lấy thành công",
                    userDetailsServiceImplement.getUserProfile()));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/profile")
    public ResponseEntity<Object> updateUserProfile(@RequestBody UserProfileDto userProfile) throws UsernameNotFoundException{
            userDetailsServiceImplement.updateUserProfile(userProfile);
            return ResponseEntity.ok(new MessageDto("Cập nhật thông tin người dùng thành công"));
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/password")
    public ResponseEntity<Object> updateUserPassword(@RequestBody PasswordUpdateDto passwordUpdateDto) throws UsernameNotFoundException {
            userDetailsServiceImplement.updatePassword(passwordUpdateDto);
            return ResponseEntity.ok(new MessageDto("Cập nhật mật khẩu người dùng thành công"));
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/user")
    public ResponseEntity<Object> deleteUser() throws  UsernameNotFoundException {
            userDetailsServiceImplement.deleteUser();
            return ResponseEntity.noContent().build();
    }

}
