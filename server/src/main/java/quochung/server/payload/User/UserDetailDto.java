package quochung.server.payload.User;

import java.util.Set;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import quochung.server.model.Role;

@Data
@AllArgsConstructor
public class UserDetailDto {
    private Long id;
    private Set<Role> roles;
    private String fullName;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String gender;
}
