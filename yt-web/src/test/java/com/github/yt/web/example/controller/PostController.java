package com.github.yt.web.example.controller;

import com.github.yt.mybatis.query.Page;
import com.github.yt.mybatis.query.PageUtils;
import org.springframework.web.bind.annotation.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("post")
public class PostController {

    @PostMapping("noParam")
    public void noParam() {

    }

    @PostMapping("param")
    public User param(@RequestBody User user) {
        return user;
    }

    @PostMapping("userList")
    public List<User> userList() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        return userList;
    }


    @PostMapping("userPage")
    public Page<User> userPage() {
        List<User> userList = new ArrayList<>();
        userList.add(new User());
        userList.add(new User());
        return PageUtils.createPage(10, 10, 100, userList);
    }


    public static class User {
        private String username = "张三";

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }

}
