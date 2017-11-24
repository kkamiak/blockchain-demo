package by.instinctools.domain.main;

import org.web3j.crypto.RawTransaction;

public interface MainManagement {

    String sendRawTransaction(RawTransaction rawTransaction, String tx);

    Status checkRawStatus(String token);
}
