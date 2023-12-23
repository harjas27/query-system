package auth;

import java.io.Serializable;

public interface AuthInfo extends Serializable {
    Principal getPrincipal();
    String getToken();
    String getTenantCode();
    String getTenantId();
    String getAccountId();
    AuthType getAuthType();
    String[] getRoles();
}

enum AuthType {
    BASIC, BEARER, SESSION_ID
}