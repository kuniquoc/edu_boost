package quochung.server.service;

import java.util.Collection;
import java.time.LocalDate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import quochung.server.model.User;

public class UserDetailsImplement implements UserDetails {
    private Long id;
    private String username;
    private String password;
    private String fullName;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String gender;
    private Collection<? extends GrantedAuthority> authorities;

    public UserDetailsImplement(User user) {
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.id = user.getId();
        this.fullName = user.getFullName();
        this.birthday = user.getBirthday();
        this.email = user.getEmail();
        this.phone = user.getPhone();
        this.gender = user.getGender();

        // Trực tiếp lấy roles từ userRoles
        try {
            this.authorities = user.getUserRoles().stream()
                    .map(userRole -> new SimpleGrantedAuthority(userRole.getRole().getRoleName()))
                    .toList();
        } catch (Exception e) {
            System.out.println("wtf " + e.getMessage());
        }

    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public LocalDate getBirthday() {
        return birthday;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getGender() {
        return gender;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
