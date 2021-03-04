package com.github.yt.web.test.example.controller;

import com.github.yt.mybatis.query.Page;
import com.github.yt.mybatis.query.PageUtils;
import com.github.yt.web.test.example.entity.WebQueryEntity;
import com.github.yt.web.query.WebQuery;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
@RequestMapping("webPageQuery")
public class WebPageQueryController {

    @GetMapping("success")
    public WebQueryEntity success(WebQuery webQuery) {
        return new WebQueryEntity()
                .setPageNo(webQuery.takePageNo())
                .setPageSize(webQuery.takePageSize());
    }

    @GetMapping("pageResult")
    public Page<Object> pageResult() {
        return PageUtils.createPage(1, 2, 3, new ArrayList<>());
    }
}
