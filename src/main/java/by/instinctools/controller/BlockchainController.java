package by.instinctools.controller;

import by.instinctools.TransactionDto;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.util.logging.Logger;

/**
 * Created by haria on 21.11.17.
 */
@Controller
@RequestMapping(value="/api")
public class BlockchainController {

    public static final  Logger LOGGER = Logger.getLogger(BlockchainController.class.getName());

    @PostMapping(path = "/blockchain")
    public void sendTransaction(@RequestBody final TransactionDto transaction) {
        final RawTransaction tx = transaction.getTx();
//
//        final String data = tx.getData();
//        final String transactionSigner = tx.getFrom().toLowerCase();
//        final String recipient = tx.getTo().toLowerCase();
//        final String value = tx.getValue();
//        final String gasLimit = tx.getGas();
//        final String gasPrice = tx.getGasPrice();
//        final String nonce = tx.getNonce();
//
//        LOGGER.info("Signed by: " + transactionSigner);
//        LOGGER.info("Nonce: " + nonce);
//        LOGGER.info("To: " + recipient );
//        LOGGER.info("Value: " + value + " (" + Convert.fromWei(value, Convert.Unit.ETHER) + "ETH)");
//        LOGGER.info("Data: " + Numeric.toHexString(data.getBytes()));
//        LOGGER.info("Gas limit: " + gasLimit);
//        LOGGER.info("Gas Price: " + gasPrice + " (" + Convert.fromWei(gasPrice, Convert.Unit.GWEI) + "Gwei)");

    }

}
