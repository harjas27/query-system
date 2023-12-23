package connector;

import auth.AuthInfo;
import query.UniversalQuery;

public abstract class AbstractConnector implements Connector {

    public String query(AuthInfo authInfo, UniversalQuery query) {
        return "";
    }
}
