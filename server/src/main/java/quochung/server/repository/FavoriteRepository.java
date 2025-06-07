package quochung.server.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quochung.server.model.Favorite;

@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    List<Favorite> findByUserId(Long userId);

    Optional<Favorite> findByUserIdAndStudyMethodId(Long userId, Long studyMethodId);

    void deleteByUserIdAndStudyMethodId(Long userId, Long studyMethodId);

    boolean existsByUserIdAndStudyMethodId(Long userId, Long studyMethodId);

    void deleteByUserId(Long userId);

    void deleteByStudyMethodId(Long studyMethodId);
}
