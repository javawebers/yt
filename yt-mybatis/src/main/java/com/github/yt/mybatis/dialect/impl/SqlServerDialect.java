package com.github.yt.mybatis.dialect.impl;

import com.github.yt.mybatis.dialect.BaseDialect;

/**
 * sql server 实现
 * TODO sql server 支持的不完整
 * @author sheng
 */
public class SqlServerDialect extends BaseDialect {

    @Override
    public String limitOffset(String sql, Integer limitFrom, Integer limitSize) {
        // TODO

        return sql;
    }
}
