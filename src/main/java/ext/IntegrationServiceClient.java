package ext;

import auth.AuthInfo;

import java.util.ArrayList;
import java.util.List;

public class IntegrationServiceClient {

    public List<Application> getIntegratedApplications(AuthInfo authInfo, List<String> types) {
        return new ArrayList<>();
    }

    public String getAuthenticationCredentials(AuthInfo authInfo, String applicationId) {
        // pass rbac claims
//        authInfo.getRoles()
        return "";
    }

    public String getEndpoint(AuthInfo authInfo, String applicationId) {
        return "";
    }

    public static class Application {
        private final String id;
        private final String endpoint;
        private final String kind;

        public Application(String id, String endpoint, String metadata) {
            this.id = id;
            this.endpoint = endpoint;
            this.kind = metadata;
        }

        public String getId() {
            return this.id;
        }

        public String getEndpoint() {
            return this.endpoint;
        }

        public String getKind() {
            return this.kind;
        }
    }

}
