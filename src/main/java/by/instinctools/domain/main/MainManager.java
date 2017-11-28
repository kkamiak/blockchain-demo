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
import org.web3j.crypto.Credentials;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.methods.request.Transaction;
import org.web3j.tx.Transfer;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

import static by.instinctools.domain.entity.Status.PENDING;
import static java.time.LocalDate.now;
import static org.web3j.protocol.core.DefaultBlockParameterName.LATEST;

@Component
public class MainManager implements MainManagement {

    private static final BigInteger TRANSFER_GAS_AMOUNT = new BigInteger("210000");
    private static final String PREFIX = "0x";
    private static final int LESS = -1;

    private final MapperManagement<String, Transaction> mapper;
    private final ValidateManagement<Transaction> validator;
    private final TransactionStatusRepository repository;
    private final Credentials credentials;
    private final Web3j web3j;

    private final long maxPendingDays;

    private final Map<String, CompletableFuture> cache;

    @Autowired
    public MainManager(final MapperManagement<String, Transaction> mapper,
                       final ValidateManagement<Transaction> validator,
                       final TransactionStatusRepository repository,
                       final Web3j web3j,
                       final Credentials credentials,

                       @Value("${max.pending.days}") final long maxPendingDays) {

        this.repository = repository;
        this.validator = validator;
        this.mapper = mapper;
        this.web3j = web3j;

        this.maxPendingDays = maxPendingDays;
        this.credentials = credentials;

        this.cache = new ConcurrentReferenceHashMap<>();
    }

    @Override
    public String sendRawTransaction(final String tx) {
        final Transaction transaction = mapper.transform(tx);
        validator.validate(transaction);

        final String from = normalize(transaction.getFrom());

        final CompletableFuture<Void> future = web3j.ethGetBalance(from, LATEST)
                .sendAsync()
                .thenAccept((ethBalance) -> {

                    final BigInteger balance = ethBalance.getBalance();
                    final BigInteger price = Numeric.decodeQuantity(transaction.getGasPrice());
                    final BigInteger executedCost = price.multiply(TRANSFER_GAS_AMOUNT);

                    try {
                        if (balance.compareTo(executedCost) == LESS) {

                            final BigDecimal amount = new BigDecimal(executedCost.subtract(balance));

                            Transfer.sendFunds(
                                    web3j,
                                    credentials,
                                    from,
                                    amount,
                                    Convert.Unit.WEI
                            ).send();
                        }

                        web3j.ethSendRawTransaction(normalize(tx)).send();

                    } catch (final Exception e) {
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

    private String normalize(final String address) {
        return address.startsWith(PREFIX) ? address : PREFIX + address;
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
