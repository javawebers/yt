package com.github.yt.web.unittest;

import com.github.yt.web.YtWebDemoApplication;
import com.github.yt.web.result.SimpleResultConfig;
import org.hamcrest.Matchers;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
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
@ActiveProfiles("webPageQueryDefault")
@SpringBootTest(classes = {YtWebDemoApplication.class})
@Test(priority = 1)
@AutoConfigureMockMvc
public class WebPageQueryDefaultTest extends AbstractTestNGSpringContextTests {
    private SimpleResultConfig resultConfig = new SimpleResultConfig();

    @Test
    public void test1() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/webPageQuery/success?pageNo=2&pageSize=3");
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageNo",
                Matchers.equalTo(2)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageSize",
                Matchers.equalTo(3)));
    }

    @Test
    public void test2() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/webPageQuery/success?pageSize=3");
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageNo",
                Matchers.equalTo(1)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageSize",
                Matchers.equalTo(3)));
    }

    @Test
    public void test3() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/webPageQuery/success");
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageNo",
                Matchers.equalTo(1)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageSize",
                Matchers.equalTo(10)));
    }

    @Test
    public void test4() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/webPageQuery/success?myPageNo=2&myPageSize=3");
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageNo",
                Matchers.equalTo(1)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageSize",
                Matchers.equalTo(10)));
    }

    // 返回的分页数据
    @Test
    public void test5() throws Exception {
        ResultActions resultActions = ControllerTestHandler.get("/webPageQuery/pageResult");
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageNo",
                Matchers.equalTo(1)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".pageSize",
                Matchers.equalTo(2)));
        resultActions.andExpect(MockMvcResultMatchers.jsonPath(
                "$." + resultConfig.getResultField() + ".totalCount",
                Matchers.equalTo(3)));
    }

}