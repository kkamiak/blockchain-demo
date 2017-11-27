package by.instinctools.domain.repository;

import by.instinctools.domain.entity.DBStatus;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionStatusRepository extends CrudRepository<DBStatus, Long> {

    DBStatus findByToken(String token);
}
