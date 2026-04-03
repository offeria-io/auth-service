package offeria.auth_service.repository;

import offeria.auth_service.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for User entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find user by username.
     * @param username user's username
     * @return Optional of user
     */
    Optional<User> findByUsername(String username);

    /**
     * Check if user exists by username.
     * @param username user's username
     * @return true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Check if user exists by email.
     * @param email user's email
     * @return true if exists
     */
    boolean existsByEmail(String email);
}
