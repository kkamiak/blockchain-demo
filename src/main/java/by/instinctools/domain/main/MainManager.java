package by.instinctools.domain.main;

import by.instinctools.domain.mapper.MapperManagement;
import by.instinctools.domain.validator.ValidateManagement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.web3j.crypto.RawTransaction;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.IOException;
import java.math.BigInteger;

import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Component
public class MainManager implements MainManagement {

    private static final Logger LOGGER = LoggerFactory.getLogger(MainManager.class);

    private static final int LESS = -1;

    private final MapperManagement<RawTransaction, Transaction> mapper;
    private final ValidateManagement<RawTransaction> validator;
    private final Web3j web3j;

    private final String hostAddress;

    @Autowired
    public MainManager(final MapperManagement<RawTransaction, Transaction> mapper,
                       final ValidateManagement<RawTransaction> validator,
                       final Web3j web3j,

                       @Value("walllet.host.address") final String hostAddress) {
        this.validator = validator;
        this.mapper = mapper;
        this.web3j = web3j;

        this.hostAddress = hostAddress;
    }

    @Override
    public void sendRawTransaction(final RawTransaction rawTransaction, final String tx) {
        validator.validate(rawTransaction);
        final Transaction transaction = mapper.transform(rawTransaction);

        try {

            final EthGetBalance ethBalance = web3j.ethGetBalance(transaction.getFrom(), LATEST).send();
            final BigInteger balance = ethBalance.getBalance();

            final EthEstimateGas ethEstimateGas = web3j.ethEstimateGas(transaction).send();
            final EthGasPrice gasPrice = web3j.ethGasPrice().send();
            final BigInteger executedCost = gasPrice.getGasPrice().multiply(ethEstimateGas.getAmountUsed());

            final int i = balance.compareTo(executedCost);

            if (i == LESS) {

                final Transaction etherTransaction = Transaction.createEtherTransaction(
                        hostAddress,
                        BigInteger.ZERO,
                        gasPrice.getGasPrice(),
                        rawTransaction.getGasLimit(),
                        transaction.getFrom(),
                        executedCost.subtract(balance));

                web3j.ethSendTransaction(etherTransaction).send();
            }

            web3j.ethSendRawTransaction(tx);

        } catch (final IOException e) {
            LOGGER.error("Error : ", e);
        }
    }

//    TODO: move to async execution
//    private CompletableFuture<EthGasPrice> getGasPrice;
//    private EthGasPrice ethGasPrice;
//
//    @Scheduled(cron = "0 0 * * * *")
//    public void updateGasPrice() throws ExecutionException, InterruptedException {
//
//        if (getGasPrice == null) {
//            getGasPrice = web3j.ethGasPrice().sendAsync();
//        } else if (getGasPrice.isDone()) {
//            ethGasPrice = getGasPrice.get();
//        }
//    }
}
