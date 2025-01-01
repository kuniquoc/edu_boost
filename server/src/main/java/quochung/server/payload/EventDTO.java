package quochung.server.payload;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
public class EventDTO {
    private Long id = 0L;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
}