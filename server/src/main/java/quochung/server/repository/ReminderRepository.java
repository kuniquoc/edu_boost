package quochung.server.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import quochung.server.model.Reminder;

public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Reminder findByEventId(Long eventId);

    void deleteByEventId(Long eventId);

    List<Reminder> findByScheduledTimeBefore(LocalDateTime time);
}
