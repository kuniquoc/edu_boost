package quochung.server.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;

@Entity
@Table(name = "schedules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    @JsonBackReference
    private User user;

    public LocalDate getStartDate() {
        if (events.isEmpty()) {
            return null; // Hoặc xử lý trường hợp không có event theo yêu cầu
        }
        return events.stream()
                .map(Event::getDate)
                .min(Comparator.naturalOrder())
                .orElse(null); // Hoặc xử lý trường hợp null theo yêu cầu
    }

    public LocalDate getEndDate() {
        if (events.isEmpty()) {
            return null; // Hoặc xử lý trường hợp không có event theo yêu cầu
        }
        return events.stream()
                .map(Event::getDate)
                .max(Comparator.naturalOrder())
                .orElse(null); // Hoặc xử lý trường hợp null theo yêu cầu
    }
}
