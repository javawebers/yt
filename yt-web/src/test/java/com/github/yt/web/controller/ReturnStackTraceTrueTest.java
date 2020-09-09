package com.github.yt.web.controller;

import com.github.yt.web.YtWetDemoApplication;
import com.github.yt.web.exception.MyBusinessExceptionEnum;
import com.github.yt.web.result.HttpResultHandler;
import org.hamcrest.Matchers;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testng.annotations.Test;
import com.github.yt.web.unittest.ControllerTestHandler;

/**
 * 和 ResultClassBusinessTest 互斥，不能同时执行
 */
@ActiveProfiles("returnStackTrace")
@SpringBootTest(classes = {YtWetDemoApplication.class})
public class ReturnStackTraceTrueTest extends AbstractTestNGSpringContextTests {


    @Test
    public void success() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/returnStackTrace/success");
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$",
                Matchers.not(Matchers.hasKey(HttpResultHandler.getResultConfig().getStackTraceField()))));
    }

    @Test
    public void error() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/returnStackTrace/error", HttpResultHandler.getResultConfig().getDefaultErrorCode());
        resultActions.andExpect(MockMvcResultMatchers.jsonPath("$",
                Matchers.hasKey(HttpResultHandler.getResultConfig().getStackTraceField())));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + HttpResultHandler.getResultConfig().getStackTraceField(),
                Matchers.notNullValue()));
    }

    @Test
    public void knowException() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/returnStackTrace/knowException", MyBusinessExceptionEnum.CODE_1003);
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + HttpResultHandler.getResultConfig().getStackTraceField(),
                Matchers.notNullValue()));
    }
}
