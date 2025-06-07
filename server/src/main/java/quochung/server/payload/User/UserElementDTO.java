package quochung.server.payload.user;

import java.time.LocalDate;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserElementDTO {

    private Long id;

    private String fullName;

    private LocalDate birthday;

    private String email;

    private String phone;

    private String gender;

    private Set<RoleDTO> roles;
}
