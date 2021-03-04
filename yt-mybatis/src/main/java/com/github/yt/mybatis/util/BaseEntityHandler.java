package com.github.yt.mybatis.util;

import com.github.yt.mybatis.YtMybatisProperties;
import com.github.yt.mybatis.entity.BaseEntityValue;

/**
 * BaseEntity 工具类
 * 静态获取登录人信息
 *
 * @author liujiasheng
 */
public class BaseEntityHandler {

    private BaseEntityHandler() {
    }

    private volatile static BaseEntityValue baseEntityValue;

    public static BaseEntityValue getBaseEntityValue() {
        if (baseEntityValue == null) {
            synchronized (BaseEntityHandler.class) {
                if (baseEntityValue == null) {
                    try {
                        YtMybatisProperties ytMybatisProperties = SpringContextUtils.getBean(YtMybatisProperties.class);
                        baseEntityValue = ytMybatisProperties.getEntity().getBaseEntityValue().newInstance();
                    } catch (InstantiationException | IllegalAccessException e) {
                        throw new RuntimeException("实例化 BaseEntityValue 类异常", e);
                    }
                }
            }
        }
        return baseEntityValue;
    }

}
