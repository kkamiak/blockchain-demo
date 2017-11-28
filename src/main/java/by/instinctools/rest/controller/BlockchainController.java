package by.instinctools.rest.controller;

import by.instinctools.domain.entity.Status;
import by.instinctools.domain.main.MainManagement;
import by.instinctools.domain.validator.ValidateManagement;
import by.instinctools.rest.dto.RawTransactionDto;
import org.apache.commons.codec.DecoderException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping(value = "/api")
public class BlockchainController {

    private final ValidateManagement<RawTransactionDto> validator;
    private final MainManagement main;

    @Autowired
    public BlockchainController(final ValidateManagement<RawTransactionDto> validator,
                                final MainManagement main) {
        this.validator = validator;
        this.main = main;
    }

    @PostMapping(path = "/blockchain")
    public ResponseEntity<String> sendRawTransaction(@RequestBody final RawTransactionDto transaction) throws DecoderException {
        validator.validate(transaction);
        final String token = main.sendRawTransaction(transaction.getTx());
        return ResponseEntity.ok().body("");
    }

    @GetMapping(path = "/blockchain/check")
    public ResponseEntity<Status> checkRawStatus(@RequestBody final String token) {
        final Status status = main.checkTransactionStatus(token);
        return ResponseEntity.ok().body(status);
    }
}
