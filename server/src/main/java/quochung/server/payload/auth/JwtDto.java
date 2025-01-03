package quochung.server.payload.auth;

import java.util.Collection;
import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class JwtDto {
    private String token;
    private Collection<? extends GrantedAuthority> roles;
}
