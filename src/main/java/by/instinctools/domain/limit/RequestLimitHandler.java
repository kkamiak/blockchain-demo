package by.instinctools.domain.limit;

import by.instinctools.domain.entity.User;
import by.instinctools.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RequestLimitHandler implements LimitHandler {

    private final Long amountTransaction;
    private final UserRepository repository;

    public RequestLimitHandler(@Value("${amount.transaction}") final Long amountTransaction,
                               final UserRepository repository) {
        this.amountTransaction = amountTransaction;
        this.repository = repository;
    }

    @Override
    public boolean canProceed(final User user) {
        return false;
    }
}
