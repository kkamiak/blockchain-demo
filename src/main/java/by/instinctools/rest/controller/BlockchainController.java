package by.instinctools.rest.controller;

import by.instinctools.rest.dto.TransactionDto;
import by.instinctools.utils.EncodedTransaction;
import by.instinctools.utils.ExtendedTransactionUtil;
import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.http.HttpService;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.logging.Logger;

import static org.web3j.utils.Convert.Unit.ETHER;
import static org.web3j.utils.Convert.Unit.GWEI;

@Controller
@RequestMapping(value = "/api")
public class BlockchainController {

    public static final Logger LOGGER = Logger.getLogger(BlockchainController.class.getName());


    public BlockchainController(@Value("smart.contract.address") final String smartContractAddress) {
        this.smartContractAddress = smartContractAddress;
    }

    private final String smartContractAddress;

    @PostMapping(path = "/blockchain")
    public void sendTransaction(@RequestBody final TransactionDto transaction) throws DecoderException {

        final String data = transaction.getTx();
        byte[] b = Hex.decodeHex(data.toCharArray());
        by.instinctools.utils.Transaction tx = new by.instinctools.utils.Transaction(b);
//        tx.toString();

//        byte[] gasPrice = tx.getGasPrice();


//        BigInteger nonce = new BigInteger(tx.getNonce());
//        BigInteger value = new BigInteger(tx.getValue());
        BigInteger gasLimit = new BigInteger(tx.getGasLimit());
        BigInteger gasPrice = new BigInteger(tx.getGasPrice());
//        String transactionSigner = Hex.encodeHexString(tx.getSender());
        String receiver = Hex.encodeHexString(tx.getReceiveAddress());

//        LOGGER.info("Signed by: " + transactionSigner);
//        LOGGER.info("Nonce " + nonce);
        LOGGER.info("To " + receiver);
//        LOGGER.info("Value " + value + " (" + Convert.fromWei(new BigDecimal(value), ETHER) + " ETH)");
        LOGGER.info("Data: " + data);
        LOGGER.info("Gas limit: " + gasLimit);
        LOGGER.info("Gas price: " + gasPrice + " (" + Convert.fromWei(new BigDecimal(gasPrice), GWEI) + " Gwei)");

        byte [] ttt = tx.getSender();
        String sender = Hex.encodeHexString(ttt);

        Web3j web3j = Web3j.build(new HttpService(
                "https://rinkeby.infura.io/WVvaSdEc0vA8e4yI3wUv "));


    }


//    @PostMapping(path = "/blockchain")
//    public void sendTransaction(@RequestBody final TransactionDto transaction) throws DecoderException {
//
//        final String data = transaction.getTx();
//        byte[] b = Hex.decodeHex(data.toCharArray());
//
//        Transaction tx = new Transaction(b);
//        tx.rlpParse();
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


//    }
}
