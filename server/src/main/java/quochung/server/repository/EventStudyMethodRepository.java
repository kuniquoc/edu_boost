package quochung.server.repository;

import java.util.List;
import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import quochung.server.model.EventStudyMethod;

@Repository
public interface EventStudyMethodRepository extends JpaRepository<EventStudyMethod, Long> {
    List<EventStudyMethod> findByEventId(Long eventId);

    List<EventStudyMethod> findByStudyMethodId(Long studyMethodId);

    void deleteByEventId(Long eventId);

    void deleteByStudyMethodId(Long studyMethodId);
}
