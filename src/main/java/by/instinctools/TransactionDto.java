package by.instinctools;

import org.web3j.crypto.RawTransaction;

import java.io.Serializable;

/**
 * Created by haria on 21.11.17.
 */
public class TransactionDto implements Serializable{

    private String token;
    private RawTransaction tx;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public RawTransaction getTx() {
        return tx;
    }

    public void setTx(RawTransaction tx) {
        this.tx = tx;
    }
}
