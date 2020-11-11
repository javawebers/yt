package com.github.yt.mybatis.mapper;

import org.apache.ibatis.annotations.DeleteProvider;
import org.apache.ibatis.annotations.InsertProvider;
import org.apache.ibatis.annotations.SelectProvider;
import org.apache.ibatis.annotations.UpdateProvider;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
 * <p>
 * 通用mapper接口
 *
 * @param <T> 实体类泛型类型
 * @author liujiasheng
 */
public interface BaseMapper<T> {

    /**
     * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
     * <p>
     * 批量新增
     * 单条和批量都会调用这个方法
     *
     * @param entityCollection 新增的集合
     * @return 新增的记录数
     */
    @InsertProvider(type = BaseMapperProvider.class, method = "saveBatch")
    int saveBatch(Collection<T> entityCollection);

    /**
     * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
     * <p>
     * 删除（物理）
     *
     * @param paramMap 删除参数
     * @return 删除的记录数
     */
    @DeleteProvider(type = BaseMapperProvider.class, method = "delete")
    int delete(Map<String, Object> paramMap);

    /**
     * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
     * <p>
     * 更新记录
     * 逻辑删除也会调用这个方法
     *
     * @param paramMap 修改相关的参数
     * @return 修改的记录数
     */
    @UpdateProvider(type = BaseMapperProvider.class, method = "update")
    int update(Map<String, Object> paramMap);

    /**
     * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
     * <p>
     * 根据条件查询一条记录
     *
     * @param param 查询参数
     * @return 单个记录，可以为空
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "findList")
    T find(Map<String, Object> param);

    /**
     * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
     * <p>
     * 根据条件查询多条记录
     *
     * @param param 查询参数
     * @return 记录集合，集合不为空，集合中的记录数可以为 0
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "findList")
    List<T> findList(Map<String, Object> param);

    /**
     * ★★★★★ 不要手动调用这个类里的方法，统一调用 BaseService 中提供的方法
     * <p>
     * 根据条件统计数量
     *
     * @param param 查询参数
     * @return 数量
     */
    @SelectProvider(type = BaseMapperProvider.class, method = "count")
    int count(Map<String, Object> param);

}
