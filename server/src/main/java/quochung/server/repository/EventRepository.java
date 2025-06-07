package quochung.server.repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import quochung.server.model.Event;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
        @Query("SELECT e FROM Event e WHERE e.schedule.id = :scheduleId AND e.date >= :startDate AND e.date <= :endDate")
        List<Event> findByScheduleIdAndStartDateBetween(@Param("scheduleId") Long scheduleId,
                        @Param("startDate") LocalDate startDate,
                        @Param("endDate") LocalDate endDate);

        void deleteByScheduleId(Long scheduleId);

        @Query("SELECT COUNT(e) > 0 FROM Event e " +
                        "WHERE e.date = :date " +
                        "AND e.schedule.id = :scheduleId " +
                        "AND (:startTime < e.endTime AND :endTime > e.startTime)")
        boolean existsByOverlappingTime(@Param("scheduleId") Long scheduleId,
                        @Param("date") LocalDate date,
                        @Param("startTime") LocalTime startTime,
                        @Param("endTime") LocalTime endTime);
}
