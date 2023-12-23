package auth;

import ext.TokenServiceClient;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(value = 0)
public class BearerTokenAuthenticationFilter extends OncePerRequestFilter {

    private final TokenServiceClient tokenServiceClient;

    public BearerTokenAuthenticationFilter(TokenServiceClient tokenServiceClient) {
        this.tokenServiceClient = tokenServiceClient;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        // get token
        tokenServiceClient.validate(header);
        filterChain.doFilter(request, response);
    }
}
