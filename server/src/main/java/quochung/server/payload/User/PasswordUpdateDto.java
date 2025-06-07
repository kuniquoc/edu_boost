package quochung.server.payload.user;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class PasswordUpdateDto {
    private String oldPassword;
    private String newPassword;
}
