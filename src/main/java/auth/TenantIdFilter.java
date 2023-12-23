package auth;

import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Order(value = 1)
public class TenantIdFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String tenantId;
        try {
            AuthInfo authInfo = AuthInfoHolder.getAuthInfo();
            tenantId = authInfo.getTenantId();
        } catch (Exception e) {
            tenantId = "NO_AUTH";
        }
        request.setAttribute("tenantId", tenantId);
        filterChain.doFilter(request, response);
    }
}