package com.github.yt.commons.exception;

import java.util.Collection;

/**
 * 异常断言
 *
 * @author liujiasheng
 */
public class Assert {

    /**
     * 断言不为空
     *
     * @param object        object
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void notNull(Object object, Enum exceptionEnum, Object... params) {
        if (object == null) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言不为空
     * 默认为当前记录不存在
     *
     * @param object object
     */
    public static void notNull(Object object, Enum exceptionEnum) {
        if (object == null) {
            throw new BaseAccidentException(exceptionEnum);
        }
    }


    /**
     * 断言为空
     *
     * @param object        object
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void isNull(Object object, Enum exceptionEnum, Object... params) {
        if (object != null) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言字符串不Empty
     *
     * @param str           str
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void notEmpty(String str, Enum exceptionEnum, Object... params) {
        if (str == null || str.length() == 0) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言字符串Empty
     *
     * @param str           str
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void empty(String str, Enum exceptionEnum, Object... params) {
        if (!(str == null || str.length() == 0)) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言集合不Empty
     *
     * @param collection    collection
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void notEmpty(Collection collection, Enum exceptionEnum, Object... params) {
        if (collection == null || collection.isEmpty()) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言集合Empty
     *
     * @param collection    collection
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void empty(Collection collection, Enum exceptionEnum, Object... params) {
        if (!(collection == null || collection.isEmpty())) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言两个对象是否equals
     *
     * @param object1       object1
     * @param object2       object2
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void equals(Object object1, Object object2, Enum exceptionEnum, Object... params) {
        if (!object1.equals(object2)) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言两个对象是否not equals
     *
     * @param object1       object1
     * @param object2       object2
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void notEquals(Object object1, Object object2, Enum exceptionEnum, Object... params) {
        if (object1.equals(object2)) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 断言
     *
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void isTrue(boolean expression, Enum exceptionEnum, Object... params) {
        if (!expression) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 否断言
     *
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void notTrue(boolean expression, Enum exceptionEnum, Object... params) {
        if (expression) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 小于
     * less then
     * @param number1       number1
     * @param number2       number2
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void lt(Number number1, Number number2, Enum exceptionEnum, Object... params) {
        double n1 = number1.doubleValue();
        double n2 = number2.doubleValue();
        if (n1 >= n2) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }


    /**
     * 小于等于
     * less equals
     * @param number1       number1
     * @param number2       number2
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void le(Number number1, Number number2, Enum exceptionEnum, Object... params) {
        double n1 = number1.doubleValue();
        double n2 = number2.doubleValue();
        if (n1 > n2) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 大于
     * great then
     * @param number1       number1
     * @param number2       number2
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void gt(Number number1, Number number2, Enum exceptionEnum, Object... params) {
        double n1 = number1.doubleValue();
        double n2 = number2.doubleValue();
        if (n1 <= n2) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

    /**
     * 大于等于
     * great equals
     * @param number1       number1
     * @param number2       number2
     * @param exceptionEnum exceptionEnum
     * @param params        params
     */
    public static void ge(Number number1, Number number2, Enum exceptionEnum, Object... params) {
        double n1 = number1.doubleValue();
        double n2 = number2.doubleValue();
        if (n1 < n2) {
            throw new BaseAccidentException(exceptionEnum, params);
        }
    }

}
