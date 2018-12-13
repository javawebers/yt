package com.github.yt.commons.exception;

/**
 * 已知异常处理器，支持多实现类
 * 将已知异常转换为BaseAccidentException或者BaseErrorException
 * @author liujiasheng
 */
public interface BaseExceptionConverter {
    Exception convertToBaseException(Exception e);
}
