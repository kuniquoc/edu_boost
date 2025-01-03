package quochung.server.payload.auth;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CodeVerifyDTO {
    private String email;
    private String code;
}
