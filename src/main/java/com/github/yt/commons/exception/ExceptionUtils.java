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
    public static Object getExceptionCode(Enum errorEnum) {
        Object errorCode = null;
        try {
            errorCode = errorEnum.getClass().getDeclaredField("code").get(errorEnum);
            if (errorCode == null) {
                errorCode = errorEnum.name();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
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
    public static String getExceptionMessage(Enum errorEnum, Object... params) {
        String errorMessage = null;
        try {
            errorMessage = (String) errorEnum.getClass().getDeclaredField("message").get(errorEnum);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return MessageFormat.format(errorMessage, params);
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
     *
     * @param errorEnum
     * @return
     */
    public static String getExceptionDescription(Enum errorEnum) {
        String exceptionDescription = null;
        try {
            exceptionDescription = (String) errorEnum.getClass().getDeclaredField("description").get(errorEnum);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {

        }
        return exceptionDescription;
    }

}
