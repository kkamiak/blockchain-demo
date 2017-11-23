package by.instinctools.rest.controller;

import by.instinctools.domain.main.MainManagement;
import by.instinctools.domain.mapper.MapperManagement;
import by.instinctools.domain.validator.ValidateManagement;
import by.instinctools.rest.dto.TransactionDto;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.web3j.crypto.RawTransaction;

@Controller
@RequestMapping(value = "/api")
public class BlockchainController {

    private final ValidateManagement<TransactionDto> validator;
    private final MapperManagement<TransactionDto, RawTransaction> mapper;
    private final MainManagement main;

    @Autowired
    public BlockchainController(final MapperManagement<TransactionDto, RawTransaction> mapper,
                                final ValidateManagement<TransactionDto> validator,
                                final MainManagement main) {
        this.validator = validator;
        this.mapper = mapper;
        this.main = main;
    }

    @PostMapping(path = "/blockchain")
    public ResponseEntity<String> sendRawTransaction(@RequestBody final TransactionDto transaction) throws DecoderException {
        validator.validate(transaction);
        final RawTransaction rt = mapper.transform(transaction);
        main.sendRawTransaction(rt, transaction.getTx());

        return ResponseEntity.ok().body("Transaction successfully sent.");
    }
}
