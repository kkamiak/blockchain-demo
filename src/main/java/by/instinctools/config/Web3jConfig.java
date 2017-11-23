package by.instinctools.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.Web3jService;
import org.web3j.protocol.http.HttpService;

import java.io.IOException;

@Configuration
public class Web3jConfig {

    @Bean
    public Web3jService web3jService() {
        return new HttpService();
    }

    @Bean
    public Web3j web3j(final Web3jService web3jService) {
        return Web3j.build(web3jService);
    }

    @Bean
    public Credentials credentials(@Value("${wallet.password}") final String password,
                                   @Value("${wallet.file.path}") final String walletPath) throws IOException, CipherException {
        final String pathResource = new ClassPathResource(walletPath).getFile().getAbsolutePath();
        return WalletUtils.loadCredentials(password, pathResource);
    }
}
