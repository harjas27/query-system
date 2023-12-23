package service;

import auth.AuthInfo;
import connector.Connector;
import connector.ConnectorFactory;
import ext.IntegrationServiceClient;
import ext.ModelStoreClient;
import query.UniversalQuery;
import utils.QueryUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class QueryServiceImpl implements QueryService {

    private final IntegrationServiceClient integrationServiceClient;
    private final ModelStoreClient modelStoreClient;
    private final ConnectorFactory connectorFactory;

    public QueryServiceImpl(IntegrationServiceClient integrationServiceClient, ModelStoreClient modelStoreClient, ConnectorFactory connectorFactory) {
        this.integrationServiceClient = integrationServiceClient;
        this.modelStoreClient = modelStoreClient;
        this.connectorFactory = connectorFactory;
    }

    public List<String> query(AuthInfo authInfo, UniversalQuery query) {
        List<String> objectList = Collections.singletonList(query.getObject());
        List<String> fieldsList = QueryUtils.getFieldsForQuery(query);

        List<String> applicationsInvolved = modelStoreClient.getApplicationsInvolved(authInfo, objectList, fieldsList);

        List<IntegrationServiceClient.Application> integratedApplications = integrationServiceClient.getIntegratedApplications(authInfo, applicationsInvolved);

        List<String> responses = new ArrayList<>();

        integratedApplications.forEach(app -> responses.add(doQuery(authInfo, query, app)));

        return responses;
    }

    private String doQuery(AuthInfo authInfo, UniversalQuery query, IntegrationServiceClient.Application application) {
        Connector connector = connectorFactory.getConnector(integrationServiceClient, modelStoreClient, application);
        return connector.query(authInfo, query);
    }
}
