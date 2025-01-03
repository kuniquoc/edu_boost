package quochung.server.payload.User;

import lombok.Data;
import java.time.LocalDate;

@Data
public class UserProfileDto {
    private String fullName;
    private LocalDate birthday;
    private String email;
    private String phone;
    private String gender;
    private boolean emailVerified;

    public UserProfileDto(String fullName, LocalDate birthday, String email, String phone, String gender,
            boolean emailVerified) {
        if (birthday.getYear() < 1900) {
            throw new IllegalArgumentException("Birthday must be after 1900");
        } else if (birthday.getMonthValue() < 1 || birthday.getMonthValue() > 12) {
            throw new IllegalArgumentException("Month must be between 1 and 12");
        } else if (birthday.getDayOfMonth() < 1 || birthday.getDayOfMonth() > 31) {
            throw new IllegalArgumentException("Day must be between 1 and 31");
        }

        this.fullName = fullName;
        this.birthday = birthday;
        this.email = email;
        this.phone = phone;
        this.gender = gender;
        this.emailVerified = emailVerified;
    }
}
