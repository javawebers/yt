package com.github.yt.mybatis.query;

import com.github.yt.mybatis.YtMybatisConfig;

import java.util.List;

/**
 * 分页工具类
 *
 * @author liujiasheng
 */
public class PageUtils {


    /**
     * 根据老分页生成新分页
     *
     * @param oldPage    原始分页信息
     * @param resultList 分页数据集
     * @param <R>        分页数据类型
     * @param <O>        原始分页数据类型
     * @return 新的分页信息
     */
    public static <R, O> Page createPage(Page<O> oldPage, List<R> resultList) {
        return createPage(oldPage.getPageNo(), oldPage.getPageSize(), oldPage.getTotalCount(), resultList);
    }

    /**
     * 创建一个分页数据
     *
     * @param pageNo     页码
     * @param pageSize   每页条数
     * @param totalCount 总条数
     * @param data       分页数据集
     * @param <R>        分页数据类型
     * @return 分页信息
     */
    public static <R> Page createPage(int pageNo, int pageSize, int totalCount, List<R> data) {
        return new Page<R>()
                .initKey(YtMybatisConfig.pageNoName, YtMybatisConfig.pageSizeName, YtMybatisConfig.pageTotalCountName, YtMybatisConfig.pageDataName)
                .initValue(pageNo, pageSize, totalCount, data);
    }


}
