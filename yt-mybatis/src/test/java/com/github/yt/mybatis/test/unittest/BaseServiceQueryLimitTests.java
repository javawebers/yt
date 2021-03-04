package com.github.yt.mybatis.test.unittest;

import com.github.yt.mybatis.test.YtMybatisDemoApplication;
import com.github.yt.mybatis.test.example.entity.DbEntityNotSame;
import com.github.yt.mybatis.test.example.service.DataBasicService;
import com.github.yt.mybatis.test.example.service.DbEntityNotSameService;
import com.github.yt.mybatis.query.Query;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;

@SpringBootTest(classes = {YtMybatisDemoApplication.class})
public class BaseServiceQueryLimitTests extends AbstractTestNGSpringContextTests {

    @Resource
    private DataBasicService dataBasicService;

    @Resource
    DbEntityNotSameService dbEntityNotSameService;

    @AfterMethod
    public void after() {
        dbEntityNotSameService.delete(new DbEntityNotSame());
    }

    @Test
    public void limit1() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> list = dbEntityNotSameService.findList(new Query().excludeAllSelectColumn().addExtendSelectColumn("test_boolean").limit(0, 2));
        Assert.assertEquals(list.size(), 2);
    }

    @Test
    public void limit2_notLimit() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> list = dbEntityNotSameService.findList(new Query().excludeAllSelectColumn().addExtendSelectColumn("test_boolean"));
        Assert.assertEquals(list.size(), 12);
    }

}
