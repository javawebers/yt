package com.github.yt.mybatis.dialect.impl;

import com.github.yt.mybatis.dialect.BaseDialect;
import com.github.yt.mybatis.dialect.Dialect;
import com.github.yt.mybatis.util.EntityUtils;

import java.lang.reflect.Field;

/**
 * mysql 实现
 *
 * @author sheng
 */
public class MysqlDialect extends BaseDialect {

    public static Dialect getInstance() {
        return new MysqlDialect();
    }

    /**
     * mysql 的转义符
     */
    private static final String ESCAPE = "`";

    @Override
    public String getTableAlas() {
        return ESCAPE + TABLE_ALAS + ESCAPE;
    }

    @Override
    public String getColumnName(Field field) {
        return ESCAPE + EntityUtils.getFieldColumnName(field) + ESCAPE;
    }
}
