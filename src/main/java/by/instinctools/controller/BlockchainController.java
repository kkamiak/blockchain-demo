package by.instinctools.controller;

import by.instinctools.TransactionDto;
import org.apache.commons.lang3.StringUtils;
import org.ethereum.core.Transaction;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.logging.Logger;

@Controller
@RequestMapping(value = "/api")
public class BlockchainController {

    public static final Logger LOGGER = Logger.getLogger(BlockchainController.class.getName());

    public BlockchainController(@Value("smart.contract.address") final String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }

    private final String smartContractAddress;

    @PostMapping(path = "/blockchain")
    public void sendTransaction(@RequestBody final TransactionDto transaction) {

        final String data = transaction.getTx();
        final String transactionSigner = transaction.getFrom();
        final String gasLimit = transaction.getGasLimit();

        if (StringUtils.equals(transaction.getTo(), smartContractAddress)) {
//            throw  new IllegalArgumentException('Wrong contract address');
//            return next(error);
        }

    }
}
