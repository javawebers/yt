package com.github.yt.commons.exception;

/**
 * 已知异常处理器，支持多实现类
 * 将已知异常转换为BaseAccidentException或者BaseErrorException
 *
 * @author liujiasheng
 */
public interface BaseExceptionConverter {
    /**
     * 已知异常转换为 BaseAccidentException，非已知异常不进行处理
     *
     * @param e 已知异常
     * @return BaseAccidentException
     */
    Exception convertToBaseException(Exception e);
}
