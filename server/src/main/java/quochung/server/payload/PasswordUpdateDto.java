package quochung.server.payload;

import lombok.Data;

@Data
public class PasswordUpdateDto {
    private String oldPassword;
    private String newPassword;
}
