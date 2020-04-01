package com.github.yt.commons.exception;


/**
 * 字定义异常接口
 *
 * @author liujiasheng
 */
public interface BaseException {

    /**
     * 获取异常码.
     *
     * @return Object
     */
    Object getErrorCode();

    /**
     * 获取传入的枚举值
     *
     * @return Enum
     */
    Enum<?> getErrorEnum();

    /**
     * 获取异常参数
     *
     * @return 参数
     */
    Object[] getErrorParams();

    /**
     * 获取异常结果集.
     *
     * @return Object
     */
    Object getErrorResult();

}
