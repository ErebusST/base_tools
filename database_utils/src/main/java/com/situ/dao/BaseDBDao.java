/*
 * Copyright (c) 2021. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.situ.dao;

import com.situ.config.DruidDBConfig;
import com.situ.entity.bo.JdbcField;
import com.situ.entity.bo.Pager;
import com.situ.entity.bo.TableSetting;
import com.situ.entity.enumeration.OrderTypeEnum;
import com.situ.entity.fo.BaseFindEntity;
import com.situ.tools.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.map.HashedMap;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.exception.SQLGrammarException;
import org.hibernate.query.NativeQuery;
import org.hibernate.transform.Transformers;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * Hibernate数据库管理类 <p> 传入参数格式 select * from table where a = :a and b = :b and c in (:c) <p> in操作，传入的参数值对象使用ArrayList
 *
 * @author 司徒彬
 * @date 2017 -01-06 09:47
 */
@Slf4j
public class BaseDBDao {

    private static final String LINE_SEPARATOR = System.getProperty("line.separator", "\n");

    /**
     * The Druid db config.
     */
    @Autowired
    DruidDBConfig druidDBConfig;


    @Resource(name = "sessionFactory")
    private final SessionFactory sessionFactory = null;


    private final static String PARAMETER_REGEX = ":[a-zA-Z]+\\w*";
    private final static String CHECK_SQL_INJECTION_ATTACK = "^" + PARAMETER_REGEX + "$";// "^[a-zA-Z]+\\w*$";


    /**
     * Gets session.
     *
     * @return the session
     */
    private Session getSession() {
        try {
            Session session = sessionFactory.getCurrentSession();

            return session;
        } catch (HibernateException e) {
            if ("Could not obtain transaction-synchronized Session for current thread".equalsIgnoreCase(e.getMessage())) {

                log.info(e.getMessage());
                return sessionFactory.openSession().getSession();
            }
            throw e;
        }
    }

    //region Util

    /**
     * Gets sort str.
     *
     * @param sortField     the sort field
     * @param orderTypeEnum the order type enum
     * @return the sort str
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    public String getSortStr(String sortField, OrderTypeEnum orderTypeEnum) {
        return getSortStr(null, sortField, orderTypeEnum);
    }

    /**
     * Gets sort str.
     *
     * @param findEntity the find entity
     * @return the sort str
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    public String getSortStr(BaseFindEntity findEntity) {
        return getSortStr(findEntity, null, null);
    }

    /**
     * Gets sort str.
     *
     * @param findEntity       the find entity
     * @param defaultSortField the default sort field
     * @param orderTypeEnum    the order type enum
     * @return the sort str
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    public String getSortStr(BaseFindEntity findEntity, String defaultSortField, OrderTypeEnum orderTypeEnum) {

        String sortField = defaultSortField;
        String sortType = orderTypeEnum.getValue();


        if (findEntity != null
                && StringUtils.isNotEmpty(findEntity.getSortField())
                && StringUtils.isNotEmpty(findEntity.getSortType())) {
            sortField = findEntity.getSortField();
            sortType = findEntity.getSortType();
        }

        if (StringUtils.isEmpty(sortField)) {
            return "";
        }

        if (StringUtils.isEmpty(sortType)) {
            sortType = "ASC";
        }

        if (sortField.trim().length() == 0 || sortType.trim().length() == 0) {
            return "";
        }

        Map<String, Class> sortSetting = SessionMap.getValue("SORT_SETTING");
        if (ObjectUtils.isNull(sortSetting)) {
            sortSetting = new HashMap<>(0);
        }
        String field = sortField;

        if (sortField.contains(".")) {
            String[] split = field.split("\\.");
            field = split[split.length - 1];
        }
        String sortStr;
        boolean isNumber = false;
        if (StringUtils.containsAny(field.toLowerCase(), "number", "count")) {
            isNumber = true;
        }
        if (sortSetting.containsKey(field.toLowerCase()) && !isNumber) {
            Class clazz = sortSetting.get(field);
            if (ObjectUtils.equals(clazz, String.class)) {
                sortStr = " ORDER BY CONVERT(".concat(sortField).concat(" USING gbk) ").concat(sortType);
            } else {
                sortStr = " ORDER BY ".concat(sortField).concat(" ").concat(sortType);
            }
        } else {
            sortStr = " ORDER BY ".concat(sortField).concat(" ").concat(sortType);
        }
        return sortStr;
    }


    /**
     * Gets session connection.
     *
     * @return the session connection
     * @throws NoSuchMethodException the no such method exception
     */
    private Connection getConnection() {
        try {
            Session session = getSession();
            //Method connectionMethod = session.getClass().getMethod("connection");
            List<Class> parameterTypes = new ArrayList<>(1);
            parameterTypes.add(Session.class);

            List<Object> argsList = new ArrayList<>(1);
            argsList.add(session);
            return (Connection) ReflectionUtils
                    .invokeMethod(session, "connection", parameterTypes.toArray(new Class[]{}), argsList.toArray());
            //return (Connection) ReflectionUtils.invokeMethod(connectionMethod, session);
        } catch (Exception e) {
            throw e;
        }

    }


    /**
     * Execute update boolean.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the boolean
     * @throws Exception the exception
     */
    private int executeUpdate(@Nonnull String sql, Map<String, Object> parameters) throws Exception {
        Session session = null;
        try {
            this.checkParametersSqlInjectionAttack(parameters);

            session = this.getSession();

            sql = fixSql(sql);
            NativeQuery nativeQuery = prepareSqlQuery(session, sql, parameters);

            int flag = nativeQuery.executeUpdate();
            return flag;
        } catch (Exception ex) {
            throw ex;
        } finally {
            sessionFlush(session);
        }
    }

    private static final String FIX_SQL_REGEX = "(?i)(\\s{1}\\w+\\.\\s{0,2}){0,1}tb_";

    private String fixSql(String sql) {
        return sql;
    }

    /**
     * Execute list list.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @param pageNumber the page num
     * @param pageSize   the result size
     * @return the list
     * @throws Exception the exception
     */
    private List<Map<String, Object>> executeList(@Nonnull String sql, Map<String, Object> parameters, Integer pageNumber, Integer pageSize)
            throws Exception {
        Session session = null;
        long start = System.currentTimeMillis();
        try {

            this.checkParametersSqlInjectionAttack(parameters);

            session = this.getSession();

            sql = fixSql(sql);
            NativeQuery nativeQuery = prepareSqlQuery(session, sql, parameters);
            nativeQuery.setResultTransformer(Transformers.ALIAS_TO_ENTITY_MAP);
            if (pageNumber != null && pageSize != null) {
                int startIndex = (pageNumber - 1) * pageSize;
                nativeQuery.setFirstResult(startIndex);
            }
            if (pageSize != null) {
                nativeQuery.setMaxResults(pageSize);
            }


            return nativeQuery.list();

        } catch (SQLGrammarException ex) {
            String stackTrace = ExceptionUtils.getStackTrace(ex);
            StringBuilder error = new StringBuilder("sql语法错误:").append(LINE_SEPARATOR);
            error.append(sql).append(LINE_SEPARATOR);
            error.append("参数:").append(LINE_SEPARATOR);
            if (ObjectUtils.isNotNull(parameters)) {
                error.append(DataSwitch.convertObjectToJsonObject(parameters, true).toString());
            }
            error.append(LINE_SEPARATOR);
            error.append("异常信息:").append(LINE_SEPARATOR);
            error.append(stackTrace);
            log.error(error.toString());
            throw ex;
        } catch (Exception ex) {
            throw ex;
        } finally {
            long end = System.currentTimeMillis();
            log.info("查询数据花费时间：{}",
                    DateUtils.getSpendTime(end, start));
            sessionFlush(session);
        }
    }

    /**
     * Execute Scalar object.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the object
     * @throws Exception the exception
     */
    private Object executeScalar(@Nonnull String sql, Map<String, Object> parameters) throws Exception {
        Session session = null;
        try {
            this.checkParametersSqlInjectionAttack(parameters);

            session = this.getSession();

            sql = fixSql(sql);

            NativeQuery nativeQuery = prepareSqlQuery(session, sql, parameters);

            return nativeQuery.uniqueResult();
        } catch (Exception ex) {
            throw ex;
        } finally {
            sessionFlush(session);
        }
    }

    /**
     * sql中变量提取的正则表达式
     */
    private final static Pattern PARAMETER_PATTERN = Pattern.compile(PARAMETER_REGEX);

    /**
     * Prepare sql query sql query.
     *
     * @param session    the session
     * @param sql        the sql
     * @param parameters the parameters
     * @return the sql query
     * @throws Exception the exception
     */
    private NativeQuery prepareSqlQuery(@Nonnull Session session, @Nonnull String sql, Map<String, Object> parameters)
            throws Exception {
        try {
            parameters = parameters == null ? new HashedMap() : parameters;

            Matcher matcher = PARAMETER_PATTERN.matcher(sql);
            List<String> errorList = new ArrayList<>();
            Map<String, Object> parametersTemp = new HashedMap();
            while (matcher.find()) {
                String parameter = matcher.group().trim().substring(1);

                if (parameters.containsKey(parameter)) {
                    Object parameterValue = parameters.get(parameter);
                    //如果参数值的类型为list，处理为:para1,:para2的形式
                    if (parameterValue != null && (ArrayList.class.equals(parameterValue.getClass())
                            || Arrays.class.equals(parameterValue.getClass().getDeclaringClass()))) {
                        List<Object> valueList = (List<Object>) parameterValue;
                        if (valueList.size() == 0) {
                            valueList.add(-1);
                        }

                        StringBuilder tempStringBuilder = new StringBuilder();
                        valueList.forEach(value -> {
                            String key = new StringBuilder(parameter).toString();
                            int prefix = 0;
                            while (parametersTemp.containsKey(key)) {
                                key = new StringBuilder(parameter).append(prefix).toString();
                                prefix++;
                            }
                            tempStringBuilder.append(",@").append(key);
                            parametersTemp.put(key, value);
                        });
                        try {
                            if (tempStringBuilder.length() != 0) {
                                String tempStr = tempStringBuilder.deleteCharAt(0).toString();
                                sql = sql.replaceFirst(PARAMETER_REGEX, tempStr);
                            } else {
                                sql = sql.replaceFirst(PARAMETER_REGEX, tempStringBuilder.toString());
                            }

                        } catch (Exception e) {
                            throw e;
                        }

                    } else {
                        //"@" + parameter
                        sql = sql.replaceFirst(PARAMETER_REGEX, String.format("@%s", parameter));
                        if (parameterValue != null && parameterValue.getClass().equals(String.class)) {
                            parameterValue = parameterValue == null ? parameterValue : parameterValue.toString().trim();
                        }
                        parametersTemp.put(parameter, parameterValue);
                    }
                } else {
                    if (!parameter.trim().equalsIgnoreCase("schema")) {
                        errorList.add("未找到变量 [" + parameter + "] 的参数值!");
                    }
                }
            }

            if (errorList.size() > 0) {
                String errorMessage = StringUtils.getCombineString(errorList);
                throw new Exception(errorMessage);
            }
            sql = sql.replace("@", ":");
            NativeQuery nativeQuery = session.createNativeQuery(sql);
            parametersTemp.entrySet().stream().forEach(entry -> nativeQuery.setParameter(entry.getKey(), entry.getValue()));
            return nativeQuery;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Session flush.
     *
     * @param session the session
     */
    private void sessionFlush(Session session) {
        try {
            if (session != null) {
                session.flush();
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * 注入漏洞验证正则表达式对象
     */
    private static final Pattern SQL_INJECTION_ATTACK_PATTERN = Pattern.compile(CHECK_SQL_INJECTION_ATTACK);

    /**
     * 验证是否包含注入代码
     *
     * @param parameters the parameters
     * @return the boolean
     */
    private boolean checkParametersSqlInjectionAttack(Map<String, Object> parameters) throws Exception {
        try {
            if (parameters == null) {
                return false;
            }

            long errorCount = parameters.keySet().stream().filter(key -> {
                boolean isEmpty = StringUtils.isEmpty(key);

                if (isEmpty) {
                    return true;
                } else {
                    Matcher matcher = SQL_INJECTION_ATTACK_PATTERN.matcher(":".concat(key));
                    boolean flag = matcher.matches();
                    return !flag;
                }

            }).count();
            if (errorCount > 0) {
                throw new Exception("this is has sql injection attack code,the keyName of parameter is not allowed. "
                        .concat(DataSwitch.convertObjectToJsonElement(parameters.keySet()).toString()));
            } else {
                return false;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Sets dataexport version.
     *
     * @param entity the entity
     * @return the dataexport version
     */
    private Object setDataVersion(Object entity) throws Exception {
        try {
            Boolean updateVersion = druidDBConfig.getUpdateVersion();
            if (!updateVersion) {
                return entity;
            }
            String version = getNewVersion(entity);
            ReflectionUtils.setFieldValue(entity, "version", version);
            return entity;

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets new version.
     *
     * @param entity the entity
     * @return the new version
     * @throws UnsupportedEncodingException the unsupported encoding exception
     * @throws NoSuchAlgorithmException     the no such algorithm exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    public String getNewVersion(Object entity) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(entity.getClass());
            String version = setting.getFields()
                    .stream()
                    .filter(field -> StringUtils.equalsIgnoreCase(field.getField().getName(), "version"))
                    .sorted(Comparator.comparing(field -> field.getField().getName()))
                    .map(field -> {
                        Object value = ReflectionUtils.getFieldValue(entity, String.valueOf(field));
                        return DataSwitch.convertObjectToString(value);
                    })
                    .collect(Collectors.joining());
            version = MessageDigestUtils.md5(version);
            return version;
        } catch (Exception ex) {
            throw ex;
        }
    }


    //endregion

    //region schema

    /**
     * Check schema boolean.
     *
     * @param schema the schema
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -10-23 11:34:55
     */
    public boolean checkSchema(String schema) throws Exception {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append(" SELECT SCHEMA_NAME TABLE_SCHEMA ");
        sbSql.append(" FROM information_schema.SCHEMATA ");
        sbSql.append(" WHERE SCHEMA_NAME = :schema ");


        Map<String, Object> parameters = new HashMap<>(1);
        parameters.put("schema", schema);
        Map<String, Object> first = findFirstOneBySql(sbSql.toString(), parameters);
        return ObjectUtils.isNotNull(first);
    }

    /**
     * Check table boolean.
     *
     * @param schema the schema
     * @param table  the table
     * @return the boolean
     * @author ErebusST
     * @since 2022 -10-23 11:34:57
     */
    public boolean checkTable(String schema, String table) throws Exception {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append(" SELECT TABLE_SCHEMA, TABLE_NAME ");
        sbSql.append(" FROM information_schema.TABLES ");
        sbSql.append(" WHERE TABLE_SCHEMA = :schema ");
        sbSql.append("   AND TABLE_NAME = :table ");


        Map<String, Object> parameters = new HashMap<>(2);
        parameters.put("schema", schema);
        parameters.put("table", table);
        Map<String, Object> first = findFirstOneBySql(sbSql.toString(), parameters);
        return ObjectUtils.isNotNull(first);
    }


    /**
     * Check field boolean.
     *
     * @param schema the schema
     * @param table  the table
     * @param field  the field
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -10-23 11:37:06
     */
    public boolean checkField(String schema, String table, String field) throws Exception {
        StringBuilder sbSql = new StringBuilder();
        sbSql.append(" SELECT TABLE_SCHEMA, TABLE_NAME, COLUMN_NAME ");
        sbSql.append(" FROM information_schema.COLUMNS ");
        sbSql.append(" WHERE TABLE_SCHEMA = :schema ");
        sbSql.append("   AND TABLE_NAME = :table ");
        sbSql.append("   AND COLUMN_NAME = :field ");

        Map<String, Object> parameters = new HashMap<>(3);
        parameters.put("schema", schema);
        parameters.put("table", table);
        parameters.put("field", field);
        Map<String, Object> first = findFirstOneBySql(sbSql.toString(), parameters);
        return ObjectUtils.isNotNull(first);
    }
    //endregion

    //region save

    /**
     * Save boolean.
     * <p>
     * 存在则更新，不存在则添加
     *
     * @param entity the entity
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    @Deprecated
    public boolean save(@Nonnull Object entity) throws Exception {
        try {
            String primaryKey = FieldUtils.getPrimaryKeySetting(entity.getClass()).getField().getName();
            Object id = ReflectionUtils.getFieldValue(entity, primaryKey);
            if (id == null) {
                return insertOne(entity);
            } else {
                return updateEntity(entity) > 0;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Save many boolean.
     *
     * @param <T>      the type parameter
     * @param entities the entities
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    @Deprecated
    public <T> boolean saveMany(@Nonnull List<T> entities) throws Exception {
        boolean isSuccess = entities.size() > 0;
        for (T entity : entities) {
            boolean isSuccessTemp = save(entity);
            isSuccess = isSuccess && isSuccessTemp;
        }
        return isSuccess;
    }

    //endregion


    //region insert Method

    /**
     * Insert one boolean.
     *
     * @param entity the
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    public boolean insertOne(@Nonnull Object entity) throws Exception {
        try {
            return insertOne(entity, true);
        } catch (Exception ex) {
            throw ex;
        } finally {
            //sessionFlush(session);
        }
    }


    /**
     * Insert one boolean.
     *
     * @param entity   the entity
     * @param createId the create id
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    public boolean insertOne(Object entity, boolean createId) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(entity.getClass());
            if (!setting.isAutoIncrement() && createId) {
                String primaryKey = setting.getPrimaryKey().getField().getName();
                ReflectionUtils.setFieldValue(entity, primaryKey, GeneratorId.nextId());
            }

            //设置版本
            entity = setDataVersion(entity);

            this.insert(entity);
            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            //sessionFlush(session);
        }
    }

    private boolean insert(@Nonnull Object entity) throws Exception {
        try {
            Class clazz = entity.getClass();
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            StringBuilder sql = new StringBuilder("INSERT INTO ");
            sql.append(setting.getTable()).append(" ");
            String insertKey = FieldUtils.getFieldJoinStringForInsertField(entity);
            String parameterKey = FieldUtils.getFieldJoinStringForInsertParameter(entity);
            sql.append("(")
                    .append(insertKey)
                    .append(") VALUES (")
                    .append(parameterKey).append(" )");
            Map<String, Object> parameters = FieldUtils.getParameters(entity);
            return executeUpdate(sql.toString(), parameters) > 0;
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Insert many boolean.
     *
     * @param <T>   the type parameter
     * @param tList the t list
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:52
     */
    @Deprecated
    public <T> boolean insertMany(@Nonnull List<T> tList) throws Exception {
        try {
            return insertMany(tList, 1000);
        } catch (Exception ex) {
            throw ex;
        }

    }

    /**
     * Insert many boolean.
     *
     * @param <T>       the type parameter
     * @param tList     the t list
     * @param flushSize the flush size
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    @Deprecated
    public <T> boolean insertMany(@Nonnull List<T> tList, @Nonnull int flushSize) throws Exception {
        Session session = null;
        try {
            int size = tList.size();
            Class clazz = size > 0 ? tList.get(0).getClass() : null;
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getField().getName();
            session = getSession();
            for (int i = 0; i < size; i++) {
                Object entity = tList.get(i);
                ReflectionUtils.setFieldValue(entity, primaryKey, GeneratorId.nextId());
                //设置版本
                entity = setDataVersion(entity);
                session.save(entity);
                if (i % flushSize == 0) {
                    sessionFlush(session);
                }
            }
            return true;
        } catch (Exception ex) {
            throw ex;
        } finally {
            sessionFlush(session);
        }
    }

    //endregion

    //region replace Method

    /**
     * Replace boolean.
     *
     * @param entity the entity
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    public boolean replace(@Nonnull Object entity) throws Exception {
        return replace(entity, true);
    }

    /**
     * Replace boolean.
     *
     * @param entity   the entity
     * @param createId the create id
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    public boolean replace(@Nonnull Object entity, Boolean createId) throws Exception {
        Class clazz = entity.getClass();
        TableSetting setting = FieldUtils.getEntityInfo(clazz);
        String primaryKey = setting.getPrimaryKey().getField().getName();

        if (!setting.isAutoIncrement() && createId) {
            ReflectionUtils.setFieldValue(entity, primaryKey, GeneratorId.nextId());
        }
        //设置版本
        setDataVersion(entity);

        Map<String, Object> parameters = FieldUtils.getParameters(entity);

        String tableName = setting.getTable();
        String schema = setting.getSchema();
        if (ObjectUtils.isNotEmpty(schema)) {
            tableName = schema.concat(".").concat(tableName);
        }
        StringBuilder sql = new StringBuilder("INSERT INTO ");
        sql.append(tableName);
        String insertKey = FieldUtils.getFieldJoinStringForInsertField(entity);
        String insertValue = FieldUtils.getFieldJoinStringForInsertParameter(entity);
        String updateStr = FieldUtils.getFieldJoinStringForUpdateString(entity);

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
        sql.append(updateStr);

        return executeUpdate(sql.toString(), parameters) > 0;

    }

    //endregion

    //region update Method

    /**
     * Update one boolean.
     *
     * @param entity the
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    public int updateOne(@Nonnull Object entity) throws Exception {
        try {
            return update(entity, false);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Update  int.
     * <p>
     * 更新实体中的字段，如果实体中某个字段为null，则不更新
     *
     * @param entity the
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    public int updateEntity(@Nonnull Object entity) throws Exception {
        try {
            return update(entity, true);
        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Update one boolean.
     *
     * @param entity       the entity
     * @param isFilterNull the is filter null
     * @return the boolean
     * @throws Exception the exception
     */
    private int update(@Nonnull Object entity, boolean isFilterNull) throws Exception {
        Class clazz = entity.getClass();
        TableSetting setting = FieldUtils.getEntityInfo(clazz);
        String primaryKey = setting.getPrimaryKey().getFieldInDb();


        String tableName = setting.getTable();
        String schema = setting.getSchema();
        if (ObjectUtils.isNotEmpty(schema)) {
            tableName = schema.concat(".").concat(tableName);
        }

        StringBuffer sbSql = new StringBuffer();

        sbSql.append(" UPDATE ");
        sbSql.append(tableName);
        sbSql.append(" SET ");


        String updateString = FieldUtils.getFieldJoinStringForUpdateString(entity, isFilterNull);
        sbSql.append(updateString);

        StringBuilder whereSql = new StringBuilder(" WHERE 1 = 1 ");
        whereSql.append(" AND ").append(primaryKey).append("= :").append(primaryKey);
        Map<String, Object> parameters = FieldUtils.getParameters(entity);
        if (!parameters.containsKey(primaryKey)) {
            throw new Exception(String.format("主键 [ %s ] 必须赋值，否则无法更新!", primaryKey));
        }
        if (parameters.values().size() < 2) {
            throw new Exception("需要至少为该实体主键和其他任意一字段赋值，无法更新!");
        }
        Optional<JdbcField> hasVersion = setting.getFields().stream()
                .filter(field -> StringUtils.equalsIgnoreCase(field.getFieldInDb(), "version"))
                .findFirst();
        if (hasVersion.isPresent()) {
            Object oldVersion = ReflectionUtils.getFieldValue(entity, "version");
            if (ObjectUtils.isNotNull(oldVersion)) {
                this.setDataVersion(entity);
                Object version = ReflectionUtils.getFieldValue(entity, "version");
                parameters.put("version", version);
                whereSql.append(" AND version = :oldVersion ");
                parameters.put("oldVersion", oldVersion);
            }
        }
        sbSql.append(whereSql.toString());
        return updateBySql(sbSql.toString(), parameters);
    }

    /**
     * Update by primary key int.
     *
     * @param <T>             the type parameter
     * @param clazz           the clazz
     * @param primaryKeyValue the primary key value
     * @param fieldName       the field name
     * @param fieldValue      the field value
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    public <T> int updateByPrimaryKey(@Nonnull Class<T> clazz, @Nonnull Serializable primaryKeyValue, @Nonnull String fieldName, Object fieldValue)
            throws Exception {
        try {
            Map<String, Object> updateFieldParameters = new HashedMap(1);
            updateFieldParameters.put(fieldName, fieldValue);
            return updateByPrimaryKey(clazz, primaryKeyValue, updateFieldParameters);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Update by primary key int.
     *
     * @param <T>                   the type parameter
     * @param clazz                 the clazz
     * @param primaryKeyValue       the primary key value
     * @param updateFieldParameters the update field parameters
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:53
     */
    public <T> int updateByPrimaryKey(@Nonnull Class<T> clazz, @Nonnull Serializable primaryKeyValue, @Nonnull Map<String, Object> updateFieldParameters)
            throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();

            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(primaryKey, primaryKeyValue);
            String sql = createUpdateSql(clazz, updateFieldParameters, parameters);
            parameters.putAll(updateFieldParameters);
            return updateBySql(sql, parameters);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Update by primary key int.
     *
     * @param <T>              the type parameter
     * @param clazz            the clazz
     * @param primaryKeyValues the primary key values
     * @param fieldName        the field name
     * @param fieldValue       the field value
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public <T> int updateByPrimaryKey(@Nonnull Class<T> clazz, @Nonnull List<Long> primaryKeyValues, @Nonnull String fieldName, Object fieldValue)
            throws Exception {
        try {
            Map<String, Object> updateFieldParameters = new HashedMap(1);
            updateFieldParameters.put(fieldName, fieldValue);
            return updateByPrimaryKey(clazz, primaryKeyValues, updateFieldParameters);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Update by filter int.
     *
     * @param <T>                   the type parameter
     * @param clazz                 the clazz
     * @param primaryKeyValues      the primary key values
     * @param updateFieldParameters the parameters
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public <T> int updateByPrimaryKey(@Nonnull Class<T> clazz, @Nonnull List<Long> primaryKeyValues, Map<String, Object> updateFieldParameters)
            throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();

            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(primaryKey, primaryKeyValues);
            String sql = createUpdateSql(clazz, updateFieldParameters, parameters);
            parameters.putAll(updateFieldParameters);
            return updateBySql(sql, parameters);

        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Update by sql int.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public int updateBySql(@Nonnull String sql, Map<String, Object> parameters) throws Exception {
        try {
            return executeUpdate(sql, parameters);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Create update sql string.
     *
     * @param <T>                   the type parameter
     * @param clazz                 the clazz
     * @param updateFieldParameters the update field
     * @param filterField           the filter field
     * @return the string
     */
    private <T> String createUpdateSql(@Nonnull Class<T> clazz, @Nonnull Map<String, Object> updateFieldParameters, Map<String, Object> filterField) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            filterField = filterField == null ? new HashedMap() : filterField;


            StringBuilder sqlBuilder = new StringBuilder();
            String tableName = setting.getTable();
            String schema = setting.getSchema();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }
            sqlBuilder.append("UPDATE ").append(tableName).append(" SET ");

            updateFieldParameters.keySet().forEach(key -> sqlBuilder.append(key).append("=:").append(key).append(","));
            sqlBuilder.deleteCharAt(sqlBuilder.length() - 1);
            sqlBuilder.append(" WHERE 1 = 1 ");
            filterField.forEach((key, value) -> {
                if (ArrayList.class.equals(value.getClass())) {
                    sqlBuilder.append(" AND ").append(key).append(" IN ").append("(:").append(key).append(")");
                } else if (ObjectUtils.isNull(value)) {
                    sqlBuilder.append(" AND ").append(key).append(" IS NULL");
                } else {
                    sqlBuilder.append(" AND ").append(key).append(" = :").append(key);
                }
            });
            return sqlBuilder.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }


    //endregion

    //region delete Method


    /**
     * Delete by primary key int.
     *
     * @param <T>             the type parameter
     * @param clazz           the clazz
     * @param primaryKeyValue the primary key value
     * @return the int
     * @throws Exception the exception Serializable
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public <T> int deleteByPrimaryKey(@Nonnull Class<T> clazz, @Nonnull Long primaryKeyValue) throws Exception {
        try {
            List<Long> primaryKeyValues = new ArrayList<>(1);
            primaryKeyValues.add(primaryKeyValue);
            return deleteByPrimaryKeys(clazz, primaryKeyValues);
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Delete by primary key int.
     *
     * @param <T>              the type parameter
     * @param clazz            the clazz
     * @param primaryKeyValues the primary key values
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public <T> int deleteByPrimaryKeys(@Nonnull Class<T> clazz, @Nonnull List<Long> primaryKeyValues)
            throws Exception {
        try {
            if (primaryKeyValues.size() == 0) {
                return 0;
            }
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();
            return deleteByFilter(clazz, primaryKey, primaryKeyValues);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Delete by filter int.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param fieldName  the field name
     * @param fieldValue the field value
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public <T> int deleteByFilter(@Nonnull Class<T> clazz, @Nonnull String fieldName, Object fieldValue)
            throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(fieldName, fieldValue);
            return deleteByFilter(clazz, parameters);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Delete by filter int.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:54
     */
    public <T> int deleteByFilter(@Nonnull Class<T> clazz, Map<String, Object> parameters) throws Exception {
        try {
            this.checkParametersSqlInjectionAttack(parameters);

            String sql = this.createDeleteSql(clazz, parameters);
            return deleteBySql(sql, parameters);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Delete by sql int.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the int
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public int deleteBySql(@Nonnull String sql, Map<String, Object> parameters) throws Exception {
        try {
            return executeUpdate(sql, parameters);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Create delete sql string.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameter
     * @return the string
     */
    private <T> String createDeleteSql(@Nonnull Class<T> clazz, Map<String, Object> parameters) throws Exception {
        try {
            if (parameters.keySet().size() == 0) {
                throw new Exception("参数不能为空");
            }
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            StringBuilder sqlBuilder = new StringBuilder();
            String tableName = setting.getTable();
            String schema = setting.getSchema();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }
            sqlBuilder.append("DELETE FROM ").append(tableName).append(" WHERE 1 = 1 ");
            parameters.forEach((key, value) -> {
                if (ArrayList.class.equals(value.getClass())) {
                    sqlBuilder.append(" AND ").append(key).append(" IN ").append("(:").append(key).append(")");
                } else if (ObjectUtils.isNull(value)) {
                    sqlBuilder.append(" AND ").append(key).append(" IS NULL");
                } else {
                    sqlBuilder.append(" AND ").append(key).append(" = :").append(key);
                }
            });
            return sqlBuilder.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }


    //endregion

    //region FindOne Method

    /**
     * 根据主键获得实体
     *
     * @param <T>       the type parameter
     * @param clazz     the clazz
     * @param primaryId the primary id
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public <T> T findById(@Nonnull Class<T> clazz, @Nonnull Serializable primaryId) throws Exception {
        String sql = this.getFindByIdSql(clazz);
        Map<String, Object> parameters = new HashMap();
        parameters.put("primaryKey", primaryId);
        final Map<String, Object> map = findFirstOneBySql(sql, parameters);
        if (ObjectUtils.isNull(map)) {
            return null;
        }
        return DataSwitch.convertMapObjToEntity(clazz, map);
    }

    private <T> String getFindByIdSql(@Nonnull Class<T> clazz) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String tableName = setting.getTable();

            final String primaryKey = setting.getPrimaryKey().getFieldInDb();
            StringBuilder sql = new StringBuilder();
            String schema = setting.getSchema();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }
            sql.append("SELECT * FROM ").append(tableName).append(" WHERE ").append(primaryKey).append(" = :primaryKey");
            return sql.toString();

        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Find first one t.
     *
     * @param <T>    the type parameter
     * @param clazz  the clazz
     * @param orders the orders
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public <T> T findFirstOne(@Nonnull Class<T> clazz, Order... orders) throws Exception {
        try {
            return this.findFirstOne(clazz, null, orders);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find first one t.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param fieldName  the field name
     * @param fieldValue the field value
     * @param orders     the orders
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public <T> T findFirstOne(@Nonnull Class<T> clazz, @Nonnull String fieldName, Object fieldValue, Order... orders)
            throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap();
            parameters.put(fieldName, fieldValue);
            return findFirstOne(clazz, parameters, orders);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find first one t.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param orders     the orders
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public <T> T findFirstOne(@Nonnull Class<T> clazz, Map<String, Object> parameters, Order... orders) throws Exception {
        try {
            List<T> list = this.findList(clazz, parameters, 1, orders);
            return list.size() > 0 ? list.get(0) : null;
        } catch (Exception ex) {
            throw ex;
        } finally {
            //sessionFlush(session);
        }
    }

    /**
     * Find first one by sql map.
     *
     * @param sql the sql
     * @return the map
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public Map<String, Object> findFirstOneBySql(String sql) throws Exception {
        return findFirstOneBySql(sql, null);
    }

    /**
     * Find first one by sql map.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the map
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
    public Map<String, Object> findFirstOneBySql(String sql, Map<String, Object> parameters) throws Exception {
        try {
            List<Map<String, Object>> list = this.executeList(sql, parameters, 1, 1);
            return list.size() == 0 ? null : list.get(0);
        } catch (Exception ex) {
            throw ex;
        }
    }

    //endregion

    /**
     * Find field value return type.
     *
     * @param <T>          the type parameter
     * @param <ReturnType> the type parameter
     * @param clazz        the clazz
     * @param filterField  the filter field
     * @param filterValue  the filter value
     * @param fieldName    the field name
     * @return the return type
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:55
     */
//region Find field
    public <T, ReturnType> ReturnType findFieldValue(Class<T> clazz, String filterField, Object filterValue, String fieldName)
            throws Exception {
        try {
            Map<String, Object> result = this.findFieldsValue(clazz, filterField, filterValue, fieldName);
            Optional<Field> fieldOptional = Arrays.stream(ReflectionUtils.getFields(clazz))
                    .filter(field -> field.getName().equalsIgnoreCase(fieldName)).findFirst();

            if (!fieldOptional.equals(Optional.empty()) && fieldOptional.get().getType().equals(Long.class)) {
                return (ReturnType) DataSwitch.convertObjectToLong(result.get(fieldName));

            } else if (fieldOptional.equals(Optional.empty())) {
                return null;
            } else {
                return (ReturnType) result.get(fieldName);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find field value t.
     *
     * @param <T>       the type parameter
     * @param sql       the sql
     * @param fieldName the field name
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> T findFieldValue(String sql, String fieldName) throws Exception {
        return this.findFieldValue(sql, null, fieldName);
    }

    /**
     * Find field value t.
     *
     * @param <T>        the type parameter
     * @param sql        the sql
     * @param parameters the parameters
     * @param fieldName  the field name
     * @return the t
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> T findFieldValue(String sql, Map<String, Object> parameters, String fieldName) throws Exception {
        try {
            if (!sql.contains(fieldName)) {
                throw new Exception(String.format("The sql must be contain fieldName named [ %s ].", fieldName));
            }

            Map<String, Object> map = this.findFirstOneBySql(sql, parameters);

            if (map != null) {
                Object value = map.get(fieldName);
                if (null == value) {
                    return null;
                }
                if (value.getClass().equals(BigInteger.class)) {
                    return (T) DataSwitch.convertObjectToLong(value);
                } else {
                    return (T) map.get(fieldName);
                }
            } else {
                return null;
            }
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Find field value map.
     *
     * @param <T>         the type parameter
     * @param clazz       the clazz
     * @param filterField the filter field
     * @param filterValue the filter value
     * @param fields      the fields
     * @return the map
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> Map<String, Object> findFieldsValue(Class<T> clazz, String filterField, Object filterValue, String... fields)
            throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(filterField, filterValue);
            return findFieldsValue(clazz, parameters, fields);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find field value map.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param fields     the fields
     * @return the map
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> Map<String, Object> findFieldsValue(Class<T> clazz, Map<String, Object> parameters, String... fields)
            throws Exception {
        try {
            String sql = this.createFindFieldsSqlString(clazz, parameters, fields);
            return this.findFirstOneBySql(sql, parameters);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find field value list list.
     *
     * @param <T>         the type parameter
     * @param clazz       the clazz
     * @param filterField the filter field
     * @param filterValue the filter value
     * @param fields      the fields
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> List<Map<String, Object>> findFieldsValueList(Class<T> clazz, String filterField, Object filterValue, String... fields)
            throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(filterField, filterValue);
            return findFieldValueList(clazz, parameters, fields);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find field value list list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param fields     the fields
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> List<Map<String, Object>> findFieldValueList(Class<T> clazz, Map<String, Object> parameters, String... fields)
            throws Exception {
        try {
            String sql = this.createFindFieldsSqlString(clazz, parameters, fields);
            return this.findListMapBySql(sql, parameters);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Create find field sql string string.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param fields     the fields
     * @return the string
     */
    private <T> String createFindFieldsSqlString(Class<T> clazz, Map<String, Object> parameters, String... fields) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String tableName = setting.getTable();
            String schema = setting.getSchema();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }

            StringBuilder sbSql = new StringBuilder("SELECT ");
            if (fields.length == 0) {
                sbSql.append(" * ");
            } else {
                Arrays.stream(fields).forEach(field ->
                        sbSql.append(field).append(",")
                );
            }

            sbSql.deleteCharAt(sbSql.length() - 1);
            sbSql.append(" FROM ").append(tableName).append(" WHERE 1 = 1 ");
            parameters.keySet().forEach(key ->
                    sbSql.append(" AND ").append(key).append(" = :").append(key)
            );
            return sbSql.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }
    //endregion

    //region FindListMethod

    /**
     * Find list list.
     *
     * @param <T>    the type parameter
     * @param clazz  the clazz
     * @param orders the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:56
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, Order... orders) throws Exception {
        return findList(clazz, null, orders);
    }

    /**
     * Find list  list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:57
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, @Nonnull String field, Object fieldValue, Order... orders)
            throws Exception {
        try {
            return findList(clazz, field, fieldValue, null, orders);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Find list list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:57
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, @Nonnull String field, Object fieldValue, Integer pageSize, Order... orders)
            throws Exception {
        try {
            return findList(clazz, field, fieldValue, null, pageSize, orders);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list  list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param pageNumber the page num
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:57
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, @Nonnull String field, Object fieldValue, Integer pageNumber, Integer pageSize, Order... orders)
            throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(field, fieldValue);
            return findList(clazz, parameters, pageNumber, pageSize, orders);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list  list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:57
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, Map<String, Object> parameters, Order... orders)
            throws Exception {
        try {
            return findList(clazz, parameters, null, orders);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list  list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:57
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, Map<String, Object> parameters, Integer pageSize, Order... orders)
            throws Exception {
        try {
            return findList(clazz, parameters, null, pageSize, orders);

        } catch (Exception e) {
            throw e;
        }

    }

    /**
     * Find list  list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param pageNumber the page num
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:57
     */
    public <T> List<T> findList(@Nonnull Class<T> clazz, Map<String, Object> parameters, Integer pageNumber, Integer pageSize, Order... orders)
            throws Exception {
        List<Map<String, Object>> data = findListMap(clazz, parameters, pageNumber, pageSize, orders);
        List<Exception> errors = new ArrayList<>(data.size());
        List<T> result = data.stream().map(row -> {
            T t = DataSwitch.convertMapObjToEntity(clazz, row);
            return t;
        }).filter(ObjectUtils::isNotNull).collect(Collectors.toList());
        return result;
    }


    /**
     * Find list  by ids list.
     *
     * @param <T>    the type parameter
     * @param clazz  the clazz
     * @param idArr  the id arr 主键
     * @param orders the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:58
     */
    public <T> List<T> findListByIds(@Nonnull Class<T> clazz, @Nonnull List<Long> idArr, Order... orders)
            throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(primaryKey, idArr);
            return findList(clazz, parameters, orders);
        } catch (Exception e) {
            throw e;
        }
    }


    /**
     * Find list map list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:58
     */
    public <T> List<Map<String, Object>> findListMap(@Nonnull Class<T> clazz, @Nonnull String field, Object fieldValue, Order... orders)
            throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(field, fieldValue);

            return findListMap(clazz, field, fieldValue, null, orders);

        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list map list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:58
     */
    public <T> List<Map<String, Object>> findListMap(@Nonnull Class<T> clazz, @Nonnull String field, Object fieldValue, Integer pageSize, Order... orders)
            throws Exception {
        try {
            return findListMap(clazz, field, fieldValue, null, pageSize, orders);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list map list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param pageNumber the page num
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:58
     */
    public <T> List<Map<String, Object>> findListMap(@Nonnull Class<T> clazz, @Nonnull String field,
                                                     Object fieldValue, Integer pageNumber,
                                                     Integer pageSize, Order... orders) throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(field, fieldValue);
            return findListMap(clazz, parameters, pageNumber, pageSize, orders);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list map list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:58
     */
    public <T> List<Map<String, Object>> findListMap(@Nonnull Class<T> clazz, Map<String, Object> parameters, Order... orders)
            throws Exception {
        try {
            return this.findListMap(clazz, parameters, null, orders);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find list map list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:58
     */
    public <T> List<Map<String, Object>> findListMap(@Nonnull Class<T> clazz, Map<String, Object> parameters, Integer pageSize, Order... orders)
            throws Exception {
        try {
            return this.findListMap(clazz, parameters, null, pageSize, orders);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find list map list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param pageNumber the page num
     * @param pageSize   the page size
     * @param orders     the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public <T> List<Map<String, Object>> findListMap(@Nonnull Class<T> clazz, Map<String, Object> parameters, Integer pageNumber, Integer pageSize,
                                                     Order... orders) throws Exception {
        try {
            String sql = this.createFindSql(clazz, parameters, orders);
            return this.executeList(sql, parameters, pageNumber, pageSize);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find list  by ids list.
     *
     * @param <T>    the type parameter
     * @param clazz  the clazz
     * @param idArr  the id arr
     * @param orders the orders
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public <T> List<Map<String, Object>> findListMapByIds(@Nonnull Class<T> clazz, @Nonnull List<Long> idArr, Order... orders)
            throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(primaryKey, idArr);
            return findListMap(clazz, parameters, orders);
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find pager pager.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @param pageNumber the page num
     * @param pageSize   the page size
     * @return the pager
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public Pager findPager(@Nonnull String sql, Map<String, Object> parameters, Integer pageNumber, Integer pageSize)
            throws Exception {
        try {
            StringBuilder contSql = new StringBuilder();
            contSql.append("SELECT COUNT(1) FROM (").append(sql).append(") TEMP");
            return findPager(sql, parameters, pageNumber, pageSize, contSql.toString());
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find pager pager.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @param pageNumber the page number
     * @param pageSize   the page size
     * @param countSql   the count sql
     * @return the pager
     * @throws Exception the exception
     * @author ErebusST
     * @since 2023 -03-21 15:15:50
     */
    public Pager findPager(@Nonnull String sql, Map<String, Object> parameters, Integer pageNumber, Integer pageSize, String countSql)
            throws Exception {
        try {
            long start = System.currentTimeMillis();
            Pager pager = new Pager();
            pager.setPageSize(pageSize);

            pager.setPageNumber(pageNumber);
            List<Map<String, Object>> resultList = this.executeList(sql, parameters, pageNumber, pageSize);
            long second = System.currentTimeMillis();
            Integer totalCount = count(countSql, parameters);
            long end = System.currentTimeMillis();

            log.info("分页数据花费时间：{},总数花费时间:{},共计:{}",
                    DateUtils.getSpendTime(second, start),
                    DateUtils.getSpendTime(end, second),
                    DateUtils.getSpendTime(end, start));

            pager.setRows(resultList);
            pager.setTotal(totalCount);
            return pager;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Find list map by sql list.
     *
     * @param sql the sql
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public List<Map<String, Object>> findListMapBySql(@Nonnull String sql) throws Exception {
        try {
            return findListMapBySql(sql, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Find list map by sql list.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public List<Map<String, Object>> findListMapBySql(@Nonnull String sql, Map<String, Object> parameters)
            throws Exception {
        try {
            return this.executeList(sql, parameters, null, null);
        } catch (Exception ex) {
            throw ex;
        }
    }

    //endregion

    //region Find Util

    /**
     * Create find sql string.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @param orders     the orders
     * @return the string
     */
    private <T> String createFindSql(@Nonnull Class<T> clazz, Map<String, Object> parameters, Order... orders) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String columns = FieldUtils.getFieldJoinStringForSelect(setting);
            String tableName = setting.getTable();
            String schema = setting.getSchema();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT ").append(columns).append(" FROM ").append(tableName).append(" WHERE 1 = 1");
            if (ObjectUtils.isNotNull(parameters)) {
                parameters.entrySet().stream().forEach(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    if (ArrayList.class.equals(value.getClass())) {
                        sqlBuilder.append(" AND ").append(key).append(" IN ( :").append(key).append(")");
                    } else if (ObjectUtils.isNull(value)) {
                        sqlBuilder.append(" AND ").append(key).append(" is NULL");
                    } else {
                        sqlBuilder.append(" AND ").append(key).append(" = :").append(key);
                    }
                });
            }

            if (orders.length > 0) {
                sqlBuilder.append(" ORDER BY ");
                sqlBuilder.append(Arrays.stream(orders).map(Order::toString).collect(Collectors.joining(",")));
            }

            return sqlBuilder.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }

    //endregion

    //region ValidateExist Method

    /**
     * Is exist boolean.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public <T> boolean isExist(@Nonnull Class<T> clazz, String field, Object fieldValue) throws Exception {
        try {
            return count(clazz, field, fieldValue, null) > 0;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Is exist boolean.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @param entity     the entity
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:38:59
     */
    public <T> boolean isExist(@Nonnull Class<T> clazz, String field, Object fieldValue, Object entity) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();
            Long primaryValue = DataSwitch.convertObjectToLong(ReflectionUtils.getFieldValue(entity, primaryKey));
            return count(clazz, field, fieldValue, primaryValue) > 0;
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Is exist boolean.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param parameters the parameters
     * @return the boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public <T> boolean isExist(@Nonnull Class<T> clazz, Map<String, Object> parameters) throws Exception {
        try {
            return count(clazz, parameters, null) > 0;
        } catch (Exception ex) {
            throw ex;
        }
    }
    //endregion

    //region Count Method

    /**
     * Count integer.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param field      the field
     * @param fieldValue the field value
     * @return the integer
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public <T> Integer count(Class<T> clazz, String field, Object fieldValue) throws Exception {
        return count(clazz, field, fieldValue, null);
    }

    /**
     * Count integer.
     *
     * @param <T>          the type parameter
     * @param clazz        the clazz
     * @param field        the field
     * @param fieldValue   the field value
     * @param primaryValue the primary value
     * @return the integer
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public <T> Integer count(Class<T> clazz, String field, Object fieldValue, Long primaryValue) throws Exception {
        try {
            Map<String, Object> parameters = new HashedMap(1);
            parameters.put(field, fieldValue);
            List<Long> primaryValues = new ArrayList<>(1);
            if (ObjectUtils.isNotEmpty(primaryValue)) {
                primaryValues.add(primaryValue);
            }

            return count(clazz, parameters, primaryValues);
        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Count integer.
     *
     * @param <T>           the type parameter
     * @param clazz         the clazz
     * @param parameters    the parameters
     * @param primaryValues the primary values
     * @return the integer
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public <T> Integer count(Class<T> clazz, Map<String, Object> parameters, List<Long> primaryValues) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            primaryValues = primaryValues == null ? new ArrayList<>(0) : primaryValues;
            String sql = this.createCountSql(clazz, parameters, primaryValues);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();
            if (primaryValues.size() == 1) {
                parameters.put(primaryKey, primaryValues.get(0));
            } else if (primaryValues.size() > 1) {
                parameters.put(primaryKey, primaryValues);
            }

            return count(sql, parameters);
        } catch (Exception ex) {
            throw ex;
        }
    }


    /**
     * Gets total count.
     *
     * @param sql        the sql
     * @param parameters the parameters
     * @return the total count
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public Integer count(String sql, Map<String, Object> parameters) throws Exception {
        try {
            Integer count = DataSwitch.convertObjectToInteger(this.executeScalar(sql, parameters));
            return count;
        } catch (Exception e) {
            throw e;
        } finally {
            //sessionFlush(session);
        }

    }


    /**
     * Create is exist sql string.
     *
     * @param <T>           the type parameter
     * @param clazz         the clazz
     * @param parameters    the parameters
     * @param primaryValues
     * @return the string/
     */
    private <T> String createCountSql(@Nonnull Class<T> clazz, Map<String, Object> parameters, List<Long> primaryValues) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();
            parameters = parameters == null ? new HashedMap() : parameters;

            String tableName = setting.getTable();
            String schema = setting.getSchema();
            if (ObjectUtils.isNotEmpty(schema)) {
                tableName = schema.concat(".").concat(tableName);
            }
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append("SELECT COUNT(1) FROM ").append(tableName).append(" WHERE 1 = 1");
            parameters.entrySet().stream().forEach(entry -> {
                String key = entry.getKey();
                Object value = entry.getValue();
                if (ArrayList.class.equals(value.getClass())) {
                    sqlBuilder.append(" AND ").append(key).append(" IN ( :").append(key).append(")");
                } else if (ObjectUtils.isNull(value)) {
                    sqlBuilder.append(" AND ").append(key).append(" IS NULL");
                } else {
                    sqlBuilder.append(" AND ").append(key).append(" = :").append(key);
                }
            });

            if (primaryValues.size() == 1) {
                sqlBuilder.append(" AND ").append(primaryKey).append(" <> ").append(":").append(primaryKey);
            } else if (primaryValues.size() > 1) {
                sqlBuilder.append(" AND ").append(primaryKey).append(" <> ").append(":").append(primaryKey);
            }
            return sqlBuilder.toString();
        } catch (Exception ex) {
            throw ex;
        }
    }


    //endregion

    //region 检查外键使用情况


//    /**
//     * Check foreign key used state boolean.
//     *
//     * @param <T>        the type parameter
//     * @param clazz      the clazz
//     * @param foreignKey the foreign key
//     * @return the boolean
//     * @throws Exception the exception
//     */
//    public <T> boolean checkForeignKeyUsedState(Class<T> clazz, Long foreignKey) throws Exception {
//        List<Long> foreignKeyValues = new ArrayList<>(1);
//        foreignKeyValues.add(foreignKey);
//        return checkForeignKeyUsedState(clazz, foreignKeyValues).size() > 0;
//    }


//    /**
//     * Check foreign key used state boolean.
//     *
//     * @param <T>              the type parameter
//     * @param clazz            the clazz
//     * @param foreignKeyValues the foreign key values
//     * @return the boolean
//     * @throws Exception the exception
//     */
//    public <T> List<Long> checkForeignKeyUsedState(Class<T> clazz, List<Long> foreignKeyValues) throws Exception
//    {
//        try
//        {
//            return this.checkForeignKeyUsedState(clazz, foreignKeyValues, Object.class);
//        }
//        catch (Exception ex)
//        {
//            throw ex;
//        }
//    }


    /**
     * Check foreign key used state filter list.
     *
     * @param <T>        the type parameter
     * @param clazz      the clazz
     * @param foreignKey the foreign key
     * @param classes    the table filters
     * @return boolean boolean
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public <T> boolean checkForeignKeyUsedState(Class<T> clazz, Long foreignKey, Class<?>... classes) throws Exception {
        List<Long> foreignKeyValues = new ArrayList<>(1);
        foreignKeyValues.add(foreignKey);
        return checkForeignKeysUsedState(clazz, foreignKeyValues, classes).size() > 0;
    }


    /**
     * Check foreign key used state filter list.
     *
     * @param <T>              the type parameter
     * @param clazz            the clazz
     * @param foreignKeyValues the foreign key values
     * @param ignoreCLasses    the table filters
     * @return the list
     * @throws Exception the exception
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public <T> List<Long> checkForeignKeysUsedState(Class<T> clazz, List<Long> foreignKeyValues, Class<?>... ignoreCLasses) throws Exception {
        try {
            TableSetting setting = FieldUtils.getEntityInfo(clazz);
            String primaryKey = setting.getPrimaryKey().getFieldInDb();

            foreignKeyValues = foreignKeyValues.stream().distinct().collect(Collectors.toList());
            int foreignKeyValuesSize = foreignKeyValues.size();
            if (foreignKeyValuesSize == 0) {
                return new ArrayList<>(0);
            }
            Map<String, List<String>> foreignKeySetting = ForeignKeyUtils.get(clazz, ignoreCLasses);
            if (foreignKeySetting == null) {
                return new ArrayList<>(0);
            }

            String where = foreignKeyValuesSize > 1 ? " in ( :foreignKey)" : "= :foreignKey";


            List<String> foreignKeyDetail = foreignKeySetting.get(primaryKey);
            if (foreignKeyDetail == null || foreignKeyDetail.size() == 0) {
                return new ArrayList<>(0);
            }
            List<String> selectSql = new ArrayList<>(foreignKeyDetail.size());
            foreignKeyDetail.stream().forEach(table -> {
                StringBuilder innerSql = new StringBuilder();
                innerSql.append("SELECT DISTINCT ").append(primaryKey)
                        .append(" id,'" + table + "' type from ")
                        .append(table).append(" WHERE ")
                        .append(primaryKey).append(where);
                selectSql.add(innerSql.toString());
            });


            if (selectSql.size() == 0) {
                return new ArrayList<>();
            }
            StringBuilder sql = new StringBuilder();
            sql.append("SELECT DISTINCT id as ids FROM (")
                    .append(StringUtils.getCombineString(" UNION ", selectSql)).append(" ) tempDB ");
            Map<String, Object> parameters = new HashMap<>();
            parameters.put("foreignKey", foreignKeyValues);

            List<Map<String, Object>> listMap = this.findListMapBySql(sql.toString(), parameters);
            return listMap.stream().filter(map -> ObjectUtils.isNotEmpty(map.get("ids")))
                    .map(map -> DataSwitch.convertObjectToLong(map.get("ids"))).collect(Collectors.toList());

        } catch (Exception ex) {
            throw ex;
        }
    }

    /**
     * Gets like string.
     *
     * @param value the value
     * @return the like string
     * @author ErebusST
     * @since 2022 -01-07 15:39:00
     */
    public String getLikeString(Object value) {
        String str = ObjectUtils.isNull(value) ? "" : value.toString();
        return StringUtils.concat("%", str, "%");
    }

    //endregion

}
