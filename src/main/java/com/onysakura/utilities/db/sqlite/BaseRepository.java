package com.onysakura.utilities.db.sqlite;

import com.onysakura.utilities.utils.CustomLogger;
import com.onysakura.utilities.utils.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class BaseRepository<T> {

    private static final CustomLogger.Log LOG = CustomLogger.getLogger(BaseRepository.class);

    private final String tableName;
    private final String[] fieldNames;
    private final Class<T> modelClass;

    @SuppressWarnings({"unchecked"})
    public Class<T> getModelClass() {
        Type type = getClass().getGenericSuperclass();
        if (type instanceof ParameterizedType) {
            Type[] pType = ((ParameterizedType) type).getActualTypeArguments();
            return (Class<T>) pType[0];
        } else {
            return null;
        }
    }

    public BaseRepository(Class<T> modelClass) {
        if (modelClass == null) {
            modelClass = getModelClass();
        }
        this.modelClass = modelClass;
        Field[] fields = modelClass.getDeclaredFields();
        fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = StringUtils.humpToUnderline(fields[i].getName());
        }
        TableName tableName = modelClass.getAnnotation(TableName.class);
        this.tableName = tableName.value();
        createTable();
    }

    public void createTable() {
        String sql = "CREATE TABLE IF NOT EXISTS $TABLE_NAME ($CONTENT);";
        List<String> content = new ArrayList<>();
        for (String fieldName : fieldNames) {
            if ("ID".equalsIgnoreCase(fieldName)) {
                content.add("ID TEXT PRIMARY KEY NOT NULL");
            } else {
                content.add(fieldName + " TEXT");
            }
        }
        sql = sql.replace("$TABLE_NAME", this.tableName)
                .replace("$CONTENT", String.join(", ", content));
        LOG.debug("create table sql: " + sql);
        int i = SQLite.executeUpdate(sql);
        if (i >= 0) {
            LOG.debug("create table " + this.tableName + " successfully");
        } else {
            LOG.warn("create table fail");
        }
    }

    public List<T> selectAll() {
        String sql = "SELECT * FROM $TABLE_NAME;";
        sql = sql.replace("$TABLE_NAME", tableName);
        LOG.debug("select sql: " + sql);
        ResultSet resultSet = SQLite.executeQuery(sql);
        return getResultList(resultSet, modelClass);
    }

    public List<T> selectAll(LinkedHashMap<String, SQLite.Sort> sort) {
        if (sort == null || sort.isEmpty()) {
            return selectAll();
        }
        String sql = "SELECT * FROM $TABLE_NAME ORDER BY $ORDER;";
        ArrayList<String> order = new ArrayList<>();
        for (Map.Entry<String, SQLite.Sort> sortEntry : sort.entrySet()) {
            order.add(StringUtils.humpToUnderline(sortEntry.getKey()) + " " + sortEntry.getValue().toString());
        }
        sql = sql.replace("$TABLE_NAME", tableName)
                .replace("$ORDER", String.join(", ", order));
        LOG.debug("select sql: " + sql);
        ResultSet resultSet = SQLite.executeQuery(sql);
        return getResultList(resultSet, modelClass);
    }

    public List<T> select(T model) {
        List<String> queries = new ArrayList<>();
        boolean hasQueries = false;
        for (String fieldName : fieldNames) {
            try {
                Method method = modelClass.getDeclaredMethod(generateGetMethodName(fieldName));
                Object invoke = method.invoke(model);
                if (invoke != null) {
                    hasQueries = true;
                    queries.add(fieldName + " = '" + SQLite.escape(invoke.toString()) + "'");
                }
            } catch (ReflectiveOperationException e) {
                LOG.warn(e, "select fail");
            }
        }
        if (!hasQueries) {
            return selectAll();
        }
        String sql = "SELECT * FROM $TABLE_NAME WHERE $QUERIES;";
        sql = sql.replace("$TABLE_NAME", tableName)
                .replace("$QUERIES", String.join(", ", queries));
        LOG.debug("select sql: " + sql);
        ResultSet resultSet = SQLite.executeQuery(sql);
        return getResultList(resultSet, modelClass);
    }

    public List<T> select(T model, LinkedHashMap<String, SQLite.Sort> sort) {
        if (sort == null || sort.isEmpty()) {
            return select(model);
        }
        List<String> queries = new ArrayList<>();
        boolean hasQueries = false;
        for (String fieldName : fieldNames) {
            try {
                Method method = modelClass.getDeclaredMethod(generateGetMethodName(fieldName));
                Object invoke = method.invoke(model);
                if (invoke != null) {
                    hasQueries = true;
                    queries.add(fieldName + " = '" + SQLite.escape(invoke.toString()) + "'");
                }
            } catch (ReflectiveOperationException e) {
                LOG.warn(e, "select fail");
            }
        }
        if (!hasQueries) {
            return selectAll(sort);
        }
        String sql = "SELECT * FROM $TABLE_NAME WHERE $QUERIES ORDER BY $ORDER;";
        ArrayList<String> order = new ArrayList<>();
        for (Map.Entry<String, SQLite.Sort> sortEntry : sort.entrySet()) {
            order.add(StringUtils.humpToUnderline(sortEntry.getKey()) + " " + sortEntry.getValue().toString());
        }
        sql = sql.replace("$TABLE_NAME", tableName)
                .replace("$QUERIES", String.join(", ", queries))
                .replace("$ORDER", String.join(", ", order));
        LOG.debug("select sql: " + sql);
        ResultSet resultSet = SQLite.executeQuery(sql);
        return getResultList(resultSet, modelClass);
    }

    public T insert(T t) {
        String info = null;
        try {
            Method toString = t.getClass().getMethod("toString");
            Object invoke = toString.invoke(t);
            if (invoke != null) {
                info = invoke.toString();
            }
        } catch (Exception ignored) {
        }
        LOG.debug("insert " + (info == null ? t.getClass().getName() : info));
        String sql = "INSERT INTO $TABLE_NAME ($FIELDS) VALUES ($VALUES);";
        List<String> fields = new ArrayList<>();
        List<String> values = new ArrayList<>();
        try {
            Method setId = modelClass.getDeclaredMethod("setId", String.class);
            setId.invoke(t, StringUtils.getNextId());
            for (String fieldName : fieldNames) {
                fields.add(fieldName);
                String methodName = generateGetMethodName(fieldName);
                Method method = modelClass.getMethod(methodName);
                Object invoke = method.invoke(t);
                if (invoke == null) {
                    values.add("NULL");
                } else {
                    values.add("'" + SQLite.escape(invoke.toString()) + "'");
                }
            }
        } catch (Exception e) {
            LOG.warn(e, "insert fail");
        }
        sql = sql.replace("$TABLE_NAME", tableName)
                .replace("$FIELDS", String.join(", ", fields))
                .replace("$VALUES", String.join(", ", values));
        LOG.debug("insert sql: " + sql);
        int update = SQLite.executeUpdate(sql);
        if (update >= 0) {
            return t;
        } else {
            return null;
        }
    }

    public T update(T t) {
        String info = null;
        try {
            Method toString = t.getClass().getMethod("toString");
            Object invoke = toString.invoke(t);
            if (invoke != null) {
                info = invoke.toString();
            }
        } catch (Exception ignored) {
        }
        LOG.debug("update " + (info == null ? t.getClass().getName() : info));
        String sql = "UPDATE $TABLE_NAME SET $VALUES WHERE ID = $ID;";
        List<String> values = new ArrayList<>();
        String id = null;
        try {
            Method setId = modelClass.getDeclaredMethod("getId");
            Object idObject = setId.invoke(t);
            if (idObject != null) {
                id = idObject.toString();
                for (String fieldName : fieldNames) {
                    String methodName = generateGetMethodName(fieldName);
                    Method method = modelClass.getMethod(methodName);
                    Object invoke = method.invoke(t);
                    String value;
                    if (invoke != null && !"ID".equalsIgnoreCase(fieldName)) {
                        value = invoke.toString();
                        values.add(fieldName + " = '" + SQLite.escape(value) + "'");
                    }
                }
            }
        } catch (Exception e) {
            LOG.warn(e, "execute sql fail");
        }
        if (id != null) {
            sql = sql.replace("$TABLE_NAME", tableName)
                    .replace("$ID", id)
                    .replace("$VALUES", String.join(", ", values));
            LOG.debug("update sql: " + sql);
            int update = SQLite.executeUpdate(sql);
            if (update >= 0) {
                return t;
            } else {
                return null;
            }
        } else {
            LOG.warn("update fail, no id value");
            return null;
        }
    }

    public int delete(String id) {
        String sql = "DELETE FROM $TABLE_NAME WHERE ID = $ID;";
        sql = sql.replace("$TABLE_NAME", tableName)
                .replace("$ID", id);
        LOG.debug("delete sql: " + sql);
        return SQLite.executeUpdate(sql);
    }

    private List<T> getResultList(ResultSet resultSet, Class<T> modelClass) {
        List<T> list = new ArrayList<>();
        if (resultSet != null) {
            try {
                while (resultSet.next()) {
                    T t = modelClass.getDeclaredConstructor().newInstance();
                    for (String fieldName : fieldNames) {
                        String resultString = resultSet.getString(fieldName);
                        String setMethodName = generateSetMethodName(fieldName);
                        Method method = modelClass.getDeclaredMethod(setMethodName, String.class);
                        method.invoke(t, resultString);
                    }
                    list.add(t);
                }
            } catch (SQLException | ReflectiveOperationException e) {
                LOG.warn(e, "get result list fail");
            }
        }
        LOG.debug("select result size: " + list.size());
        return list;
    }

    private String generateGetMethodName(String fieldName) {
        String name = StringUtils.underlineToHump(fieldName);
        char c = name.charAt(0);
        String firstChar = String.valueOf(c);
        return "get" + firstChar.toUpperCase() + name.substring(1);
    }

    private String generateSetMethodName(String fieldName) {
        String name = StringUtils.underlineToHump(fieldName);
        char c = name.charAt(0);
        String firstChar = String.valueOf(c);
        return "set" + firstChar.toUpperCase() + name.substring(1);
    }
}
