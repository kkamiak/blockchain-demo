package by.instinctools.mvc.auth.filter;

import by.instinctools.domain.limit.LimitHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

@Component
public class RequestLimitFilter implements Filter {

    private final LimitHandler handler;

    @Autowired
    public RequestLimitFilter(LimitHandler handler) {
        this.handler = handler;
    }

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(final ServletRequest request,
                         final ServletResponse response,
                         final FilterChain chain) throws IOException, ServletException {
    }

    @Override
    public void destroy() {
    }
}
