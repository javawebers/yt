package com.github.yt.web.controller;

import com.github.yt.web.YtWetDemoApplication;
import com.github.yt.web.example.controller.PostController;
import com.github.yt.web.unittest.ControllerTestHandler;
import com.github.yt.web.unittest.HttpRestHandler;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.springframework.test.web.servlet.ResultActions;
import org.testng.annotations.Test;

import java.util.List;

@ActiveProfiles("default")
@SpringBootTest(classes = {YtWetDemoApplication.class})
public class PostTest extends AbstractTestNGSpringContextTests {

    @Test
    public void noParam() {
        ResultActions resultActions = ControllerTestHandler.post("/post/noParam");
    }

    @Test
    public void param() {
        PostController.User user = new PostController.User();
        user.setUsername("李四");
        ResultActions resultActions = ControllerTestHandler.post("/post/param", user);
    }

    @Test
    public void param2() {
        ResultActions resultActions = ControllerTestHandler.post("/post/param", "{}");
        PostController.User user = HttpRestHandler.getResult(resultActions, PostController.User.class);
        System.out.println(user);
    }

    @Test
    public void userList() {
        ResultActions resultActions = ControllerTestHandler.post("/post/userList");
        List<PostController.User> userList = HttpRestHandler.getListResult(resultActions, PostController.User.class);
        userList.forEach(user -> System.out.println(user.getUsername()));
        System.out.println(userList);
    }

    @Test
    public void userPage() {
        ResultActions resultActions = ControllerTestHandler.post("/post/userPage");
        List<PostController.User> userList = HttpRestHandler.getPageListResult(resultActions, PostController.User.class);
        userList.forEach(user -> System.out.println(user.getUsername()));
        System.out.println(userList);
    }

}
