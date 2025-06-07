package quochung.server.payload.auth;

import java.util.Collection;

import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtDto {
    private String token;
    private Collection<? extends GrantedAuthority> roles;
}
