package by.instinctools.domain.main;

import org.web3j.crypto.RawTransaction;

public interface MainManagement {

    void sendRawTransaction(RawTransaction rawTransaction, String tx);
}
