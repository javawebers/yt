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
     * 获取异常结果集.
     *
     * @return Object
     */
    Object getErrorResult();

}
