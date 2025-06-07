package quochung.server.payload.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignInDto {
    private String username;
    private String password;
}
