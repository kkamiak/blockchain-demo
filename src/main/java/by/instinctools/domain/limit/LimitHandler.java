package by.instinctools.domain.limit;

import by.instinctools.domain.entity.User;

public interface LimitHandler {
    boolean canProceed(User user);
}
