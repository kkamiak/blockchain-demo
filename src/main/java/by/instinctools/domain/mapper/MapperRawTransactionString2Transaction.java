package by.instinctools.domain.mapper;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.request.Transaction;

import java.math.BigInteger;

@Service
public class MapperRawTransactionString2Transaction implements MapperManagement<String, Transaction> {

    @Override
    public Transaction transform(final String data) {

        try {

            final by.instinctools.utils.Transaction source = new by.instinctools.utils.Transaction(Hex.decodeHex(data.toCharArray()));

            return new Transaction(
                    Hex.encodeHexString(source.getSender()),
                    new BigInteger(source.getNonce()),
                    new BigInteger(source.getGasPrice()),
                    new BigInteger(source.getGasLimit()),
                    Hex.encodeHexString(source.getReceiveAddress()),
                    new BigInteger(source.getValue()),
                    Hex.encodeHexString(source.getData())
            );

        } catch (DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
