package by.instinctools.config;

import by.instinctools.domain.ethereum.EthereumBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.concurrent.Executors;

@Configuration
public class Config {

    @Bean
    public EthereumBean ethereumBean() throws Exception {
        final EthereumBean ethereumBean = new EthereumBean();
        Executors.newSingleThreadExecutor().submit(ethereumBean::start);
        return ethereumBean;
    }
}
