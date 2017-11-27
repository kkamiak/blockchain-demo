package by.instinctools.domain.main;

import by.instinctools.domain.entity.DBStatus;
import by.instinctools.domain.entity.Status;
import by.instinctools.domain.mapper.MapperManagement;
import by.instinctools.domain.repository.TransactionStatusRepository;
import by.instinctools.domain.validator.ValidateManagement;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.ConcurrentReferenceHashMap;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.protocol.core.methods.response.EthEstimateGas;
import org.web3j.protocol.core.methods.response.EthGasPrice;
import org.web3j.protocol.core.methods.response.EthGetBalance;

import java.io.IOException;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static by.instinctools.domain.entity.Status.PENDING;
import static java.math.BigInteger.ZERO;
import static java.time.LocalDate.now;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Component
public class MainManager implements MainManagement {

    private static final BigInteger NONCE = ZERO;
    private static final int LESS = -1;

    private final MapperManagement<String, Transaction> mapper;
    private final ValidateManagement<Transaction> validator;
    private final TransactionStatusRepository repository;
    private final Web3j web3j;

    private final BigInteger gasLimit;
    private final long maxPendingDays;
    private final String hostAddress;

    private final Map<String, CompletableFuture> cache;

    @Autowired
    public MainManager(final MapperManagement<String, Transaction> mapper,
                       final ValidateManagement<Transaction> validator,
                       final TransactionStatusRepository repository,
                       final Web3j web3j,

                       @Value("${transaction.gas.limit}") final BigInteger gasLimit,
                       @Value("${wallet.host.address}") final String hostAddress,
                       @Value("${max.pending.days}") final long maxPendingDays) {

        this.repository = repository;
        this.validator = validator;
        this.mapper = mapper;
        this.web3j = web3j;

        this.maxPendingDays = maxPendingDays;
        this.hostAddress = hostAddress;
        this.gasLimit = gasLimit;

        this.cache = new ConcurrentReferenceHashMap<>();
    }

    @Override
    public String sendRawTransaction(final String tx) {
        final Transaction transaction = mapper.transform(tx);
        validator.validate(transaction);

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
                            NONCE,
                            gasPrice,
                            gasLimit,
                            transaction.getFrom(),
                            executedCost.subtract(balance));

                    web3j.ethSendTransaction(trans).send();
                }

                web3j.ethSendRawTransaction(tx).send();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        final String token = UUID.randomUUID().toString();

        final DBStatus entity = new DBStatus();
        entity.setStatus(PENDING);
        entity.setToken(token);
        entity.setTime(now().plusDays(maxPendingDays));

        repository.save(entity);

        cache.put(token, future);

        return token;
    }

    @Override
    public Status checkTransactionStatus(final String token) {
        return repository.findByToken(token).getStatus();
    }

    @Scheduled(cron = "*/20 * * * * *")
    public void cleanCache() {
        cache.entrySet().stream()
                .filter(ks -> !Status.PENDING.equals(getStatus(ks)))
                .peek(ks -> cache.remove(ks.getKey()))
                .forEach(ks -> {
                    final DBStatus byToken = repository.findByToken(ks.getKey());
                    byToken.setStatus(getStatus(ks));
                    repository.save(byToken);
                });
    }

    private Status getStatus(final Map.Entry<String, CompletableFuture> ks) {
        final CompletableFuture future = ks.getValue();

        if (future.isDone()) {
            return Status.FINISHED;
        }

        if (future.isCompletedExceptionally()) {
            return Status.ERROR;
        }

        final DBStatus dbStatus = repository.findByToken(ks.getKey());
        if (now().isAfter(dbStatus.getTime())) {
            return Status.EXPIRED;
        }

        return Status.PENDING;
    }
}
