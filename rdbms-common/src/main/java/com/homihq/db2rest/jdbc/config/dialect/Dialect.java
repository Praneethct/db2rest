package com.homihq.db2rest.jdbc.config.dialect;


import com.homihq.db2rest.jdbc.config.model.DbColumn;
import com.homihq.db2rest.jdbc.config.model.DbTable;

import java.util.List;
import java.util.Map;

public interface Dialect {

    boolean isSupportedDb(String productName, int majorVersion);

    default boolean supportBatchReturnKeys() {
        return true;
    }
    default boolean supportAlias() {
        return true;
    }


    default String getProductFamily() {
        return "";
    }

    default int getMajorVersion() {
        return -1;
    }


    void processTypes(DbTable table, List<String> insertableColumns, Map<String,Object> data);

    String renderTableName(DbTable table, boolean containsWhere, boolean deleteOp);

    String renderTableNameWithoutAlias(DbTable table);

    default String getAliasedName(DbColumn dbColumn, boolean deleteOp) {
        return dbColumn.tableAlias() + "."+ dbColumn.name();
    }

    default String getAliasedNameParam(DbColumn dbColumn, boolean deleteOp) {
        return dbColumn.tableAlias() + "_"+ dbColumn.name();
    }



    default List<Object> parseListValues(List<String> values, Class type) {
        return
                values.stream()
                        .map(v -> processValue(v, type, null))
                        .toList();
    }

    //TODO use Spring converter
    default Object processValue(String value, Class<?> type, String format) {
        System.out.println("type " + type);
        if (String.class == type) {
            //return "'" + value + "'";
            return value;
        }
        else if (Boolean.class == type || boolean.class == type) {
            Boolean aBoolean = Boolean.valueOf(value);
            return aBoolean ? "1" : "0";
        }
        else if (Integer.class == type || int.class == type) {
            return Integer.valueOf(value);
        }
        else if (Long.class == type || long.class == type) {
            return Long.valueOf(value);
        }
        else if (Short.class == type || short.class == type) {
            return Short.valueOf(value);
        }
        else {
            return value;
        }

    }
}
