package quochung.server.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quochung.server.model.SubjectType;

@Repository
public interface SubjectTypeRepository extends JpaRepository<SubjectType, Long> {
}
