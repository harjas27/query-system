package connector;

import auth.AuthInfo;
import com.google.common.annotations.VisibleForTesting;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import ext.IntegrationServiceClient;
import ext.ModelStoreClient;
import query.UniversalQuery;
import utils.QueryUtils;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class SalesforceConnector extends AbstractConnector {
    private final IntegrationServiceClient integrationServiceClient;
    private final ModelStoreClient modelStoreClient;
    private final IntegrationServiceClient.Application application;

    public SalesforceConnector(IntegrationServiceClient integrationServiceClient, ModelStoreClient modelStoreClient, IntegrationServiceClient.Application application) {
        this.integrationServiceClient = integrationServiceClient;
        this.modelStoreClient = modelStoreClient;
        this.application = application;
    }

    public String query(AuthInfo authInfo, UniversalQuery query) {
        String urlString = getURLString(authInfo, query);
        String req = prepareQuery(authInfo, query);
        String authToken = getAuthToken(authInfo);
        String response = doQuery(urlString, req, authToken);
        return updateFieldsInResponse(authInfo, response, query);
    }
    public String doQuery(String urlString, String req, String authToken) {
        try {

            URL url = new URL(urlString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setDoOutput(true);


            byte[] postData = req.getBytes(StandardCharsets.UTF_8);

            connection.setRequestProperty("Content-Length", String.valueOf(postData.length));


            String authHeaderValue = "Bearer " + authToken;
            connection.setRequestProperty("Authorization", authHeaderValue);


            try (DataOutputStream wr = new DataOutputStream(connection.getOutputStream())) {
                wr.write(postData);
            }

            int responseCode = connection.getResponseCode();
            System.out.println("Response Code: " + responseCode);

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder response = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            System.out.println("Response: " + response.toString());

            connection.disconnect();
            return response.toString();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    private String prepareQuery(AuthInfo authInfo, UniversalQuery query) {
        Map<String, String> fieldNames = modelStoreClient.getFieldNamesForApplication(authInfo, QueryUtils.getFieldsForQuery(query), application.getKind());
        JsonObject reqJson = new JsonObject();
        List<String> updatedFields = query.getFieldsList().stream().map(f -> fieldNames.getOrDefault(f, f)).collect(Collectors.toList());
        JsonArray fields = new JsonArray();
        updatedFields.forEach(fields::add);
        reqJson.add("properties", fields);
        reqJson.add("filter", new JsonPrimitive(QueryUtils.convertToLogicalExpression(query.getFilter())));
        return reqJson.toString();
    }

    private String getURLString(AuthInfo authInfo, UniversalQuery query) {
        String objectName = modelStoreClient.getObjectNameForApplication(authInfo, query.getObject(), application.getKind());
        return application.getEndpoint() + "/sobjects" + objectName;
    }

    private String getAuthToken(AuthInfo authInfo) {
        return integrationServiceClient.getAuthenticationCredentials(authInfo, application.getId());
    }

    private String updateFieldsInResponse(AuthInfo authInfo, String response, UniversalQuery query) {
        Map<String, String> fieldNames = modelStoreClient.getFieldNamesForApplication(authInfo, QueryUtils.getFieldsForQuery(query), application.getKind());
        Map<String, String> reversedFieldsMap = fieldNames.entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getValue, Map.Entry::getKey));
        JsonArray responseJson = new Gson().fromJson(response, JsonArray.class);
        JsonArray updatedJsonArray = new JsonArray();
        for (JsonElement object : responseJson) {
            JsonObject asJsonObject = object.getAsJsonObject();
            JsonObject updatedObject = new JsonObject();
            for (Map.Entry<String, JsonElement> stringJsonElementEntry : asJsonObject.entrySet()) {
                updatedObject.add(reversedFieldsMap.getOrDefault(stringJsonElementEntry.getKey(), stringJsonElementEntry.getKey()), stringJsonElementEntry.getValue());
            }
            updatedJsonArray.add(updatedObject);
        }
        return updatedJsonArray.toString();
    }
}
