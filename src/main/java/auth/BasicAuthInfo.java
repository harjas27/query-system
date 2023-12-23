package auth;

public class BasicAuthInfo implements AuthInfo {

    private final Principal principal;
    private final String token;
    private final String tenantId;
    private final String[] roles;

    public BasicAuthInfo(String userId, String token, String[] roles) {
        this.principal = new PrincipalImpl(userId, null);
        this.token = token;
        this.roles = roles;
        this.tenantId = null;
    }

    public BasicAuthInfo(String userId, String tenantId, String token, String[] roles) {
        this.roles = roles;
        this.principal = new PrincipalImpl(userId, tenantId);
        this.token = token;
        this.tenantId = tenantId;
    }

    public Principal getPrincipal() {
        return principal;
    }

    public String getToken() {
        return token;
    }

    public AuthType getAuthType() {
        return AuthType.BASIC;
    }

    @Override
    public String[] getRoles() {
        return roles;
    }

    public String getTenantCode() {
        return null;
    }


    public String getTenantId() {
        return this.tenantId;
    }


    public String getAccountId() {
        return null;
    }

    static class PrincipalImpl implements Principal {

        private final String userId;
        private final String tenantId;

        public PrincipalImpl(String userId) {
            this.userId = userId;
            this.tenantId = null;
        }
        public PrincipalImpl(String userId, String tenantId) {
            this.userId = userId;
            this.tenantId = tenantId;
        }

        public String getId() {
            return userId;
        }

        public PrincipalType getPrincipalType() {
            return null;
        }

        public String getAccountId() {
            return null;
        }

        public String getTenantId() {
            return this.tenantId;
        }

        public boolean hasPermission(String permission) {
            return false;
        }
    }
}
