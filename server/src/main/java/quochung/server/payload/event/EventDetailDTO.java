package quochung.server.payload.event;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class EventDetailDTO {
    private Long id = 0L;

    private String title;

    private LocalDate date;

    private LocalTime startTime;

    private LocalTime endTime;

    private String description;

    private List<TodoItemDTO> todoItems = new ArrayList<>();

    private List<ReminderDTO> reminders = new ArrayList<>();

    private List<StudyMethodDTO> studyMethods = new ArrayList<>();

}
