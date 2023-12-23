package auth;

import org.springframework.security.authentication.AuthenticationCredentialsNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

public class AuthInfoHolder {

    public static String getTenantId() {
        return getAuthInfo().getPrincipal().getTenantId();
    }

    public static String getToken() {
        return getAuthInfo().getToken();
    }

    public static boolean isAuthenticated() {
        return getAuthInfo() != null;
    }

    public static Principal getPrincipal() {
        return getAuthInfo().getPrincipal();
    }

    public static AuthInfo getAuthInfo() {
        SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "The SecurityContext is not set. Make sure that the authentication is enabled on this module");
        }
        Authentication authentication = context.getAuthentication();
        if (authentication == null) {
            throw new AuthenticationCredentialsNotFoundException(
                    "The Authentication is not found on the context. Make sure that the authentication is enabled on this module");
        }
        return (AuthInfo) authentication;
    }

    public static void removeAuthInfo() {
        SecurityContextHolder.clearContext();
    }

    public static String getTenantIdOrNull() {
        AuthInfo authInfo = (AuthInfo) SecurityContextHolder.getContext().getAuthentication();
        if (authInfo == null) {
            return null;
        }
        return authInfo.getTenantId();
    }
}
