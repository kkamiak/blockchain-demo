package by.instinctools.domain.main;

import by.instinctools.domain.entity.Status;
import org.web3j.crypto.RawTransaction;

public interface MainManagement {

    String sendRawTransaction(RawTransaction rawTransaction, String tx);

    Status checkTransactionStatus(String token);
}
