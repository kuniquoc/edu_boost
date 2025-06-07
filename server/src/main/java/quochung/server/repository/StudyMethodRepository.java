package quochung.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quochung.server.model.StudyMethod;
import quochung.server.model.SubjectType;

import java.util.List;

@Repository
public interface StudyMethodRepository extends JpaRepository<StudyMethod, Long> {
    Page<StudyMethod> findByIdIn(List<Long> ids, Pageable pageable);

    Page<StudyMethod> findByType(SubjectType type, Pageable pageable);

    Page<StudyMethod> findByTypeAndIdIn(SubjectType type, List<Long> ids, Pageable pageable);

    Page<StudyMethod> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<StudyMethod> findByNameContainingAndTypeIgnoreCase(String name, SubjectType type, Pageable pageable);

    Page<StudyMethod> findByNameContainingAndIdInIgnoreCase(String name, List<Long> ids, Pageable pageable);

    Page<StudyMethod> findByNameContainingAndTypeAndIdInIgnoreCase(String name, SubjectType type, List<Long> ids,
            Pageable pageable);
}
