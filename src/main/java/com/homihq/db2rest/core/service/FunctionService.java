package com.homihq.db2rest.core.service;


import org.springframework.jdbc.core.simple.SimpleJdbcCall;

import java.util.Map;

public interface FunctionService extends SubRoutine {
    SimpleJdbcCall getSimpleJdbcCall(String subRoutineName);
    Map<String, Object> execute(String subRoutineName, Map<String, Object> inParams);
}
