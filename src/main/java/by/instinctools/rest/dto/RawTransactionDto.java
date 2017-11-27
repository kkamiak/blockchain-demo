package by.instinctools.rest.dto;

import java.io.Serializable;

public class RawTransactionDto implements Serializable {

    private static final long serialVersionUID = -612398939922696830L;

    private String tx;
    private String token;

    public String getTx() {
        return tx;
    }

    public void setTx(String tx) {
        this.tx = tx;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
