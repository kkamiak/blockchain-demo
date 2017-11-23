package by.instinctools.domain.mapper;

import by.instinctools.rest.dto.TransactionDto;
import org.springframework.stereotype.Service;
import org.web3j.crypto.RawTransaction;

import static java.math.BigInteger.ZERO;
import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class MapperRawDto2Model implements MapperManagement<TransactionDto, RawTransaction> {

    @Override
    public RawTransaction transform(final TransactionDto transaction) {

//        TODO: stub method. waiting to implement decoding string
//        final String data = transaction.getTx();
//        byte[] b = Hex.decodeHex(data.toCharArray());
//
//
//        BigInteger nonce = new BigInteger(tx.getNonce());
//        BigInteger value = new BigInteger(tx.getValue());
//        BigInteger gasLimit = new BigInteger(tx.getGasLimit());
//        BigInteger gasPrice = new BigInteger(tx.getGasPrice());
//        String transactionSigner = Hex.encodeHexString(tx.getSender());
//        String receiver = Hex.encodeHexString(tx.getReceiveAddress());
//
//        LOGGER.info("Signed by: " + transactionSigner);
//        LOGGER.info("Nonce " + nonce);
//        LOGGER.info("To " + receiver);
//        LOGGER.info("Value " + value + " (" + EtherUtil.convert(value.longValue(), EtherUtil.Unit.ETHER) + " ETH)");
//        LOGGER.info("Data: " + data);
//        LOGGER.info("Gas limit: " + gasLimit);
//        LOGGER.info("Gas price: " + gasPrice + " (" + EtherUtil.convert(gasPrice.longValue(), EtherUtil.Unit.GWEI) + " Gwei)");

        return RawTransaction.createTransaction(
                ZERO,
                ZERO,
                ZERO,
                EMPTY,
                ZERO,
                EMPTY
        );
    }

}
