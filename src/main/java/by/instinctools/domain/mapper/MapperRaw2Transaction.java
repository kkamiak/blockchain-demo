package by.instinctools.domain.mapper;

import org.springframework.stereotype.Service;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;

import static org.apache.commons.lang3.StringUtils.EMPTY;

@Service
public class MapperRaw2Transaction implements MapperManagement<RawTransaction, Transaction> {

    @Override
    public Transaction transform(final RawTransaction source) {
        return new Transaction(
                EMPTY,
                source.getNonce(),
                source.getGasPrice(),
                source.getGasLimit(),
                source.getTo(),
                source.getValue(),
                source.getData()
        );
    }
}
