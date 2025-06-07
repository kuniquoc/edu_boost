package quochung.server.controller;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import quochung.server.model.Role;
import quochung.server.payload.MessageDto;
import quochung.server.payload.PasswordUpdateDto;
import quochung.server.payload.UserProfileDto;
import quochung.server.service.UserDetailsServiceImplement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/api")
@Transactional
public class UserController {

    @Autowired
    private UserDetailsServiceImplement userDetailsServiceImplement;

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/role")
    public ResponseEntity<?> getRole() {
        return ResponseEntity.ok(new MessageDto("Lấy role thành công", userDetailsServiceImplement.getRole()));
    }

    public String getMethodName(@RequestParam String param) {
        return new String();
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/user/profile")
    public ResponseEntity<?> getUserProfile() {
        try {
            return ResponseEntity.ok(new MessageDto("Thông tin người dùng được lấy thành công",
                    userDetailsServiceImplement.getUserProfile()));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/profile")
    public ResponseEntity<?> updateUserProfile(@RequestBody UserProfileDto userProfile) {
        try {
            userDetailsServiceImplement.updateUserProfile(userProfile);
            return ResponseEntity.ok(new MessageDto("Cập nhật thông tin người dùng thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('USER')")
    @PutMapping("/user/password")
    public ResponseEntity<?> updateUserPassword(@RequestBody PasswordUpdateDto passwordUpdateDto) {
        try {
            userDetailsServiceImplement.updatePassword(passwordUpdateDto);
            return ResponseEntity.ok(new MessageDto("Cập nhật mật khẩu người dùng thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Mật khẩu cũ không đúng"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('USER')")
    @DeleteMapping("/user")
    public ResponseEntity<?> deleteUser() {
        try {
            userDetailsServiceImplement.deleteUser();
            return ResponseEntity.noContent().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(
                    new MessageDto("Thông tin các người dùng được lấy thành công",
                            userDetailsServiceImplement.getAllUsers()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(new MessageDto("Thông tin người dùng được lấy thành công",
                    userDetailsServiceImplement.getUserById(id)));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/password")
    public ResponseEntity<?> updateUserPassword(@PathVariable Long id,
            @RequestBody PasswordUpdateDto passwordUpdateDto) {
        try {
            userDetailsServiceImplement.updatePassword(id, passwordUpdateDto);
            return ResponseEntity.ok(new MessageDto("Cập nhật mật khẩu người dùng thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userDetailsServiceImplement.deleteUser(id);
            return ResponseEntity.noContent().build();
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("users/roles")
    public ResponseEntity<?> getAllRoles() {
        try {
            return ResponseEntity
                    .ok(new MessageDto("Lấy các quyền tài khoản thành công",
                            userDetailsServiceImplement.getAllRoles()));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(@PathVariable Long id, @RequestBody Set<Role> roles) {
        try {
            userDetailsServiceImplement.updateRole(id, roles);
            return ResponseEntity.ok(new MessageDto("Cập nhật quyền tài khoản thành công"));
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(404).body(new MessageDto("Người dùng không tồn tại"));
        } catch (Exception e) {
            System.out.println("Error: " + e);
            return ResponseEntity.status(500).body(new MessageDto("Lỗi server"));
        }
    }
}
