package com.github.yt.commons.exception;

import java.text.MessageFormat;

/**
 * @author liujiasheng
 */
public class ExceptionUtils {

    /**
     * 获取异常码.
     *
     * @param errorEnum errorEnum
     * @return Object
     */
    public static Object getExceptionCode(Enum<?> errorEnum) {
        Object errorCode;
        try {
            errorCode = errorEnum.getClass().getDeclaredField("code").get(errorEnum);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            errorCode = errorEnum.name();
        }
        return errorCode;
    }

    /**
     * 获取异常消息.
     *
     * @param errorEnum errorEnum
     * @param params    params
     * @return String
     */
    public static String getExceptionMessage(Enum<?> errorEnum, Object... params) {
        try {
            String errorMessage = (String) errorEnum.getClass().getDeclaredField("message").get(errorEnum);
            return MessageFormat.format(errorMessage, params);
        } catch (IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException("获取异常枚举中 message 属性异常", e);
        }
    }

    /**
     * 获取异常消息.
     *
     * @param errorMsg  errorMsg
     * @param params    params
     * @return String
     */
    public static String getExceptionMessage(String errorMsg, Object... params) {
        return MessageFormat.format(errorMsg, params);
    }

    /**
     * 获取异常描述
     */
    public static String getExceptionDescription(Enum<?> errorEnum) {
        String exceptionDescription = null;
        try {
            exceptionDescription = (String) errorEnum.getClass().getDeclaredField("description").get(errorEnum);
        } catch (IllegalAccessException | NoSuchFieldException ignored) {
        }
        return exceptionDescription;
    }

}
