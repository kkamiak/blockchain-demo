package by.instinctools.domain.validator;

import by.instinctools.rest.dto.RawTransactionDto;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;

@Service("TransactionDTOValidator")
@Qualifier("TransactionDTOValidator")
public class TransactionDTOValidator implements ValidateManagement<RawTransactionDto> {

    @Override
    public void validate(final RawTransactionDto transaction) {

        final String data = transaction.getTx();

        if (StringUtils.isEmpty(data)) {
            throw new ValidationException("Data is empty.");
        }
    }

}
