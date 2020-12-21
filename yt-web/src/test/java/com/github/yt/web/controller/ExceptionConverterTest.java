package com.github.yt.web.controller;

import com.github.yt.web.YtWebExceptionEnum;
import com.github.yt.web.YtWebDemoApplication;
import com.github.yt.web.result.SimpleResultConfig;
import com.github.yt.web.unittest.ControllerTestHandler;
import org.hamcrest.Matchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.testng.annotations.Test;

/**
 * 和 ResultClassBusinessTest 互斥，不能同时执行
 */
@ActiveProfiles("default")
@SpringBootTest(classes = {YtWebDemoApplication.class})
@AutoConfigureMockMvc
public class ExceptionConverterTest extends AbstractTestNGSpringContextTests {
    private SimpleResultConfig resultConfig = new SimpleResultConfig();

    @Test
    public void unsupportedOperationException() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/exceptionConverter/unsupportedOperationExceptionNoMessage", YtWebExceptionEnum.CODE_14);
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getMessageField(),
                Matchers.equalTo("不支持的操作")));
    }



}
