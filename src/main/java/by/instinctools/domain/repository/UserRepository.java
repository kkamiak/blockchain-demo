package by.instinctools.domain.repository;

import by.instinctools.domain.model.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Long> {

    Boolean existsByToken(final String token);

    Boolean existsByEmail(final String email);

    User findByEmail(final String email);

    User findByToken(final String token);
}
