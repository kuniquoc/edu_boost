package quochung.server.payload.User;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String oldPassword;
    private String newPassword;
}
