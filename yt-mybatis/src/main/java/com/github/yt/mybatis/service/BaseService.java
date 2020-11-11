package com.github.yt.mybatis.service;

import com.github.yt.commons.exception.Assert;
import com.github.yt.mybatis.query.Page;
import com.github.yt.mybatis.query.PageUtils;
import com.github.yt.mybatis.YtMybatisConfig;
import com.github.yt.mybatis.YtMybatisExceptionEnum;
import com.github.yt.mybatis.dialect.DialectHandler;
import com.github.yt.mybatis.entity.YtColumnType;
import com.github.yt.mybatis.mapper.BaseMapper;
import com.github.yt.mybatis.query.*;
import com.github.yt.mybatis.util.BaseEntityHandler;
import com.github.yt.mybatis.util.EntityUtils;
import com.github.yt.mybatis.util.SpringContextUtils;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.persistence.Id;
import javax.persistence.Transient;
import java.io.Serializable;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * 通用service实现
 *
 * @param <T> 实体类泛型类型
 * @author liujiasheng
 */
public abstract class BaseService<T> implements IBaseService<T> {
    private static final String ID_MUST_NOT_BE_NULL = "The given id must not be null!";
    private static final String ENTITY_MUST_NOT_BE_NULL = "The given entity must not be null!";

    private Class<T> domainClass;

    private BaseMapper<?> mapper;

    @Override
    @SuppressWarnings("unchecked")
    public <M extends BaseMapper<T>> M getMapper() {
        // 如果子类中没有getMapper方法会调用baseService中的getMapper方法，在这个方法中直接获取mapper属性
        if (mapper == null) {
            synchronized (this) {
                if (mapper == null) {
                    Field mapperField = EntityUtils.getField(this.getClass(), "mapper");
                    mapper = (M) EntityUtils.getValue(this, mapperField);
                    // 没有覆盖getMapper方法，也没有mapper字段，抛出异常
                    Assert.notNull(mapper, YtMybatisExceptionEnum.CODE_85);
                }
            }
        }
        return (M) mapper;
    }

    /**
     * 根据泛型获取实体类类型
     *
     * @return 实体类类型
     */
    @SuppressWarnings("unchecked")
    private Class<T> getEntityClass() {
        if (domainClass == null) {
            synchronized (this) {
                if (domainClass == null) {
                    Type[] actualTypeArguments = ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments();
                    if (actualTypeArguments.length == 0) {
                        throw new RuntimeException("该 mapper 没有泛型参数");
                    }
                    domainClass = (Class<T>) actualTypeArguments[0];
                }
            }
        }
        return domainClass;
    }

    private T newEntityInstance() {
        try {
            return getEntityClass().newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public int save(T entity) {
        List<T> entityList = Collections.singletonList(entity);
        return saveBatch(entityList, false);
    }

    @Override
    public int saveBatch(Collection<T> entityCollection) {
        return saveBatch(entityCollection, true);
    }

    /**
     * 所有的保存都走这里
     */
    private int saveBatch(Collection<T> entityCollection, boolean batch) {
        if (entityCollection == null || entityCollection.size() == 0) {
            return 0;
        }
        setEntityId(entityCollection, batch);
        setCreatorInfo(entityCollection);
        setDeleteFlag(entityCollection);
        return getMapper().saveBatch(entityCollection);
    }


    @Override
    public int updateById(T entity, String... fieldColumnNames) {
        return updateById(entity, true, fieldColumnNames);
    }

    @Override
    public int updateById(T entity, boolean defaultSettingDeleteFlag, String... fieldColumnNames) {
        org.springframework.util.Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
        org.springframework.util.Assert.notNull(EntityUtils.getIdValue(entity), ID_MUST_NOT_BE_NULL);
        return updateById(entity, true, defaultSettingDeleteFlag, fieldColumnNames);
    }

    @Override
    public int updateForSelectiveById(T entity, String... fieldColumnNames) {
        return updateForSelectiveById(entity, true, fieldColumnNames);
    }

    @Override
    public int updateForSelectiveById(T entity, boolean defaultSettingDeleteFlag, String... fieldColumnNames) {
        org.springframework.util.Assert.notNull(entity, ENTITY_MUST_NOT_BE_NULL);
        org.springframework.util.Assert.notNull(EntityUtils.getIdValue(entity), ID_MUST_NOT_BE_NULL);
        return updateById(entity, false, defaultSettingDeleteFlag, fieldColumnNames);
    }

    @Override
    public int updateBatchById(Collection<T> entities, String... fieldColumnNames) {
        return updateBatchById(entities, true, fieldColumnNames);
    }

    @Override
    public int updateBatchById(Collection<T> entities, boolean defaultSettingDeleteFlag, String... fieldColumnNames) {
        // TODO: 2020-11-11 目前先循环更新，需要改成一条语句更新多条记录方式
        int count = 0;
        for (T entity : entities) {
            count += updateById(entity, defaultSettingDeleteFlag, fieldColumnNames);
        }
        return count;
    }

    @Override
    public int updateBatchForSelectiveById(Collection<T> entities, String... fieldColumnNames) {
        return updateBatchForSelectiveById(entities, true, fieldColumnNames);
    }

    @Override
    public int updateBatchForSelectiveById(Collection<T> entities, boolean defaultSettingDeleteFlag, String... fieldColumnNames) {
        // TODO: 2020-11-11 目前先循环更新，需要改成一条语句更新多条记录方式
        int count = 0;
        for (T entity : entities) {
            count += updateForSelectiveById(entity, defaultSettingDeleteFlag, fieldColumnNames);
        }
        return count;
    }

    private int updateById(T entity, boolean isUpdateNullField, boolean defaultSettingDeleteFlag, String... selectedFieldColumnNames) {
        // 判断是否更新指定字段
        Set<String> selectedFieldColumnNameSet = getSelectedFieldColumnNameSet(selectedFieldColumnNames, entity);
        Query query = new Query();
        setModifier(entity);

        Field idField = EntityUtils.getIdField(entity.getClass());
        for (Field field : EntityUtils.getTableFieldList(entity.getClass())) {
            field.setAccessible(true);
            //处理主键
            if (null != field.getAnnotation(Id.class) || null != field.getAnnotation(Transient.class)) {
                continue;
            }
            if (!isUpdateNullField) {
                if (EntityUtils.getValue(entity, field) == null) {
                    continue;
                }
            }
            if (selectedFieldColumnNameSet != null && selectedFieldColumnNameSet.size() > 0) {
                if (!selectedFieldColumnNameSet.contains(EntityUtils.getFieldColumnName(field))) {
                    continue;
                }
            }
            query.update(DialectHandler.getDialect().getColumnName(field), EntityUtils.getValue(entity, field));
        }
        query.equal(DialectHandler.getDialect().getColumnName(idField), EntityUtils.getValue(entity, idField));
        T entityCondition = newEntityInstance();
        defaultSettingDeleteFlag(entityCondition, defaultSettingDeleteFlag);
        return getMapper().update(ParamUtils.getParamMap(entityCondition, query, false));
    }

    @Override
    public int update(T entityCondition, MybatisQuery<?> query) {
        return update(entityCondition, query, true);
    }

    @Override
    public int update(T entityCondition, MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        defaultSettingDeleteFlag(entityCondition, defaultSettingDeleteFlag);
        setUpdateBaseColumn(entityCondition.getClass(), query);
        return getMapper().update(ParamUtils.getParamMap(entityCondition, query, false));
    }

    @Override
    public int update(MybatisQuery<?> query) {
        return update(query, true);
    }

    @Override
    public int update(MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        T entityCondition = newEntityInstance();
        return update(entityCondition, query, defaultSettingDeleteFlag);
    }

    @Override
    public int logicDeleteOneById(Serializable id) {
        int num = logicDeleteById(id);
        if (num != 1) {
            throw new EmptyResultDataAccessException("逻辑删除的数据不为1条，删除了: " + num + "条数据，id: " + id, 1);
        }
        return num;
    }

    @Override
    public int logicDeleteById(Serializable id) {
        org.springframework.util.Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        Field idField = EntityUtils.getIdField(getEntityClass());
        Query query = new Query();
        query.equal(DialectHandler.getDialect().getColumnName(idField), id);
        T entityCondition = newEntityInstance();
        int num = this.logicDelete(entityCondition, query);
        Assert.le(num, 1, YtMybatisExceptionEnum.CODE_77);
        return num;
    }

    @Override
    public int logicDeleteBatchByIds(Object firstValue, Serializable... moreIds) {
        org.springframework.util.Assert.notNull(firstValue, ID_MUST_NOT_BE_NULL);
        Collection<?> idList = convertToCollection(firstValue, moreIds);

        Field idField = EntityUtils.getIdField(getEntityClass());
        Query query = new Query();
        query.in(DialectHandler.getDialect().getColumnName(idField), idList);
        T entityCondition = newEntityInstance();
        int num = this.logicDelete(entityCondition, query);
        Assert.le(num, idList.size(), YtMybatisExceptionEnum.CODE_77);
        return num;
    }

    @Override
    public int logicDelete(T entityCondition, MybatisQuery<?> query) {
        setUpdateDeleteFlag(entityCondition, query);
        setUpdateBaseColumn(entityCondition.getClass(), query);
        return getMapper().update(ParamUtils.getParamMap(entityCondition, query, false));
    }

    @Override
    public int logicDelete(MybatisQuery<?> query) {
        return logicDelete(newEntityInstance(), query);
    }


    @Override
    public int logicDelete(T entityCondition) {
        org.springframework.util.Assert.notNull(entityCondition, "逻辑删除条件不能为空");
        return logicDelete(entityCondition, new Query());
    }

    @Override
    public int deleteOneById(Serializable id) {
        int num = deleteById(id);
        if (num != 1) {
            throw new EmptyResultDataAccessException("逻辑删除的数据不为1条，删除了: " + num + "条数据，id: " + id, 1);
        }
        return num;
    }

    @Override
    public int deleteById(Serializable id) {
        org.springframework.util.Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        Field idField = EntityUtils.getIdField(getEntityClass());
        Query query = new Query();
        query.equal(DialectHandler.getDialect().getColumnName(idField), id);
        int num = this.delete(newEntityInstance(), query);
        Assert.le(num, 1, YtMybatisExceptionEnum.CODE_76);
        return num;
    }

    @Override
    public int delete(T entityCondition) {
        return delete(entityCondition, new Query());
    }

    @Override
    public int delete(T entityCondition, MybatisQuery<?> query) {
        return getMapper().delete(ParamUtils.getParamMap(entityCondition, query, false));
    }

    @Override
    public int delete(MybatisQuery<?> query) {
        return delete(newEntityInstance(), query);
    }

    @Override
    public T findById(Serializable id) {
        return findById(id, true);
    }

    @Override
    public T findById(Serializable id, boolean defaultSettingDeleteFlag) {
        org.springframework.util.Assert.notNull(id, ID_MUST_NOT_BE_NULL);
        Field idField = EntityUtils.getIdField(getEntityClass());
        Query query = new Query();
        query.equal(DialectHandler.getDialect().getColumnName(idField), id);
        T entityCondition = newEntityInstance();
        return find(entityCondition, query, defaultSettingDeleteFlag);
    }

    @Override
    public T findOneById(Serializable id) {
        return findOneById(id, true);
    }

    @Override
    public T findOneById(Serializable id, boolean defaultSettingDeleteFlag) {
        T entity = findById(id, defaultSettingDeleteFlag);
        if (entity == null) {
            throw new EmptyResultDataAccessException("没有查询到数据，id: " + id, 1);
        }
        return entity;
    }

    @Override
    public T find(T entityCondition) {
        return find(entityCondition, true);
    }

    @Override
    public T find(T entityCondition, boolean defaultSettingDeleteFlag) {
        return find(entityCondition, new Query(), defaultSettingDeleteFlag);
    }

    @Override
    public T findOne(T entityCondition) {
        return findOne(entityCondition, true);
    }

    @Override
    public T findOne(T entityCondition, boolean defaultSettingDeleteFlag) {
        T entity = find(entityCondition, defaultSettingDeleteFlag);
        if (entity == null) {
            throw new EmptyResultDataAccessException("没有查询到数据", 1);
        }
        return entity;
    }

    @Override
    public T findOne(T entityCondition, MybatisQuery<?> query) {
        return findOne(entityCondition, query, true);
    }

    @Override
    public T findOne(T entityCondition, MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        T entity = find(entityCondition, query, defaultSettingDeleteFlag);
        if (entity == null) {
            throw new EmptyResultDataAccessException("没有查询到数据", 1);
        }
        return entity;
    }

    @Override
    public T findOne(MybatisQuery<?> query) {
        return findOne(query, true);
    }

    @Override
    public T findOne(MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        return findOne(newEntityInstance(), query, defaultSettingDeleteFlag);
    }

    @Override
    public T find(T entityCondition, MybatisQuery<?> query) {
        return find(entityCondition, query, true);
    }

    @Override
    public T find(T entityCondition, MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        if (query.takeLimitFrom() == null) {
            query.limit(0, 2);
        }
        defaultSettingDeleteFlag(entityCondition, defaultSettingDeleteFlag);
        return getMapper().find(ParamUtils.getParamMap(entityCondition, query, true));
    }

    @Override
    public T find(MybatisQuery<?> query) {
        return find(query, true);
    }

    @Override
    public T find(MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        return find(newEntityInstance(), query, defaultSettingDeleteFlag);
    }

    @Override
    public List<T> findList(T entityCondition) {
        return findList(entityCondition, true);
    }

    @Override
    public List<T> findList(T entityCondition, boolean defaultSettingDeleteFlag) {
        return findList(entityCondition, new Query(), defaultSettingDeleteFlag);
    }

    @Override
    public List<T> findList(T entityCondition, MybatisQuery<?> query) {
        return findList(entityCondition, query, true);
    }

    @Override
    public List<T> findList(T entityCondition, MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        defaultSettingDeleteFlag(entityCondition, defaultSettingDeleteFlag);
        return getMapper().findList(ParamUtils.getParamMap(entityCondition, query, true));
    }

    @Override
    public List<T> findList(MybatisQuery<?> query) {
        return findList(query, true);
    }

    @Override
    public List<T> findList(MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        return findList(newEntityInstance(), query, defaultSettingDeleteFlag);
    }

    @Override
    public int count(T entityCondition) {
        return count(entityCondition, true);
    }

    @Override
    public int count(T entityCondition, boolean defaultSettingDeleteFlag) {
        return count(entityCondition, new Query(), defaultSettingDeleteFlag);
    }

    @Override
    public int count(T entityCondition, MybatisQuery<?> query) {
        return count(entityCondition, query, true);
    }

    @Override
    public int count(T entityCondition, MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        defaultSettingDeleteFlag(entityCondition, defaultSettingDeleteFlag);
        return getMapper().count(ParamUtils.getParamMap(entityCondition, query, true));

    }

    @Override
    public int count(MybatisQuery<?> query) {
        return count(query, true);
    }

    @Override
    public int count(MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        return count(newEntityInstance(), query, defaultSettingDeleteFlag);
    }

    @Override
    public Page<T> findPage(T entityCondition, MybatisQuery<?> query) {
        return findPage(entityCondition, query, true);
    }

    @Override
    public Page<T> findPage(T entityCondition, MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        defaultSettingDeleteFlag(entityCondition, defaultSettingDeleteFlag);
        // 设置页数页码
        ParamUtils.setPageInfo(query);
        Map<String, Object> paramMap = ParamUtils.getParamMap(entityCondition, query, true);
        query.limit((query.takePageNo() - 1) * query.takePageSize(), query.takePageSize());
        // 尽量减少 count 操作
        List<T> entityList = getMapper().findList(paramMap);
        int count;
        if (entityList.size() == 0) {
            if (query.takePageNo() == 0) {
                count = 0;
            } else {
                count = getMapper().count(paramMap);
            }
        } else if (entityList.size() == query.takePageSize()) {
            count = getMapper().count(paramMap);
        } else {
            count = entityList.size() + (query.takePageNo() - 1) * query.takePageSize();
        }
        return PageUtils.createPage(query.takePageNo(), query.takePageSize(), count, entityList);
    }

    @Override
    public Page<T> findPage(MybatisQuery<?> query) {
        return findPage(query, true);
    }

    @Override
    public Page<T> findPage(MybatisQuery<?> query, boolean defaultSettingDeleteFlag) {
        return findPage(newEntityInstance(), query, defaultSettingDeleteFlag);
    }

    /**
     * id 为 String 类型，自动生成 uuid id
     * id 为空直接返回
     *
     * @param entityCollection 实体集合
     * @param batch            是否批量保存
     */
    private void setEntityId(Collection<T> entityCollection, boolean batch) {
        // 设置id字段的值
        Class<T> entityClass = EntityUtils.getEntityClass(entityCollection);
        Field idField;
        try {
            idField = EntityUtils.getIdField(entityClass);
        } catch (Exception e) {
            return;
        }
        if (idField.getType() != String.class) {
            return;
        }

        int i = 0;

        YtMybatisConfig ytMybatisConfig = SpringContextUtils.getBean(YtMybatisConfig.class);
        YtMybatisConfig.IdGenerateRule idGenerateRule = ytMybatisConfig.getEntity().getIdGenerateRule();

        String generateIdValue;
        switch (idGenerateRule) {
            case TABLE_DATE_RANDOM:
                generateIdValue = EntityUtils.getTableName(entityClass) +
                        "_" + new SimpleDateFormat("yyyyMMddHHmmssSSS").format(new Date()) +
                        "_" + randomString(ytMybatisConfig.getEntity().getIdRandomNum());
                break;
            case UUID:
            default:
                generateIdValue = UUID.randomUUID().toString().replace("-", "");
                break;
        }

        for (T entity : entityCollection) {
            String idValue = (String) EntityUtils.getValue(entity, idField);
            if (idValue == null) {
                if (batch) {
                    idValue = generateIdValue + "_" + i;
                } else {
                    idValue = generateIdValue;
                }
                try {
                    idField.set(entity, idValue);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
                i++;
            }
        }
    }

    /**
     * 设置创建人信息
     *
     * @param entityCollection 实体类集合
     */
    private void setCreatorInfo(Collection<T> entityCollection) {
        Class<T> entityClass = EntityUtils.getEntityClass(entityCollection);
        Field founderIdField = EntityUtils.getYtColumnField(entityClass, YtColumnType.FOUNDER_ID);
        Field founderNameField = EntityUtils.getYtColumnField(entityClass, YtColumnType.FOUNDER_NAME);
        Field createTimeField = EntityUtils.getYtColumnField(entityClass, YtColumnType.CREATE_TIME);

        Object founderId = null;
        String founderName = null;
        Date createTime = null;
        if (founderIdField != null) {
            founderId = BaseEntityHandler.getBaseEntityValue().getFounderId();
        }
        if (founderNameField != null) {
            founderName = BaseEntityHandler.getBaseEntityValue().getFounderName();
        }
        if (createTimeField != null) {
            createTime = new Date();
        }

        for (T entity : entityCollection) {
            if (founderIdField != null && founderId != null) {
                Object trueFounderId = EntityUtils.getValue(entity, founderIdField);
                if (trueFounderId == null) {
                    EntityUtils.setValue(entity, founderIdField, founderId);
                }
            }
            if (founderNameField != null && founderName != null) {
                Object trueFounderName = EntityUtils.getValue(entity, founderNameField);
                if (trueFounderName == null) {
                    EntityUtils.setValue(entity, founderNameField, founderName);
                }
            }
            if (createTimeField != null) {
                Object trueCreateTime = EntityUtils.getValue(entity, createTimeField);
                if (trueCreateTime == null) {
                    EntityUtils.setValue(entity, createTimeField, createTime);
                }
            }
        }
    }

    /**
     * 设置 deleteFlag 默认 false
     *
     * @param entityCollection 实体类集合
     */
    private void setDeleteFlag(Collection<T> entityCollection) {
        Class<T> entityClass = EntityUtils.getEntityClass(entityCollection);
        Field deleteFlagField = EntityUtils.getYtColumnField(entityClass, YtColumnType.DELETE_FLAG);
        for (T entity : entityCollection) {
            if (deleteFlagField != null) {
                Object deleteFlag = EntityUtils.getValue(entity, deleteFlagField);
                if (deleteFlag == null) {
                    EntityUtils.setValue(entity, deleteFlagField, false);
                }
            }
        }
    }

    /**
     * 设置修改人信息
     *
     * @param entity 实体类
     */
    private void setModifier(T entity) {
        Field modifierIdField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.MODIFIER_ID);
        Field modifierNameField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.MODIFIER_NAME);
        Field modifyTimeField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.MODIFY_TIME);
        if (modifierIdField != null) {
            Object modifierId = BaseEntityHandler.getBaseEntityValue().getModifierId();
            Object trueModifierId = EntityUtils.getValue(entity, modifierIdField);
            if (trueModifierId == null) {
                EntityUtils.setValue(entity, modifierIdField, modifierId);
            }
        }
        if (modifierNameField != null) {
            String modifierName = BaseEntityHandler.getBaseEntityValue().getModifierName();
            Object trueModifierName = EntityUtils.getValue(entity, modifierNameField);
            if (trueModifierName == null) {
                EntityUtils.setValue(entity, modifierNameField, modifierName);
            }
        }
        if (modifyTimeField != null) {
            Date modifyTime = new Date();
            Object trueModifyTime = EntityUtils.getValue(entity, modifyTimeField);
            if (trueModifyTime == null) {
                EntityUtils.setValue(entity, modifyTimeField, modifyTime);
            }
        }
    }

    private void setUpdateBaseColumn(Class<?> entityClass, MybatisQuery<?> query) {
        if (query.takeUpdateBaseColumn()) {
            Field modifierIdField = EntityUtils.getYtColumnField(entityClass, YtColumnType.MODIFIER_ID);
            Field modifierNameField = EntityUtils.getYtColumnField(entityClass, YtColumnType.MODIFIER_NAME);
            Field modifyTimeField = EntityUtils.getYtColumnField(entityClass, YtColumnType.MODIFY_TIME);
            if (modifierIdField != null) {
                query.update(DialectHandler.getDialect().getColumnName(modifierIdField), BaseEntityHandler.getBaseEntityValue().getModifierId());
            }
            if (modifierNameField != null) {
                query.update(DialectHandler.getDialect().getColumnName(modifierNameField), BaseEntityHandler.getBaseEntityValue().getModifierName());
            }
            if (modifyTimeField != null) {
                query.update(DialectHandler.getDialect().getColumnName(modifyTimeField), new Date());
            }
        }
    }

    private void setUpdateDeleteFlag(T entityCondition, MybatisQuery<?> query) {
        Field deleteFlagField = EntityUtils.getYtColumnField(entityCondition.getClass(), YtColumnType.DELETE_FLAG);
        org.springframework.util.Assert.notNull(deleteFlagField, "逻辑删除字段不存在");

        String deleteFlagColumn = DialectHandler.getDialect().getColumnName(deleteFlagField);
        query.update(deleteFlagColumn, true);
        query.equal(deleteFlagColumn, false);
    }


    private Set<String> getSelectedFieldColumnNameSet(String[] selectedFieldColumnNames, T entity) {
        Set<String> selectedFieldColumnNameSet = null;
        if (selectedFieldColumnNames != null && selectedFieldColumnNames.length > 0) {
            selectedFieldColumnNameSet = new HashSet<>(Arrays.asList(selectedFieldColumnNames));
            Field modifierIdField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.MODIFIER_ID);
            Field modifierNameField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.MODIFIER_NAME);
            Field modifyTimeField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.MODIFY_TIME);
            if (modifierIdField != null) {
                selectedFieldColumnNameSet.add(EntityUtils.getFieldColumnName(modifierIdField));
            }
            if (modifierNameField != null) {
                selectedFieldColumnNameSet.add(EntityUtils.getFieldColumnName(modifierNameField));
            }
            if (modifyTimeField != null) {
                selectedFieldColumnNameSet.add(EntityUtils.getFieldColumnName(modifyTimeField));
            }
        }
        return selectedFieldColumnNameSet;
    }

    /**
     * 获得一个随机的字符串
     *
     * @param length 字符串的长度
     * @return 随机字符串
     */
    private static String randomString(int length) {
        String baseString = "0123456789abcdefghijklmnopqrstuvwxyz";
        final StringBuilder sb = new StringBuilder(length);
        if (length < 1) {
            length = 1;
        }
        int baseLength = baseString.length();
        for (int i = 0; i < length; i++) {
            int number = ThreadLocalRandom.current().nextInt(baseLength);
            sb.append(baseString.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 默认设置 deleteFlag
     *
     * @param entity                   实体类
     * @param defaultSettingDeleteFlag 是否默认设置 deleteFlag（不是设置 deleteFlag）
     */
    private void defaultSettingDeleteFlag(T entity, boolean defaultSettingDeleteFlag) {
        // 判断是否设置
        if (defaultSettingDeleteFlag) {
            Field deleteFlagField = EntityUtils.getYtColumnField(entity.getClass(), YtColumnType.DELETE_FLAG);
            // 判断是否有 deleteFlag 字段
            if (deleteFlagField != null) {
                // 获取当前 deleteFlag 值，如果为空就设置
                Object deleteFlag = EntityUtils.getValue(entity, deleteFlagField);
                if (deleteFlag == null) {
                    // 将 deleteFlag 设置成 false
                    EntityUtils.setValue(entity, deleteFlagField, false);
                }
            }
        }
    }


    private Collection<?> convertToCollection(Object firstValue, Serializable... moreIds) {
        if (firstValue instanceof Collection) {
            return (Collection<?>)firstValue;
        } else if (firstValue.getClass().isArray()) {
            int length = Array.getLength(firstValue);
            Object[] os = new Object[length];
            for (int i = 0; i < os.length; i++) {
                os[i] = Array.get(firstValue, i);
            }
            return Arrays.asList(os);
        } else {
            List<Object> list = new ArrayList<>();
            list.add(firstValue);
            list.addAll(Arrays.asList(moreIds));
            return list;
        }
    }

}
