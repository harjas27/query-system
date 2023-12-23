package rest;

import auth.AuthInfo;
import auth.AuthInfoHolder;
import com.google.gson.JsonArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import query.UniversalQuery;
import service.QueryService;

import java.io.IOException;
import java.util.List;

import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@RestController
@RequestMapping("/query-system/api/v1/")
public class QueryServiceController {
    private static final Logger LOGGER = LoggerFactory.getLogger(QueryServiceController.class.getName());
    private final QueryService queryService;

    public QueryServiceController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping(produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<String> query(@RequestBody UniversalQuery query) {
        AuthInfo authInfo = AuthInfoHolder.getAuthInfo();
        List<String> list = queryService.query(authInfo, query);
        JsonArray jsonArray = new JsonArray();
        list.forEach(jsonArray::add);
        return ResponseEntity.ok(jsonArray.toString());
    }
}
