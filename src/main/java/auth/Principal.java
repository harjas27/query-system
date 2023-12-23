package auth;

import java.io.Serializable;

public interface Principal extends Serializable {
    String getId();

    PrincipalType getPrincipalType();

    String getAccountId();

    String getTenantId();

    boolean hasPermission(String permission);
}

enum PrincipalType {
    PERSON, SERVICE
}
