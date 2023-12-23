package connector;

import ext.IntegrationServiceClient;
import ext.ModelStoreClient;

public class ConnectorFactory {

    public Connector getConnector(IntegrationServiceClient integrationServiceClient, ModelStoreClient modelStoreClient, IntegrationServiceClient.Application application) {
        switch (application.getKind()) {
            case Constants.SALESFORCE_APP_TYPE:
                return new SalesforceConnector(integrationServiceClient, modelStoreClient, application);
            case Constants.HUBSPOT_APP_TYPE:
                return new HubSpotConnector(integrationServiceClient, modelStoreClient, application);
            default:
                return new GenericConnector();
        }
    }
}
