package by.instinctools.mvc.auth.filter;

import by.instinctools.domain.entity.User;
import by.instinctools.domain.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@Component
public class AuthenticationTokenProcessingFilter extends GenericFilterBean {

    private final UserRepository repository;

    @Autowired
    public AuthenticationTokenProcessingFilter(final UserRepository repository) {
        this.repository = repository;
    }

    @Override
    public void doFilter(final ServletRequest servletRequest,
                         final ServletResponse servletResponse,
                         final FilterChain filterChain) throws IOException, ServletException {

        final String accessToken = extractAuthTokenFromRequest(servletRequest);

        if (accessToken != null) {
            final User user = repository.findByToken(accessToken);

            final Authentication authentication = new UsernamePasswordAuthenticationToken(user.getEmail(), user.getToken());

            final SecurityContext context = SecurityContextHolder.getContext();
            context.setAuthentication(authentication);
        }

        filterChain.doFilter(servletRequest, servletResponse);
    }

    private String extractAuthTokenFromRequest(final ServletRequest servletRequest) {
        final HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
        final String authToken = httpRequest.getHeader("X-Access-Token");
        return authToken == null ? httpRequest.getParameter("token") : authToken;
    }
}
