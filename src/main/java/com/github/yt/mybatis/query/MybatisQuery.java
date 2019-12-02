package com.github.yt.mybatis.query;

import com.github.yt.commons.query.PageQuery;

import java.util.List;
import java.util.Map;

/**
 * mybatis 查询条件
 */
public interface MybatisQuery<T extends MybatisQuery> extends PageQuery<T> {

    // apis

    /**
     * 添加扩展的查询字段
     *
     * @param extendSelectColumn 扩展 select 的 column ,如: "count(num) as countNum"
     * @return this
     */
    T addExtendSelectColumn(String extendSelectColumn);

    /**
     * 添加排除的查询字段
     * 如: 忽略敏感字段, 业务中不需要的字段等
     * 如: create_time
     *
     * @param excludeSelectColumn 排除的查询字段
     * @return this
     */
    T addExcludeSelectColumn(String excludeSelectColumn);

    /**
     * 排除所有默认的表字段, 结合 addSelectColumn 使用,只查询业务中用到的字段
     *
     * @return this
     */
    T excludeAllSelectColumn();

    /**
     * 更新基本字段,如 modifyTime, modifierId, modifierName
     *
     * @param updateBaseColumn true/false,默认true
     * @return this
     */
    T updateBaseColumn(boolean updateBaseColumn);

    /**
     * 添加 mybatis 的参数 #{name}
     *
     * @param paramName  参数名 name
     * @param paramValue 参数值 张三
     * @return this
     */
    T addParam(String paramName, Object paramValue);

    /**
     * 添加更新的字段
     * 多个更新字段 "," 分割
     *
     * @param updateColumn 更新的字段 "name = '张三'"
     * @return this
     */
    T addUpdate(String updateColumn);

    /**
     * 添加查询条件
     * 多个查询条件之间 "and" 拼接
     *
     * @param where 查询条件, username = #{username}
     * @return this
     */
    T addWhere(String where);

    /**
     * 添加排序字段
     *
     * @param columns 排序字段
     * @return this
     */
    T addOrderBy(String columns);

    /**
     * 添加分组字段
     *
     * @param columns 分组字段
     * @return this
     */
    T addGroupBy(String columns);

    /**
     * limit
     *
     * @param from 开始条数
     * @param size 查询条数
     * @return this
     */
    T limit(int from, int size);

    /**
     * 添加 join 查询
     *
     * @param joinType                 joinType
     * @param tableNameAndOnConditions 表名和连接条件, 如: "t_user t2 on t.user_id = t2.userId"
     * @return this
     */
    T addJoin(QueryJoinType joinType, String tableNameAndOnConditions);

    ////// take 参数
    Map<String, Object> takeParam();

    boolean takeUpdateBaseColumn();

    List<QueryInCondition> takeInParamList();

    List<String> takeUpdateColumnList();

    List<String> takeExtendSelectColumnList();

    List<String> takeExcludeSelectColumnList();

    boolean takeExcludeAllSelectColumn();

    List<String> takeWhereList();

    List<String> takeOrderByList();

    String takeGroupBy();

    List<QueryJoin> takeJoinList();

    Integer takeLimitFrom();

    Integer takeLimitSize();

}
