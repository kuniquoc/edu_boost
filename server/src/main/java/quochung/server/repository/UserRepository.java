package quochung.server.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import quochung.server.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByEmailAndEmailVerified(String email, boolean emailVerified);

    Optional<User> findByEmail(String email);

    Page<User> findByUserRoles_Role_IdAndFullNameContainingIgnoreCase(Long roleId, String fullName, Pageable pageable);

    Page<User> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);

    Page<User> findByUserRoles_Role_Id(Long roleId, Pageable pageable);
}
