package quochung.server.payload;

import java.time.LocalDate;
import java.time.LocalTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import quochung.server.util.LocalDateDeserializer;
import quochung.server.util.LocalTimeDeserializer;

@Getter
@Setter
@AllArgsConstructor
public class CreateEventDTO {
    private String title;
    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonFormat(pattern = "yyyy-MM-dd") // Giữ lại @JsonFormat cho serialization
    private LocalDate date;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(pattern = "HH:mm") // Giữ lại @JsonFormat cho serialization
    private LocalTime startTime;
    @JsonDeserialize(using = LocalTimeDeserializer.class)
    @JsonFormat(pattern = "HH:mm") // Giữ lại @JsonFormat cho serialization
    private LocalTime endTime;
}
