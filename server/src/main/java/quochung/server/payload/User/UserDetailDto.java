package quochung.server.payload.user;

import java.util.Set;
import java.time.LocalDate;

import lombok.*;

@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class UserDetailDto {
    private Long id;
    private Set<RoleDTO> roles;
    private String fullName;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String gender;
}
