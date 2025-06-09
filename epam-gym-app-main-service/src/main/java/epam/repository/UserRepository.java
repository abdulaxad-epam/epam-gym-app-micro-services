package epam.repository;

import epam.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Integer> {

    boolean existsByUsername(String username);

    boolean existsByUsernameAndPassword(String username, String password);

    Optional<User> findByUsername(String username);
}
