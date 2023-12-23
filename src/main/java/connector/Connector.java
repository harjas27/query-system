package connector;

import auth.AuthInfo;
import query.UniversalQuery;

public interface Connector {

    String query(AuthInfo authInfo, UniversalQuery query);

}

