package by.instinctools.domain.main;

import by.instinctools.domain.entity.Status;

public interface MainManagement {

    String sendRawTransaction(String tx);

    Status checkTransactionStatus(String token);
}
