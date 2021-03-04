package com.github.yt.web.test.common;

import com.github.yt.web.result.HttpResultHandler;
import org.hamcrest.Matchers;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

public class ResultActionsUtils {

    public static void packaged(ResultActions resultActions) {
        try {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getErrorCodeField(),
                    Matchers.equalTo(HttpResultHandler.getResultConfig().getDefaultSuccessCode())));
            // uuid
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getUuidField(),
                    Matchers.notNullValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static void notPackaged(ResultActions resultActions) {
        try {
            resultActions.andExpect(MockMvcResultMatchers.content().string(""));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void defaultErrorPackaged(ResultActions resultActions) {
        try {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getErrorCodeField(),
                    Matchers.equalTo(HttpResultHandler.getResultConfig().getDefaultErrorCode())));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getMessageField(),
                    Matchers.equalTo(HttpResultHandler.getResultConfig().getDefaultErrorMessage())));

            // uuid
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getUuidField(),
                    Matchers.notNullValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public static void resultSinglePackage(ResultActions resultActions) {
        try {
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getErrorCodeField(),
                    Matchers.equalTo(HttpResultHandler.getResultConfig().getDefaultSuccessCode())));
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getResultField(),
                    Matchers.equalTo(1)));

            // uuid
            resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                    "$." + HttpResultHandler.getResultConfig().getUuidField(),
                    Matchers.notNullValue()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
