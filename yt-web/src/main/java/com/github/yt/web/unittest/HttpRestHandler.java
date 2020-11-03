package com.github.yt.web.unittest;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.yt.web.YtWebConfig;
import com.github.yt.web.result.BaseResultConfig;
import com.github.yt.web.result.HttpResultHandler;
import com.github.yt.web.util.JsonUtils;
import com.github.yt.web.util.SpringContextUtils;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * http接口请求返回实体处理类
 *
 * @author liujiasheng
 */
public class HttpRestHandler {


    /**
     * 获取接口返回结果
     *
     */
    public static <T> T getResult(ResultActions resultActions, Class<T> resultType) {
        return getResult(responseToString(resultActions), resultType);
    }

    /**
     * 获取接口返回结果
     *
     */
    @SuppressWarnings("unchecked")
    public static <T> T getResult(String json, Class<T> resultType) {
        try {
            Object result = getResult(json);
            if (result instanceof Map) {
                ObjectMapper objectMapper = new ObjectMapper();
                return objectMapper.readValue(JsonUtils.toJsonString(result), resultType);
            }
            return (T) result;
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 获取接口返回结果（列表）
     *
     */
    public static <T> List<T> getListResult(ResultActions resultActions, Class<T> resultType) {
        String jsonResult = responseToString(resultActions);
        Object result = getResult(jsonResult);
        return getListResult(result, resultType);
    }

    /**
     * 获取接口返回结果（分页）
     *
     */
    public static <T> List<T> getPageListResult(ResultActions resultActions, Class<T> resultType) {
        String jsonResult = responseToString(resultActions);
        Object result = getResult(jsonResult);
        if (result instanceof Map) {
            YtWebConfig ytWebConfig = SpringContextUtils.getBean(YtWebConfig.class);
            Object listData = ((Map<?, ?>) result).get(ytWebConfig.getPage().getPageDataName());
            return getListResult(listData, resultType);
        }
        throw new RuntimeException("结果集不是集合类型");
    }

    private static <T> List<T> getListResult(Object listData, Class<T> resultType){
        ObjectMapper objectMapper = new ObjectMapper();
        if (listData instanceof List) {
            List<T> resultList = new ArrayList<>();
            ((List<?>) listData).forEach(resultEntity -> {
                try {
                    T t = objectMapper.readValue(JsonUtils.toJsonString(resultEntity), resultType);
                    resultList.add(t);
                } catch (JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            });
            return resultList;
        }
        throw new RuntimeException("结果集不是集合类型");
    }


    private static Object getValue(Object source, String fieldName) {
        try {
            Field field = getField(source.getClass(), fieldName);
            field.setAccessible(true);
            return field.get(source);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private static Field getField(Class<?> clazz, String fieldName) {
        for (Field field : clazz.getDeclaredFields()) {
            if (field.getName().equals(fieldName)) {
                return field;
            }
        }
        if (clazz.getSuperclass() == null || clazz.getSuperclass().equals(Object.class)) {
            throw new RuntimeException("属性不存在");
        } else {
            return getField(clazz.getSuperclass(), fieldName);
        }
    }

    private static String responseToString(ResultActions resultActions) {
        MvcResult mvcResult = resultActions.andReturn();
        MockHttpServletResponse mockHttpServletResponse = mvcResult.getResponse();
        try {
            PrintWriter printWriter = mockHttpServletResponse.getWriter();
            OutputStreamWriter outputStreamWriter = ((OutputStreamWriter) getValue(printWriter, "out"));
            return getValue(outputStreamWriter, "lock").toString();
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    private static Object getResult(String json) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            HashMap jsonObject = objectMapper.readValue(json, HashMap.class);
            BaseResultConfig baseResultConfig = HttpResultHandler.getResultConfig();
            return jsonObject.get(baseResultConfig.getResultField());
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
