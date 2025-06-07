package quochung.server.controller;

import java.util.Set;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import quochung.server.payload.MessageDto;
import quochung.server.payload.user.PasswordUpdateDto;
import quochung.server.payload.user.RoleDTO;
import quochung.server.service.UserDetailsServiceImplement;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserDetailsServiceImplement userDetailsServiceImplement;

    @GetMapping("/users")
    public ResponseEntity<Object> getAllUsers(@RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "6") int size,
            @RequestParam(defaultValue = "0") long roleId,
            @RequestParam(defaultValue = "") String search) {
        String message = "Thông tin các người dùng được lấy thành công";
        Object data = userDetailsServiceImplement.getAllUsers(page, size, roleId, search);
        return ResponseEntity.ok(new MessageDto(message, data));
    }

    @GetMapping("/users/{id}")
    public ResponseEntity<Object> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(new MessageDto("Thông tin người dùng được lấy thành công",
                userDetailsServiceImplement.getUserById(id)));
    }

    @PutMapping("/users/{id}/password")
    public ResponseEntity<Object> updateUserPassword(@PathVariable Long id,
            @RequestBody PasswordUpdateDto passwordUpdateDto) {
        userDetailsServiceImplement.updatePassword(id, passwordUpdateDto);
        return ResponseEntity.ok(new MessageDto("Cập nhật mật khẩu người dùng thành công"));
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable Long id) {
        userDetailsServiceImplement.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("users/roles")
    public ResponseEntity<Object> getAllRoles() {
        return ResponseEntity
                .ok(new MessageDto("Lấy các quyền tài khoản thành công",
                        userDetailsServiceImplement.getAllRoles()));
    }

    @PutMapping("/users/{id}/role")
    public ResponseEntity<Object> updateUserRole(@PathVariable Long id, @RequestBody Set<RoleDTO> roles) {
        userDetailsServiceImplement.updateRole(id, roles);
        return ResponseEntity.ok(new MessageDto("Cập nhật quyền tài khoản thành công"));
    }

    @GetMapping("/users/{userId}/role")
    public ResponseEntity<Object> getUserRole(@PathVariable Long userId) {
        return ResponseEntity.ok(new MessageDto("Lấy role người dùng thành công",
                userDetailsServiceImplement.getUserRole(userId)));
    }
}
