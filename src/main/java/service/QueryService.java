package service;

import auth.AuthInfo;
import query.UniversalQuery;

import java.util.List;

public interface QueryService {

    List<String> query(AuthInfo authInfo, UniversalQuery query);
}
