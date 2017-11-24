package by.instinctools.domain.main;

import by.instinctools.domain.mapper.MapperManagement;
import by.instinctools.domain.validator.ValidateManagement;
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
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Component
public class MainManager implements MainManagement {

    private static final int LESS = -1;

    private final MapperManagement<RawTransaction, Transaction> mapper;
    private final ValidateManagement<RawTransaction> validator;
    private final Web3j web3j;

    private final String hostAddress;

    private final Map<String, CompletableFuture> cashe;

    @Autowired
    public MainManager(final MapperManagement<RawTransaction, Transaction> mapper,
                       final ValidateManagement<RawTransaction> validator,
                       final Web3j web3j,

                       @Value("walllet.host.address") final String hostAddress) {
        this.validator = validator;
        this.mapper = mapper;
        this.web3j = web3j;

        this.hostAddress = hostAddress;
        this.cashe = new HashMap<>();
    }

    @Override
    public String sendRawTransaction(final RawTransaction rawTransaction, final String tx) {
        validator.validate(rawTransaction);
        final Transaction transaction = mapper.transform(rawTransaction);

        final CompletableFuture<EthGetBalance> getBalance = web3j.ethGetBalance(transaction.getFrom(), LATEST).sendAsync();
        final CompletableFuture<EthEstimateGas> getGas = web3j.ethEstimateGas(transaction).sendAsync();
        final CompletableFuture<EthGasPrice> getGasPice = web3j.ethGasPrice().sendAsync();

        final CompletableFuture future = CompletableFuture.allOf(
                getBalance,
                getGas,
                getGasPice
        ).thenAccept((ignoreVoid) -> {

            final BigInteger gasPrice = getGasPice.join().getGasPrice();
            final BigInteger balance = getBalance.join().getBalance();
            final BigInteger ethEstimateGas = getGas.join().getAmountUsed();

            final BigInteger executedCost = gasPrice.multiply(ethEstimateGas);

            try {
                if (balance.compareTo(executedCost) == LESS) {

                    final Transaction trans = Transaction.createEtherTransaction(
                            hostAddress,
                            BigInteger.ZERO,
                            gasPrice,
                            rawTransaction.getGasLimit(),
                            transaction.getFrom(),
                            executedCost.subtract(balance));

                    web3j.ethSendTransaction(trans).send();
                }

                web3j.ethSendRawTransaction(tx).send();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final String key = UUID.randomUUID().toString();
        cashe.put(key, future);
        return key;
    }

    @Override
    public Status checkRawStatus(final String token) {

        final CompletableFuture future = cashe.get(token);

        if (future.isDone()) {
            return Status.FINISHED;
        }

        if (future.isCompletedExceptionally()) {
            return Status.ERROR;
        }

        return Status.PENDING;
    }
}
