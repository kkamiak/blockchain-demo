package by.instinctools.domain.ethereum;

import org.ethereum.facade.Ethereum;
import org.ethereum.facade.EthereumFactory;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.concurrent.Executors;

@Component
public class EthereumBean {

    Ethereum ethereum;

    @PostConstruct
    void init() {
        Executors.newSingleThreadExecutor().submit(this::start);
    }

    public void start() {
        this.ethereum = EthereumFactory.createEthereum();
        this.ethereum.addListener(new EthereumListener(ethereum));
    }

    public String getBestBlock() {
        return "" + ethereum.getBlockchain().getBestBlock().getNumber();
    }
}
