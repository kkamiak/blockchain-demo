package by.instinctools.domain.validator;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.web3j.crypto.RawTransaction;

import javax.validation.ValidationException;

@Service("RawTransactionValidator")
@Qualifier("RawTransactionValidator")
public class RawTransactionValidator implements ValidateManagement<RawTransaction> {

    private final String smartContractAddress;

    @Autowired
    public RawTransactionValidator(@Value("smart.contract.address") final String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }

    @Override
    public void validate(final RawTransaction transaction) {

        final String to = transaction.getTo();

        if (!StringUtils.equals(to, smartContractAddress)) {
            throw new ValidationException("Wrong contract address.");
        }
    }
}
