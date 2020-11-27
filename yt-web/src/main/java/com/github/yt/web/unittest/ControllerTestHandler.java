package com.github.yt.web.unittest;

import com.github.yt.commons.exception.ExceptionUtils;
import com.github.yt.web.result.HttpResultHandler;
import com.github.yt.web.util.JsonUtils;
import org.hamcrest.Matchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.lang.reflect.Field;
import java.util.*;

/**
 * controller 测试处理类
 * 提供常用的 get post 方法
 *
 * @author sheng
 */
public class ControllerTestHandler {

    private static ResultActions getResultActions(String url, MultiValueMap<String, String> paramMap, Object code, boolean isPackaged) {
        if (code instanceof Enum) {
            code = HttpResultHandler.getResultConfig().convertErrorCode(ExceptionUtils.getExceptionCode((Enum<?>) code));
        }
        try {
            ResultActions resultActions;
            resultActions = InitApplication.getMockMvc().perform(MockMvcRequestBuilders.get(url)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .params(paramMap)
                    .headers(InitApplication.getHttpHeaders()))
                    .andDo(MockMvcResultHandlers.print());
            if (isPackaged) {
                resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                        "$." + HttpResultHandler.getResultConfig().getErrorCodeField(),
                        Matchers.equalTo(code)));
            }

            return resultActions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static ResultActions postResultActions(String url, Object jsonBody, MultiValueMap<String, String> paramMap, Object code) {
        if (code instanceof Enum) {
            code = HttpResultHandler.getResultConfig().convertErrorCode(ExceptionUtils.getExceptionCode((Enum<?>) code));
        }
        if (jsonBody == null) {
            jsonBody = "{}";
        }
        try {
            String jsonStr;
            if (jsonBody instanceof String) {
                jsonStr = (String) jsonBody;
            } else {
                jsonStr = JsonUtils.toJsonString(jsonBody);
            }
            ResultActions resultActions;
            resultActions = InitApplication.getMockMvc().perform(MockMvcRequestBuilders.post(url)
                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .contentType(MediaType.APPLICATION_JSON_UTF8)
                    .content(jsonStr)
                    .content(jsonStr.getBytes())
                    .params(paramMap)
                    .headers(InitApplication.getHttpHeaders()));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getErrorCodeField(),
                    Matchers.equalTo(code)));
            return resultActions;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static ResultActions get(String url, LinkedMultiValueMap<String, String> param, Object code) {
        return getResultActions(url, param, code, true);
    }

    public static ResultActions get(String url, LinkedMultiValueMap<String, String> param) {
        return getResultActions(url, param, HttpResultHandler.getResultConfig().getDefaultSuccessCode(), true);
    }

    public static ResultActions get(String url, Object code) {
        return getResultActions(url, new LinkedMultiValueMap<>(), code, true);
    }

    public static ResultActions get(String url, boolean isPackaged) {
        return getResultActions(url, new LinkedMultiValueMap<>(), HttpResultHandler.getResultConfig().getDefaultSuccessCode(), isPackaged);
    }

    public static ResultActions get(String url) {
        return get(url, new LinkedMultiValueMap<>(), HttpResultHandler.getResultConfig().getDefaultSuccessCode());
    }

    public static ResultActions getWithObjectParam(String url, Object paramObject) {
        return getWithObjectParam(url, paramObject, HttpResultHandler.getResultConfig().getDefaultSuccessCode());
    }

    public static ResultActions getWithObjectParam(String url, Object paramObject, Object code) {
        return get(parseToUrlPair(url, paramObject), new LinkedMultiValueMap<>(), code);
    }

    public static ResultActions post(String url, Object jsonBody, LinkedMultiValueMap<String, String> param, Object code) {
        return postResultActions(url, jsonBody, param, code);
    }

    public static ResultActions post(String url, Object jsonBody, LinkedMultiValueMap<String, String> param) {
        return postResultActions(url, jsonBody, param, HttpResultHandler.getResultConfig().getDefaultSuccessCode());
    }

    public static ResultActions post(String url, Object jsonBody, Object code) {
        return postResultActions(url, jsonBody, new LinkedMultiValueMap<>(), code);
    }

    public static ResultActions post(String url, Object jsonBody) {
        return postResultActions(url, jsonBody, new LinkedMultiValueMap<>(), HttpResultHandler.getResultConfig().getDefaultSuccessCode());
    }

    public static ResultActions post(String url) {
        return postResultActions(url, "{}", new LinkedMultiValueMap<>(), HttpResultHandler.getResultConfig().getDefaultSuccessCode());
    }

    public static String parseToUrlPair(String url, Object o) {
        String params = parseToUrlPair(o);
        if (!url.contains("?")) {
            url += "?";
        }

        if (!url.endsWith("&")) {
            url += "&";
        }
        return url + params;
    }

    public static String parseToUrlPair(Object o) {
        Class<?> c = o.getClass();
        List<Field> fields = getFieldList(c);
        Map<String, Object> map = new TreeMap<>();
        for (Field field : fields) {
            field.setAccessible(true);
            String name = field.getName();
            Object value;
            try {
                value = field.get(o);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value != null) {
                map.put(name, value);
            }
        }
        Set<Map.Entry<String, Object>> set = map.entrySet();
        Iterator<Map.Entry<String, Object>> it = set.iterator();
        StringBuilder sb = new StringBuilder();
        while (it.hasNext()) {
            Map.Entry<String, Object> e = it.next();
            sb.append(e.getKey()).append("=").append(e.getValue()).append("&");
        }
        return sb.toString();
    }

    private static List<Field> getFieldList(Class<?> entityClass) {
        List<Field> fieldList = new ArrayList<>();
        for (Class<?> c = entityClass; c != Object.class; c = c.getSuperclass()) {
            fieldList.addAll(Arrays.asList(c.getDeclaredFields()));
        }
        return fieldList;
    }

}
