package by.instinctools.domain.mapper;

import by.instinctools.rest.dto.TransactionDto;
import by.instinctools.utils.Transaction;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.stereotype.Service;
import org.web3j.crypto.RawTransaction;

import java.math.BigInteger;

@Service
public class MapperRawDto2Model implements MapperManagement<TransactionDto, RawTransaction> {

    @Override
    public RawTransaction transform(final TransactionDto transaction) {

        try {

            final String data = transaction.getTx();
            final Transaction tx = new Transaction(Hex.decodeHex(data.toCharArray()));

            return RawTransaction.createTransaction(
                    new BigInteger(tx.getNonce()),
                    new BigInteger(tx.getGasPrice()),
                    new BigInteger(tx.getGasLimit()),
                    Hex.encodeHexString(tx.getReceiveAddress()),
                    new BigInteger(tx.getValue()),
                    Hex.encodeHexString(tx.getData())
            );

        } catch (DecoderException e) {
            throw new IllegalArgumentException(e);
        }
    }

}
