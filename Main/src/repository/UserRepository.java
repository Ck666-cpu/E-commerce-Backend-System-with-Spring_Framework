package repository;

import com.example.ecommerce.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Custom Query 1: Derived Query Method
    // Spring parses the method name "findByEmail" and creates the SQL automatically.
    // Returns Optional because the user might not exist.
    Optional<User> findByEmail(String email);

    // Checks if an email exists (useful during registration)
    boolean existsByEmail(String email);
}