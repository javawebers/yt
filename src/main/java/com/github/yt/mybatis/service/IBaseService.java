package com.github.yt.mybatis.service;

import com.github.yt.commons.query.Page;
import com.github.yt.commons.query.Query;
import com.github.yt.mybatis.mapper.BaseMapper;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * 服务接口的基类
 *
 * @param <T>此服务接口服务的数据模型，即model
 */
public interface IBaseService<T> {

    /**
     * 获取mapper
     * @param <M> Mapper类型
     * @return mapper
     */
    <M extends BaseMapper<T>>M getMapper();


    /**
     * 保存实体
     * @param entity 待保存的实体
     * @return 保存的条数
     */
    int save(T entity);

    /**
     * 批量保存
     *
     * @param entities 待保存实体列表
     * @return 保存的条数
     */
    int saveBatch(Collection<T> entities);

    /**
     * 根据主键更新实体
     *
     * @param entity 业务实体
     * @param fieldColumnNames 更新的字段对应数据库字段
     * @return 更新的条数
     */
    int update(T entity, String... fieldColumnNames);

    /**
     * 只更新非空字段
     *
     * @param entity 业务实体
     * @param fieldColumnNames 更新的字段对应数据库字段
     * @return 更新的条数
     */
    int updateForSelective(T entity, String... fieldColumnNames);

    /**
     * 根据条件更新
     * @param entityCondition 实体条件
     * @param query 扩展条件
     * @return 更新条数
     */
    int updateByCondition(T entityCondition, Query query);

    /**
     * 逻辑删除实体
     * @param entityCondition 删除条件类
     * @param query 删除辅助类
     * @return 删除的条数
     */
    int logicDelete(T entityCondition, Query query);
    int logicDelete(T entityCondition);


    /**
     * 删除实体
     *
     * @param clazz clazz
     * @param id    业务实体ID
     * @return 删除的条数
     */
    int delete(Class<T> clazz, Serializable id);

    /**
     * 删除实体
     * @param entityCondition 删除条件类
     * @param query 删除辅助类
     * @return 删除的条数
     */
    int delete(T entityCondition, Query query);
    int delete(T entityCondition);

    /**
     * 根据ID获取实体
     *
     * @param clazz 实体类型
     * @param id 业务实体ID
     * @return 业务实体
     */
    T get(Class<T> clazz, Serializable id);

    /**
     * 按条件查询一条记录
     *
     * @param entity 业务实体类或业务查询实体类
     * @return 业务实体
     */
    T find(T entity);

    /**
     * 按条件查询一条记录
     *
     * @param entity 业务实体类或业务查询实体类
     * @param query 查询辅助类
     * @return 业务实体
     */
    T find(T entity, Query query);

    /**
     * 按条件查询记录集合
     *
     * @param entityCondition 业务实体类或业务查询实体类
     * @return 业务实体集合
     */
    List<T> findList(T entityCondition);

    /**
     * 按条件查询记录集合
     *
     * @param entityCondition       业务实体类或业务查询实体类
     * @param query 查询辅助类
     * @return 业务实体集合
     */
    List<T> findList(T entityCondition, Query query);

    /**
     * 查询数量
     *
     * @param entityCondition       业务实体类或业务查询实体类
     * @return 数量
     */
    int count(T entityCondition);

    /**
     * 查询数量
     *
     * @param entityCondition       业务实体类或业务查询实体类
     * @param query 查询辅助类
     * @return 数量
     */
    int count(T entityCondition, Query query);

    /**
     * 获取数据
     *
     * @param entity       查询业务实体
     * @param query 查询辅助类
     * @return 根据查询条件查询的查询结果集
     */
    Page<T> findPage(T entity, Query query);
}
