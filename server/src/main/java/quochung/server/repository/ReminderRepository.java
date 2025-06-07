package quochung.server.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quochung.server.model.Reminder;

@Repository
public interface ReminderRepository extends JpaRepository<Reminder, Long> {
    Reminder findByEventId(Long eventId);

    void deleteByEventId(Long eventId);

    List<Reminder> findByScheduledTimeBefore(LocalDateTime time);
}
