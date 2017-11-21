package by.instinctools.mvc.auth;

import by.instinctools.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Service;

@Service
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private UserRepository repository;

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        final Object username = authentication.getPrincipal();
        final Object password = authentication.getCredentials();

        return repository.existsByEmail(username.toString())
                ? new UsernamePasswordAuthenticationToken(username, password)
                : null;
    }

    @Override
    public boolean supports(final Class<?> aClass) {
        return false;
    }
}
