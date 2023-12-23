package ext;

import auth.AuthInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModelStoreClient {

    public List<String> getApplicationsInvolved(AuthInfo authInfo, List<String> objects, List<String> fields) {
        // noop
        return new ArrayList<>();
    }

    public Map<String, String> getFieldNamesForApplication(AuthInfo authInfo, List<String> fields, String applicationType) {
        return new HashMap<String, String>();
    }

    public Map<String, String> getGenericFieldNames(AuthInfo authInfo, List<String> fields, String applicationType) {
        return new HashMap<String, String>();
    }

    public String getObjectNameForApplication(AuthInfo authInfo, String object, String applicationType) {
        return "";
    }

}
