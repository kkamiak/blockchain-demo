package by.instinctools.domain.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.protocol.core.methods.request.Transaction;

import javax.validation.ValidationException;

@Service("RequestTransactionValidator")
@Qualifier("RequestTransactionValidator")
public class RequestTransactionValidator implements ValidateManagement<Transaction> {

    private final String smartContractAddress;

    @Autowired
    public RequestTransactionValidator(@Value("${smart.contract.address}") final String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }

    @Override
    public void validate(final Transaction transaction) {

        final String to = transaction.getTo();

        if (!StringUtils.equals(to, smartContractAddress)) {
            throw new ValidationException("Wrong contract address.");
        }
    }
}
