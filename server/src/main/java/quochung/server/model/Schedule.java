package quochung.server.model;

import jakarta.persistence.*;
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
public class Schedule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user; // A schedule belongs to a user
    @OneToMany(mappedBy = "schedule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events = new ArrayList<>();

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
