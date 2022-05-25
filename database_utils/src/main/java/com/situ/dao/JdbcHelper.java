/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.pool.DruidPooledConnection;
import com.google.gson.JsonObject;
import com.situ.config.JdbcSource;
import com.situ.entity.bo.JdbcField;
import com.situ.entity.bo.TableSetting;
import com.situ.entity.enumeration.DatabaseSetting;
import com.situ.tools.*;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * JDBC数据库管理类 <p> 传入参数格式 select * from table where a = :a and b = :b and c in (:c) <p> in操作，传入的参数值对象使用ArrayList
 *
 * @author 司徒彬
 * @date 2017 -01-04 15:47
 */
@Repository
@Slf4j
public class JdbcHelper {


    /**
     * The Druid pool.
     */
    @Autowired
    DruidPool druidPool;

    private static final Map<String, Execute> EXECUTE_SOURCES = new HashMap<>(DruidPool.DEFAULT_SIZE);

    /**
     * Get execute.
     *
     * @return the execute
     * @author ErebusST
     * @since 2022 -01-07 15:39:01
     */
    public Execute get() {
        return get(DruidPool.DEFAULT);
    }

    public Execute get(DatabaseSetting databases) {
        String database = databases.getDatabase();
        return get(database);
    }

    /**
     * Get execute.
     *
     * @param key the key
     * @return the execute
     * @author ErebusST
     * @since 2022 -01-07 15:39:01
     */
    public Execute get(String key) {
        if (StringUtils.equalsIgnoreCase(key, DruidPool.DEFAULT)) {
            key = DruidPool.DATA_SOURCE_SETTING.keySet().stream().findFirst().get();
        }
        if (!EXECUTE_SOURCES.containsKey(key)) {
            JdbcSource.Source source = DruidPool.DATA_SOURCE_SETTING.get(key);
            Execute execute = new Execute();
            execute.setDataSource(source.getDruidDataSource());
            execute.setSchema(source.getSchema());
            EXECUTE_SOURCES.put(key, execute);
        }
        return EXECUTE_SOURCES.get(key);
    }

    /**
     * The type Execute.
     */
    public class Execute {

        /**
         * The Data source.
         */
        @Setter
        DruidDataSource dataSource;

        @Setter
        private String schema;


        private final String regex = ":[a-zA-Z]+\\w*";

        /**
         * Rollback .
         *
         * @param connection the connection
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        @SneakyThrows
        public void rollback(DruidPooledConnection connection) {
            if (connection != null) {
                connection.rollback();
            }
        }

        /**
         * Commit .
         *
         * @param connection the connection
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        @SneakyThrows
        public void commit(DruidPooledConnection connection) {
            if (connection != null) {
                connection.commit();
            }
        }

        /**
         * Close .
         *
         * @param connection the connection
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        public void close(DruidPooledConnection connection) {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
            }
        }

        /**
         * Get connection druid pooled connection.
         *
         * @return the connection
         * @throws SQLException the sql exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        public DruidPooledConnection getConnection() throws SQLException {
            return getConnection(true);
        }


        /**
         * Gets connection. 使用事务时调用
         *
         * @return the connection
         * @throws Exception the exception
         */
        private DruidPooledConnection getConnection(boolean transaction) throws SQLException {
            DruidPooledConnection connection = dataSource.getConnection();
            if (transaction) {
                connection.setAutoCommit(false);
                connection.setTransactionIsolation(DruidPooledConnection.TRANSACTION_REPEATABLE_READ);
            } else {
                connection.setAutoCommit(true);
            }
            return connection;
        }


        /**
         * 用于执行语句（eg：insert语句，update语句，delete语句）
         *
         * @param sql the sql
         * @return the int
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        public int executeNonQuery(String sql) throws Exception {
            try {
                return executeNonQuery(sql, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用于执行语句（eg：insert语句，update语句，delete语句）
         *
         * @param connection the connection
         * @param sql        the sql
         * @return the int
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        public int executeNonQuery(DruidPooledConnection connection, String sql) throws Exception {
            try {
                return executeNonQuery(connection, sql, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用于执行语句（eg：insert语句，update语句，delete语句）
         *
         * @param sql        the sql
         * @param parameters the parameters
         * @return the int
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        public int executeNonQuery(String sql, Map<String, Object> parameters) throws Exception {
            DruidPooledConnection connection = null;
            try {
                connection = getConnection(false);
                return executeNonQuery(connection, sql, parameters);
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, null, connection);
            }
        }

        /**
         * 用于执行语句（eg：insert语句，update语句，delete语句）
         *
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the int
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:01
         */
        public int executeNonQuery(DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            PreparedStatement preparedStatement = null;
            try {
                preparedStatement = prepareCommand(connection, sql, parameters);
                int flag = preparedStatement.executeUpdate();
                return flag;
            } catch (Exception ex) {
                log.error("sql_error:" + sql, ex);
                throw ex;
            } finally {
                finallyExecute(null, preparedStatement, null);
            }
        }

        /**
         * 批量执行sql（eg：insert语句，update语句，delete语句）
         *
         * @param sql            the sql
         * @param parametersList the parameters list
         * @return the int
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public int executeBatch(String sql, List<Map<String, Object>> parametersList) throws Exception {
            DruidPooledConnection connection = null;
            try {
                connection = getConnection(false);
                return executeBatch(connection, sql, parametersList);
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, null, connection);
            }
        }

        /**
         * 批量执行sql（eg：insert语句，update语句，delete语句
         *
         * @param connection     the connection
         * @param sql            the sql
         * @param parametersList the parameters list
         * @return the int
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public int executeBatch(DruidPooledConnection connection, String sql, List<Map<String, Object>> parametersList) throws Exception {
            PreparedStatement preparedStatement = null;
            try {

                Map<String, Object> tempParameters = parametersList.size() == 0 ? null : parametersList.get(0);
                preparedStatement = prepareSqlBatch(connection, sql, tempParameters);
                for (Map<String, Object> parameters : parametersList) {
                    prepareParameters(preparedStatement, sql, parameters);
                    preparedStatement.addBatch();
                }
                int[] flag = preparedStatement.executeBatch();

                preparedStatement.clearBatch();
                return Arrays.stream(flag).sum();
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, preparedStatement, null);
            }
        }

        /**
         * Find first t.
         *
         * @param <T>   the type parameter
         * @param clazz the clazz
         * @return the t
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public <T> T findFirst(Class<T> clazz) throws Exception {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String fieldsString = FieldUtils.getFieldJoinStringForSelect(setting);
            String schema = setting.getSchema();
            String name = setting.getTable();
            if (ObjectUtils.isNotEmpty(schema)) {
                name = schema.concat(".").concat(name);
            }
            String sql = "select " + fieldsString + " from " + name + " limit 1";
            Map<String, Object> first = findFirst(sql);
            return DataSwitch.convertMapObjToEntity(clazz, first);
        }

        /**
         * Find first t.
         *
         * @param <T>        the type parameter
         * @param clazz      the clazz
         * @param sql        the sql
         * @param parameters the parameters
         * @return the t
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public <T> T findFirst(Class<T> clazz, String sql, Map<String, Object> parameters) throws Exception {
            Map<String, Object> first = findFirst(sql, parameters);
            return DataSwitch.convertMapObjToEntity(clazz, first);
        }

        /**
         * 用户查询单个对象
         *
         * @param sql the sql
         * @return the map
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public Map<String, Object> findFirst(String sql) throws Exception {
            try {
                return findFirst(sql, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用户查询单个对象
         *
         * @param sql        the sql
         * @param parameters the parameters
         * @return the map
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public Map<String, Object> findFirst(String sql, Map<String, Object> parameters) throws Exception {
            DruidPooledConnection connection = null;
            try {
                connection = getConnection(false);
                return findFirst(connection, sql, parameters);
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, null, connection);
            }
        }

        /**
         * 用户查询单个对象
         *
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the map
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public Map<String, Object> findFirst(DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = prepareCommand(connection, sql, parameters);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    Map<String, Object> map = doCreateRow(resultSet);
                    return map;
                } else {
                    return null;
                }
            } catch (Exception ex) {
                log.error("sql_error:" + sql, ex);
                throw ex;
            } finally {
                finallyExecute(resultSet, preparedStatement, null);
            }
        }

        /**
         * 用户查询对象集合 （eg：selete * from table）
         *
         * @param connection the connection
         * @param sql        the sql
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public List<Map<String, Object>> findList(DruidPooledConnection connection, String sql) throws Exception {
            try {
                return findList(connection, sql, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用户查询对象集合 （eg：selete * from table）
         *
         * @param sql the sql
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public List<Map<String, Object>> findList(String sql) throws Exception {
            try {
                return findList(sql, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用户查询对象集合 （eg：selete * from table）
         *
         * @param sql        the sql
         * @param parameters the parameters
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:02
         */
        public List<Map<String, Object>> findList(String sql, Map<String, Object> parameters) throws Exception {
            DruidPooledConnection connection = null;
            try {
                connection = getConnection(false);
                return findList(connection, sql, parameters);
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, null, connection);
            }
        }

        /**
         * 用户查询对象集合 （eg：selete * from table）
         *
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public List<Map<String, Object>> findList(DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = prepareCommand(connection, sql, parameters);
                resultSet = preparedStatement.executeQuery();
                List<Map<String, Object>> resultMap = getMapList(resultSet);
                return resultMap;
            } catch (Exception ex) {
                log.error("sql_error:" + sql, ex);
                throw ex;

            } finally {
                finallyExecute(resultSet, preparedStatement, null);
            }
        }

        /**
         * 用户查询对象集合 （eg：selete * from table） <p> 返回list<T>
         *
         * @param <T>   the type parameter
         * @param clazz the clazz
         * @param sql   the sql
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public <T> List<T> findList(Class<T> clazz, String sql) throws Exception {
            try {
                return findList(clazz, sql, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        public <T> List<T> findList(Class<T> clazz) throws Exception {
            return findList(clazz, new HashedMap(1));
        }

        /**
         * Find list list.
         *
         * @param <T>        the type parameter
         * @param clazz      the clazz
         * @param parameters the parameters
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public <T> List<T> findList(Class<T> clazz, Map<String, Object> parameters) throws Exception {
            if (parameters.size() == 0) {
                parameters = null;
            }
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String fields = FieldUtils.getFieldJoinStringForSelect(setting);

            String schema = setting.getSchema();
            String table = setting.getTable();
            if (ObjectUtils.isNotEmpty(schema)) {
                table = schema.concat(".").concat(table);
            }

            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(fields);
            sql.append(" FROM ").append(table);
            if (ObjectUtils.isNotNull(parameters)) {
                String where = parameters.entrySet()
                        .stream()
                        .map(entry -> {
                            String key = entry.getKey();
                            return key + "=:" + key;
                        })
                        .collect(Collectors.joining(" AND "));
                sql.append(" WHERE ").append(where);
            }
            return findList(clazz, sql.toString(), parameters);
        }

        /**
         * 用户查询对象集合 （eg：selete * from table） <p> 返回list<T>
         *
         * @param <T>        the type parameter
         * @param clazz      the clazz
         * @param sql        the sql
         * @param parameters the parameters
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public <T> List<T> findList(Class<T> clazz, String sql, Map<String, Object> parameters) throws Exception {
            try {
                DruidPooledConnection connection = null;
                try {
                    connection = getConnection(false);
                    return findList(clazz, connection, sql, parameters);
                } catch (Exception ex) {
                    throw ex;
                } finally {
                    finallyExecute(null, null, connection);
                }
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用户查询对象集合 （eg：selete * from table） <p> 返回list<T>
         *
         * @param <T>        the type parameter
         * @param clazz      the clazz
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the list
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public <T> List<T> findList(Class<T> clazz, DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = prepareCommand(connection, sql, parameters);
                resultSet = preparedStatement.executeQuery();
                List<T> resultMap = getMapList(resultSet, clazz);
                return resultMap;
            } catch (Exception ex) {
                log.error(sql, ex);
                throw ex;
            } finally {
                finallyExecute(resultSet, preparedStatement, null);
            }
        }

        /**
         * 用于获取单字段值语句，返回第一个字段
         *
         * @param sql the sql
         * @return the object
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public Object executeScalar(String sql) throws Exception {
            try {
                return executeScalar(sql, "");
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用于获取单字段值语句，返回第一个字段
         *
         * @param sql        the sql
         * @param parameters the parameters
         * @return the object
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public Object executeScalar(String sql, Map<String, Object> parameters) throws Exception {
            try {
                return executeScalar(sql, null, parameters);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用于获取单字段值语句（用数据库字段名指定字段）
         *
         * @param sql  the sql
         * @param name the name
         * @return the object
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public Object executeScalar(String sql, String name) throws Exception {
            try {
                return executeScalar(sql, name, null);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 用于获取单字段值语句（用数据库字段名指定字段）
         *
         * @param sql        the sql
         * @param name       the name
         * @param parameters the parameters
         * @return the object
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:03
         */
        public Object executeScalar(String sql, String name, Map<String, Object> parameters) throws Exception {
            DruidPooledConnection connection = null;
            try {
                connection = getConnection(false);
                return executeScalar(connection, sql, name, parameters);
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, null, connection);
            }
        }

        /**
         * 用于获取单字段值语句（用数据库字段名指定字段）
         *
         * @param connection the connection
         * @param sql        the sql
         * @param name       the name
         * @param parameters the parameters
         * @return the object
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public Object executeScalar(DruidPooledConnection connection, String sql, String name, Map<String, Object> parameters) throws Exception {
            PreparedStatement preparedStatement = null;
            ResultSet resultSet = null;
            try {
                preparedStatement = prepareCommand(connection, sql, parameters);
                resultSet = preparedStatement.executeQuery();
                if (resultSet.next()) {
                    if (StringUtils.isEmpty(name)) {
                        return resultSet.getObject(0);
                    } else {
                        return resultSet.getObject(name);
                    }

                } else {
                    return null;
                }
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(resultSet, preparedStatement, null);
            }
        }

        /**
         * Is exist boolean.
         *
         * @param tableName 表名，使用数据库实际表名
         * @param key       key为字段名（数据库中实际字段名）
         * @param value     value为要验证的值
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public boolean isExist(String tableName, String key, Object value) throws Exception {
            try {
                Map<String, Object> parameters = new HashedMap();
                parameters.put(key, value);
                return isExist(tableName, parameters);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Is exist boolean.
         *
         * @param tableName  表名，使用数据库实际表名
         * @param parameters 验证的参数，key为字段名（数据库中实际字段名），value为要验证的值
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public boolean isExist(String tableName, Map<String, Object> parameters) throws Exception {
            DruidPooledConnection connection = null;
            try {
                connection = getConnection(false);
                return isExist(connection, tableName, parameters);
            } catch (Exception ex) {
                throw ex;
            } finally {
                finallyExecute(null, null, connection);
            }
        }

        /**
         * Is exist boolean.
         *
         * @param connection the connection
         * @param tableName  the table name
         * @param parameters the parameters
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public boolean isExist(DruidPooledConnection connection, String tableName, Map<String, Object> parameters) throws Exception {
            try {
                if (tableName.trim().length() == 0) {
                    throw new Exception("tableName不能为空!");
                }
                long nullCount = parameters.keySet().stream().filter(key -> StringUtils.isEmpty(key) || StringUtils.isEmpty(key.trim())).count();
                if (nullCount > 0) {
                    throw new Exception("key不能为空!");
                }
                StringBuffer sbSql = new StringBuffer();
                sbSql.append(" SELECT COUNT(1) ");
                sbSql.append(" AS NUM FROM ");
                sbSql.append(tableName);
                sbSql.append(" WHERE 1 = 1 ");

                parameters.forEach((key, value) ->
                {
                    if (!StringUtils.isEmpty(key)) {
                        sbSql.append(" AND ");
                        sbSql.append(key);
                        sbSql.append("= :" + key);
                    }
                });
                int flag = Integer.parseInt(executeScalar(connection, sbSql.toString(), "NUM", parameters).toString());
                return flag > 0;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Prepare command prepared statement.
         *
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the prepared statement
         * @throws Exception the exception
         */
        private PreparedStatement prepareCommand(DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            try {
                if (ObjectUtils.isNotNull(parameters) && parameters.size() == 0) {
                    parameters = null;
                }
                PreparedStatement preparedStatement = prepareSql(connection, sql, parameters);
                return prepareParameters(preparedStatement, sql, parameters);
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Prepare command prepared statement.
         *
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the prepared statement
         * @throws Exception the exception
         */
        private PreparedStatement prepareSql(DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            try {
                sql = getSqlString(sql, parameters);
                PreparedStatement preparedStatement = connection.prepareStatement(sql);
                return preparedStatement;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Prepare command batch prepared statement.
         *
         * @param connection the connection
         * @param sql        the sql
         * @param parameters the parameters
         * @return the prepared statement
         * @throws Exception the exception
         */
        private PreparedStatement prepareSqlBatch(DruidPooledConnection connection, String sql, Map<String, Object> parameters) throws Exception {
            try {
                sql = getSqlString(sql, parameters);
                PreparedStatement preparedStatement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY);
                return preparedStatement;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Prepare command prepared statement.
         *
         * @param preparedStatement the prepared statement
         * @param sql               the sql
         * @param parameters        the parameters
         * @return the prepared statement
         * @throws Exception the exception
         */
        private PreparedStatement prepareParameters(PreparedStatement preparedStatement, String sql, Map<String, Object> parameters) throws Exception {
            try {
                List<Object> parameterList = getSqlParameters(sql, parameters);

                //			for (Object parameter : parameterList)
                //			{
                //				int index = parameterList.indexOf(parameter) + 1;
                //
                //				preparedStatement.setObject(index, parameter);
                //			}
                for (int i = 0; i < parameterList.size(); i++) {
                    int index = i + 1;
                    Object parameter = parameterList.get(i);
                    preparedStatement.setObject(index, parameter);
                }
                return preparedStatement;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Gets sql string.
         *
         * @param sql the sql
         * @return the sql string
         */
        private String getSqlString(String sql, Map<String, Object> parameters) throws Exception {
            parameters = parameters == null ? new HashedMap() : parameters;
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(sql);
            List<String> errorList = new ArrayList<>();
            while (matcher.find()) {
                String parameter = matcher.group();
                parameter = parameter.substring(1);
                if (parameters.containsKey(parameter)) {

                    Object paramValue = parameters.get(parameter);
                    if (paramValue != null && paramValue.getClass().equals(ArrayList.class)) {
                        List<Object> valueList = (List<Object>) paramValue;
                        StringBuilder tempStringBuilder = new StringBuilder();
                        valueList.forEach(value -> tempStringBuilder.append(",?"));
                        String tempStr = tempStringBuilder.deleteCharAt(0).toString();
                        sql = sql.replaceFirst(regex, tempStr);
                    } else {
                        sql = sql.replaceFirst(regex, "?");
                    }
                } else {
                    errorList.add("未找到变量 [" + parameter + "] 的参数值!");
                }
            }
            if (errorList.size() > 0) {
                String errorMessage = StringUtils.getCombineString(errorList);
                throw new Exception(errorMessage);
            }
            //sql = sql.replaceAll(regex, "?");
            return sql;
        }

        /**
         * Gets sql parameters.
         *
         * @param sql        the sql
         * @param parameters the parameters
         * @return the sql parameters
         * @throws Exception the exception
         */
        private List<Object> getSqlParameters(String sql, Map<String, Object> parameters) throws Exception {
            try {
                parameters = parameters == null ? new HashedMap() : parameters;
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(sql);

                List<Object> parameterList = new ArrayList<>();

                while (matcher.find()) {
                    String parameter = matcher.group();
                    if (parameter.trim().length() != 0) {
                        parameter = parameter.substring(1);
                        Object paramValue = parameters.get(parameter);
                        if (paramValue != null && paramValue.getClass().equals(ArrayList.class)) {
                            ((ArrayList) paramValue).forEach(inValue ->
                            {
                                parameterList.add(inValue);
                            });
                        } else {
                            parameterList.add(paramValue);
                        }
                    }
                }
                return parameterList;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Gets map list.
         *
         * @param resultSet the result set
         * @return the map list
         * @throws SQLException the sql exception
         */
        private final List<Map<String, Object>> getMapList(ResultSet resultSet) throws SQLException {
            try {
                List<Map<String, Object>> mapList = new ArrayList<>();
                while (resultSet.next()) {
                    mapList.add(doCreateRow(resultSet));
                }
                return mapList;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * Gets map list.
         *
         * @param <T>       the type parameter
         * @param resultSet the result set
         * @param clazz     the clazz
         * @return the map list
         * @throws Exception the exception
         */
        private final <T> List<T> getMapList(ResultSet resultSet, Class<T> clazz) throws Exception {
            try {
                List<T> list = new ArrayList<>();
                while ((resultSet.next())) {
                    Map<String, Object> map = doCreateRow(resultSet);
                    T entity = DataSwitch.convertMapObjToEntity(clazz, map);
                    list.add(entity);
                }
                return list;
            } catch (Exception ex) {
                throw ex;
            }
        }

        /**
         * 将执行 SQL 语句的结果放在 Map 中
         *
         * @param resultSet 语句返回的结果集
         * @return map
         * @throws SQLException
         */
        private final Map<String, Object> doCreateRow(ResultSet resultSet) throws SQLException {
            try {
                Map<String, Object> map = new HashedMap();
                ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
                int count = resultSetMetaData.getColumnCount();
                for (int i = 0; i < count; i++) {
                    String label = resultSetMetaData.getColumnLabel(i + 1);
                    Object value = resultSet.getObject(i + 1);
                    map.put(label, value);
                }
                return map;
            } catch (SQLException e) {
                throw e;
            }

        }

        /**
         * Finally execute.
         *
         * @param resultSet         the result set
         * @param preparedStatement the prepared statement
         * @param connection        the connection
         * @throws Exception the exception
         */
        private void finallyExecute(ResultSet resultSet, PreparedStatement preparedStatement, DruidPooledConnection connection) throws Exception {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (Exception e) {
                throw e;
            }
        }

        /**
         * Find by id t.
         *
         * @param <T>   the type parameter
         * @param clazz the clazz
         * @param id    the id
         * @return the t
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <T> T findById(Class<T> clazz, Long id) throws Exception {
            if (ObjectUtils.isNull(id)) {
                return null;
            }
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            JdbcField primaryKey = setting.getPrimaryKey();
            String schema = setting.getSchema();
            String tableName = setting.getTable();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }
            String fields = FieldUtils.getFieldJoinStringForSelect(setting);
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(fields);
            sql.append(" FROM ")
                    .append(tableName)
                    .append(" WHERE ")
                    .append(primaryKey.getFieldInDb())
                    .append(" = :")
                    .append(primaryKey.getFieldInDb());
            Map<String, Object> parameters = new HashMap<>(1);
            parameters.put(primaryKey.getFieldInDb(), id);
            return findFirst(clazz, sql.toString(), parameters);
        }

        /**
         * Find first t.
         *
         * @param <T>        the type parameter
         * @param clazz      the clazz
         * @param parameters the parameters
         * @return the t
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <T> T findFirst(Class<T> clazz, Map<String, Object> parameters) throws Exception {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String schema = setting.getSchema();
            String tableName = setting.getTable();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }

            String fields = FieldUtils.getFieldJoinStringForSelect(setting);
            StringBuilder sql = new StringBuilder("SELECT ");
            sql.append(fields);
            sql.append(" FROM ")
                    .append(tableName);
            if (ObjectUtils.isNotNull(parameters)) {
                String where = parameters.entrySet().stream()
                        .map(entry -> {
                            String key = entry.getKey();
                            return key + "=:" + key;
                        })
                        .collect(Collectors.joining(" AND "));
                sql.append(" WHERE ").append(where);
            }
            return findFirst(clazz, sql.toString(), parameters);
        }


        /**
         * Insert boolean.
         *
         * @param <Entity> the type parameter
         * @param entity   the entity
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <Entity> boolean insert(Entity entity) throws Exception {
            return insert(entity, "", "");
        }

        /**
         * Insert boolean.
         *
         * @param <Entity>  the type parameter
         * @param entity    the entity
         * @param tableName the table name
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <Entity> boolean insert(Entity entity, String tableName) throws Exception {
            return insert(entity, "", tableName);
        }

        /**
         * Insert boolean.
         *
         * @param <Entity>  the type parameter
         * @param entity    the entity
         * @param schema    the schema
         * @param tableName the table name
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <Entity> boolean insert(Entity entity, String schema, String tableName) throws Exception {
            DruidPooledConnection connection = getConnection();
            try {
                boolean replace = insert(connection, entity, schema, tableName);
                connection.commit();
                return replace;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.close();
            }
        }

        /**
         * Insert boolean.
         *
         * @param <Entity>   the type parameter
         * @param connection the connection
         * @param entity     the entity
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <Entity> boolean insert(DruidPooledConnection connection, Entity entity) throws Exception {
            return insert(connection, entity, "", "");
        }

        /**
         * Insert boolean.
         *
         * @param <Entity>   the type parameter
         * @param connection the connection
         * @param entity     the entity
         * @param schema     the schema
         * @param tableName  the table name
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:04
         */
        public <Entity> boolean insert(DruidPooledConnection connection, Entity entity, String schema, String tableName) throws Exception {
            Class clazz = entity.getClass();
            TableSetting setting = FieldUtils.getEntityInfo(clazz);

            tableName = StringUtils.isNotEmpty(tableName) ? tableName : setting.getTable();
            schema = StringUtils.isNotEmpty(schema) ? schema : setting.getSchema();
            Map<String, Object> parameters = FieldUtils.getParameters(entity);
            JdbcField primaryKeySetting = setting.getPrimaryKey();

            String primaryId = primaryKeySetting.getField().getName();
            boolean autoGenerate = primaryKeySetting.isAutoGenerate();
            if (autoGenerate) {
                String primaryKeyInDb = primaryKeySetting.getFieldInDb();
                parameters.put(primaryKeyInDb, ReflectionUtils.getFieldValue(entity, primaryId));
            }
            if (parameters.size() == 0) {
                return false;
            }
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            if (StringUtils.isNotEmpty(schema)) {
                sql.append(schema).append(".");
            }
            sql.append(tableName);
            String insertKey = FieldUtils.getFieldJoinStringForInsertField(entity);
            String insertValue = FieldUtils.getFieldJoinStringForInsertParameter(entity);

            /**
             * INSERT INTO tb_residential_houses
             *     ( province, city, area, buildingName,address )
             * VALUES
             *     ( '河北', '唐山', '丰润', '建筑一' ,:address)
             */
            sql.append("(").append(insertKey).append(")");
            sql.append(" VALUES ");
            sql.append("(").append(insertValue).append(")");

            try {
                return executeNonQuery(connection, sql.toString(), parameters) > 0;
            } catch (Exception ex) {
                log.error(sql.toString());
                JsonObject jsonObject = DataSwitch.convertObjectToJsonObject(parameters);
                log.error(jsonObject.toString(), ex);

                throw ex;
            }
        }

        /**
         * Replace boolean.
         *
         * @param <Entity> the type parameter
         * @param entity   the entity
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:05
         */
        public <Entity> boolean replace(Entity entity) throws Exception {
            return replace(entity, null);
        }

        /**
         * Replace boolean.
         *
         * @param <Entity>  the type parameter
         * @param entity    the entity
         * @param tableName the table name
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:05
         */
        public <Entity> boolean replace(Entity entity, String tableName) throws Exception {
            DruidPooledConnection connection = getConnection();
            try {
                boolean replace = replace(connection, entity, tableName);
                connection.commit();
                return replace;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.close();
            }
        }

        /**
         * Replace boolean.
         *
         * @param <Entity>   the type parameter
         * @param connection the connection
         * @param entity     the entity
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:05
         */
        public <Entity> boolean replace(DruidPooledConnection connection, Entity entity) throws Exception {
            return replace(connection, entity, null);
        }

        /**
         * Replace boolean.
         *
         * @param <Entity>   the type parameter
         * @param connection the connection
         * @param entity     the entity
         * @param tableName  the table name
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:05
         */
        public <Entity> boolean replace(DruidPooledConnection connection, Entity entity, String tableName) throws Exception {
            Class<Entity> clazz = (Class<Entity>) entity.getClass();
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            tableName = StringUtils.isNotEmpty(tableName) ? tableName : setting.getTable();
            String schema = setting.getSchema();
            Map<String, Object> parameters = FieldUtils.getParameters(entity);

            JdbcField primaryKeySetting = setting.getPrimaryKey();

            boolean autoGenerate = primaryKeySetting.isAutoGenerate();
            if (autoGenerate) {
                String primaryId = primaryKeySetting.getField().getName();
                String primaryKeyInDb = primaryKeySetting.getFieldInDb();
                parameters.put(primaryKeyInDb, ReflectionUtils.getFieldValue(entity, primaryId));
            }

            if (parameters.size() == 0) {
                return false;
            }
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            if (StringUtils.isNotEmpty(schema)) {
                sql.append(schema).append(".");
            }
            sql.append(tableName);
            String insertKey = FieldUtils.getFieldJoinStringForInsertField(entity);
            String insertValue = FieldUtils.getFieldJoinStringForInsertParameter(entity);
            String update = FieldUtils.getFieldJoinStringForUpdateString(entity);

            /**
             * INSERT INTO tb_residential_houses
             *     ( province, city, area, buildingName,address )
             * VALUES
             *     ( '河北', '唐山', '丰润', '建筑一' ,:address)
             *     ON DUPLICATE KEY UPDATE address = :address;
             */
            sql.append("(").append(insertKey).append(")");
            sql.append(" VALUES ");
            sql.append("(").append(insertValue).append(")");
            sql.append("ON DUPLICATE KEY UPDATE ");
            sql.append(update);
            try {
                return executeNonQuery(connection, sql.toString(), parameters) > 0;
            } catch (Exception ex) {
                log.error(sql.toString());
                JsonObject jsonObject = DataSwitch.convertObjectToJsonObject(parameters);
                log.error(jsonObject.toString(), ex);

                throw ex;
            }
        }

        /**
         * Update boolean.
         *
         * @param <Entity> the type parameter
         * @param entity   the entity
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:05
         */
        public <Entity> boolean update(Entity entity) throws Exception {
            DruidPooledConnection connection = getConnection();
            try {
                boolean replace = update(connection, entity);
                connection.commit();
                return replace;
            } catch (Exception ex) {
                connection.rollback();
                throw ex;
            } finally {
                connection.close();
            }
        }

        /**
         * Update boolean.
         *
         * @param <Entity>   the type parameter
         * @param connection the connection
         * @param entity     the entity
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -01-07 15:39:05
         */
        public <Entity> boolean update(DruidPooledConnection connection, Entity entity) throws Exception {
            Class<Entity> clazz = (Class<Entity>) entity.getClass();

            TableSetting setting = FieldUtils.getEntityInfo(clazz);

            String tableName = setting.getTable();
            String schema = setting.getSchema();
            Map<String, Object> parameters = FieldUtils.getParameters(entity);

            JdbcField primaryKey = setting.getPrimaryKey();
            String primaryId = primaryKey.getFieldInDb();


            if (parameters.size() == 0) {
                return false;
            }
            StringBuilder sql = new StringBuilder("UPDATE ");
            if (StringUtils.isNotEmpty(schema)) {
                sql.append(schema).append(".");
            }
            sql.append(tableName);

            String update = FieldUtils.getFieldJoinStringForUpdateString(entity);
            sql.append(" SET ").append(update);
            sql.append(" WHERE ").append(primaryId).append("=:").append(primaryId);

            return executeNonQuery(connection, sql.toString(), parameters) > 0;
        }

        /**
         * Check table exists boolean.
         *
         * @param table the table
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -05-25 11:48:48
         */
        public Boolean checkTableExists(String table) throws Exception {
            return checkTableExists(schema, table);
        }

        /**
         * Check table exist boolean.
         *
         * @param schema the schema
         * @param table  the table
         * @return the boolean
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -05-25 11:26:41
         */
        public Boolean checkTableExists(String schema, String table) throws Exception {
            StringBuilder sbSql = new StringBuilder();
            sbSql.append(" SELECT count(1) exist ");
            sbSql.append(" FROM information_schema.TABLES ");
            sbSql.append(" WHERE TABLE_NAME = :table ");
            sbSql.append(" AND TABLE_SCHEMA = :schema ");

            Map<String, Object> parameters = new HashMap<>(2);
            parameters.put("schema", schema);
            parameters.put("table", table);

            Object exist = executeScalar(sbSql.toString(), "exist", parameters);
            return DataSwitch.convertObjectToBoolean(exist, false);
        }

        /**
         * Clone table .
         *
         * @param source the source
         * @param target the target
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -05-25 11:48:04
         */
        public void cloneTable(String source, String target) throws Exception {
            cloneTable(schema, source, target);
        }

        /**
         * Clone table .
         *
         * @param schema the schema
         * @param source the source
         * @param target the target
         * @throws Exception the exception
         * @author ErebusST
         * @since 2022 -05-25 11:26:23
         */
        public void cloneTable(String schema, String source, String target) throws Exception {
            StringBuilder sbSql = new StringBuilder();
            sbSql.append("CREATE TABLE ");
            sbSql.append(schema).append(".");
            sbSql.append(target);
            sbSql.append(" LIKE ");
            sbSql.append(schema).append(".");
            sbSql.append(source);
            executeNonQuery(sbSql.toString());
        }
    }


}
