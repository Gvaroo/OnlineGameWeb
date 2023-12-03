package ge.itstep.demo.repository;

import ge.itstep.demo.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository

public interface UserRepository extends JpaRepository<User, Long> {
    User findUserByEmail(String email);
    User findUserById(long id);

    boolean existsByEmail(String email);
}
