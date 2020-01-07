package com.github.yt.web.common;

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

}
