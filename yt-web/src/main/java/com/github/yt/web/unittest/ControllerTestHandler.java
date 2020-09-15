package com.github.yt.web.unittest;

import com.github.yt.commons.exception.ExceptionUtils;
import com.github.yt.web.result.HttpResultHandler;
import com.github.yt.web.util.JsonUtils;
import org.hamcrest.Matchers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.HashMap;

public class ControllerTestHandler {
    private static Logger logger = LoggerFactory.getLogger(ControllerTestHandler.class);

    private static ResultActions getResultActions(String url, MultiValueMap<String, String> paramMap, Object code, boolean isPackaged) {
        if (code instanceof Enum) {
            code = HttpResultHandler.getResultConfig().convertErrorCode(ExceptionUtils.getExceptionCode((Enum<?>) code));
        }
        try {
            ResultActions resultActions;
            resultActions = InitApplication.mockMvc.perform(MockMvcRequestBuilders.get(url)
//                    .accept(MediaType.APPLICATION_JSON_UTF8)
                    .params(paramMap)
                    .headers(InitApplication.getHttpHeaders()))
//                    .andExpect(MockMvcResultMatchers.status().isOk())
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

    private static ResultActions postResultActions(String url, Object jsonBody, MultiValueMap<String, String> paramMap, Object code){
        if (code instanceof Enum) {
            code = HttpResultHandler.getResultConfig().convertErrorCode(ExceptionUtils.getExceptionCode((Enum<?>) code));
        }
        if (jsonBody == null) {
            jsonBody = "{}";
        }
        try {
            String jsonStr;
            if (jsonBody instanceof String) {
                jsonStr = (String)jsonBody;
            } else {
                jsonStr = JsonUtils.toJsonString(jsonBody);
            }
            ResultActions resultActions;
            resultActions = InitApplication.mockMvc.perform(MockMvcRequestBuilders.post(url)
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

}
