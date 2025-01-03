package quochung.server.payload.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EmailVerifyDTO {
    private String code;
    private String email;
}
