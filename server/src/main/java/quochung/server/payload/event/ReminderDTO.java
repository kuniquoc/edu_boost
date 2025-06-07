package quochung.server.payload.event;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReminderDTO {
    private Long id;

    private LocalDateTime scheduledTime;
}
