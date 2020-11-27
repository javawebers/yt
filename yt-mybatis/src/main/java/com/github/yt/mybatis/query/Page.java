package com.github.yt.mybatis.query;


import com.github.yt.commons.query.IPage;

import java.util.List;

/**
 * 分页数据
 *
 * @author liujiasheng
 */
public class Page<T> implements IPage<T> {

    private static final long serialVersionUID = 80238317179585389L;

    /**
     * 页码
     */
    private int pageNo;
    /**
     * 每页的数量
     */
    private int pageSize;

    /**
     * 总记录数
     */
    private int totalCount;

    /**
     * 结果集
     */
    private List<T> data;

    public Page(int pageNo, int pageSize, int totalCount, List<T> data) {
        setPageNo(pageNo).
                setPageSize(pageSize).
                setTotalCount(totalCount).
                setData(data);
    }

    @Override
    public int getPageNo() {
        return pageNo;
    }

    public Page<T> setPageNo(int pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    @Override
    public int getPageSize() {
        return pageSize;
    }

    public Page<T> setPageSize(int pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    @Override
    public int getTotalCount() {
        return totalCount;
    }

    public Page<T> setTotalCount(int totalCount) {
        this.totalCount = totalCount;
        return this;
    }

    @Override
    public List<T> getData() {
        return data;
    }

    public Page<T> setData(List<T> data) {
        this.data = data;
        return this;
    }
}
