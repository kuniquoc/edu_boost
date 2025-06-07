package quochung.server.payload.schedule;

import java.time.LocalDate;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ScheduleDTO {
    private Long id;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
