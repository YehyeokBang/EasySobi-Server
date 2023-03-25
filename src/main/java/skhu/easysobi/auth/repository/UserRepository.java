package skhu.easysobi.auth.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import skhu.easysobi.auth.domain.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

}
