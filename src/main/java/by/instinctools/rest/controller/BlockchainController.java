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
    public void sendTransaction(@RequestBody final TransactionDto transaction) throws DecoderException {

        final String data = transaction.getTx();
        byte[] b = Hex.decodeHex(data.toCharArray());
        EncodedTransaction tx = new EncodedTransaction(b);
        tx.toString();
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
