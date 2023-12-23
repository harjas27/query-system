import com.google.protobuf.util.JsonFormat;
import connector.ConnectorFactory;
import connector.Constants;
import connector.HubSpotConnector;
import connector.SalesforceConnector;
import ext.IntegrationServiceClient;
import ext.ModelStoreClient;
import org.junit.Test;
import org.mockito.internal.util.reflection.BeanPropertySetter;
import org.mockito.internal.util.reflection.FieldInitializer;
import org.mockito.internal.util.reflection.InstanceField;
import query.UniversalQuery;
import service.QueryServiceImpl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.internal.util.reflection.FieldInitializer.*;

public class QueryTest {

    @Test
    public void sampleQueryTest() throws IOException, URISyntaxException, NoSuchFieldException {
        // test preparation - START
        String queryJson = readJson("query.json");
        UniversalQuery.Builder queryBuilder = UniversalQuery.newBuilder();
        JsonFormat.parser().merge(queryJson, queryBuilder);
        UniversalQuery query = queryBuilder.build();

        String hsResponseJson = readJson("hs_response.json");
        String sfResponseJson = readJson("sf_response.json");

        IntegrationServiceClient.Application sfApplication = new IntegrationServiceClient.Application("id1", "https://MyDomainName.my.salesforce.com/services/data/v59.0/", "SALESFORCE");
        IntegrationServiceClient.Application hsApplication = new IntegrationServiceClient.Application("id2", "https://MyDomainName.my.hubspot.com/crm/v3/", "HUBSPOT");

        IntegrationServiceClient integrationServiceClient = mock(IntegrationServiceClient.class);
        when(integrationServiceClient.getIntegratedApplications(null, List.of("CRM")))
                .thenReturn(List.of(sfApplication, hsApplication));
        when(integrationServiceClient.getAuthenticationCredentials(null, sfApplication.getId())).thenReturn("sf_auth");
        when(integrationServiceClient.getAuthenticationCredentials(null, hsApplication.getId())).thenReturn("hs_auth");

        ModelStoreClient modelStoreClient = mock(ModelStoreClient.class);
        when(modelStoreClient.getApplicationsInvolved(null, List.of("Account"), List.of("owner", "name", "description", "number_of_employees")))
                .thenReturn(List.of("CRM"));
        when(modelStoreClient.getObjectNameForApplication(null, "Account", Constants.SALESFORCE_APP_TYPE))
                .thenReturn("sf_Account");
        when(modelStoreClient.getObjectNameForApplication(null, "Account", Constants.HUBSPOT_APP_TYPE))
                .thenReturn("sf_Account");

        HashMap<String, String> sfFields = new HashMap<>();
        sfFields.put("owner", "sf_owner");
        sfFields.put("name", "sf_name");
        sfFields.put("description", "sf_description");
        sfFields.put("number_of_employees", "sf_number_of_employees");
        HashMap<String, String> hsFields = new HashMap<>();
        hsFields.put("owner", "hs_owner");
        hsFields.put("name", "hs_name");
        hsFields.put("description", "hs_description");
        hsFields.put("number_of_employees", "hs_number_of_employees");
        when(modelStoreClient.getFieldNamesForApplication(null, List.of("owner", "name", "description", "number_of_employees"), Constants.SALESFORCE_APP_TYPE))
                .thenReturn(sfFields);
        when(modelStoreClient.getFieldNamesForApplication(null, List.of("owner", "name", "description", "number_of_employees"), Constants.HUBSPOT_APP_TYPE))
                .thenReturn(hsFields);

        SalesforceConnector salesforceConnector = mock(SalesforceConnector.class);
        when(salesforceConnector.doQuery(any(), any(), anyString())).thenReturn(sfResponseJson);
        when(salesforceConnector.query(null, query)).thenCallRealMethod();
        new InstanceField(SalesforceConnector.class.getDeclaredField("application"), salesforceConnector).set(sfApplication);
        new InstanceField(SalesforceConnector.class.getDeclaredField("integrationServiceClient"), salesforceConnector).set(integrationServiceClient);
        new InstanceField(SalesforceConnector.class.getDeclaredField("modelStoreClient"), salesforceConnector).set(modelStoreClient);

        HubSpotConnector hubSpotConnector = mock(HubSpotConnector.class);
        when(hubSpotConnector.doQuery(any(), any(), anyString())).thenReturn(hsResponseJson);
        when(hubSpotConnector.query(null, query)).thenCallRealMethod();
        new InstanceField(HubSpotConnector.class.getDeclaredField("application"), hubSpotConnector).set(hsApplication);
        new InstanceField(HubSpotConnector.class.getDeclaredField("integrationServiceClient"), hubSpotConnector).set(integrationServiceClient);
        new InstanceField(HubSpotConnector.class.getDeclaredField("modelStoreClient"), hubSpotConnector).set(modelStoreClient);

        ConnectorFactory connectorFactory = mock(ConnectorFactory.class);
        when(connectorFactory.getConnector(integrationServiceClient, modelStoreClient, sfApplication)).thenReturn(salesforceConnector);
        when(connectorFactory.getConnector(integrationServiceClient, modelStoreClient, hsApplication)).thenReturn(hubSpotConnector);

        // test preparation - END

        QueryServiceImpl queryService = new QueryServiceImpl(integrationServiceClient, modelStoreClient, connectorFactory);
        List<String> response = queryService.query(null, query);
        System.out.println(response);
    }

    private String readJson(String path) throws URISyntaxException, IOException {
        try {
            ClassLoader loader = ClassLoader.getSystemClassLoader();
            String json = Files.lines(Paths.get(loader.getResource(path).toURI()))
                    .parallel()
                    .collect(Collectors.joining());
            return json;
        } catch (NullPointerException e) {
            return null;
        }

    }
}
