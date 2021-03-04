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
public class BaseServiceQueryDistinctTests extends AbstractTestNGSpringContextTests {

    @Resource
    private DataBasicService dataBasicService;

    @Resource
    DbEntityNotSameService dbEntityNotSameService;

    @AfterMethod
    public void after() {
        dbEntityNotSameService.delete(new DbEntityNotSame());
    }

    @Test
    public void distinct1() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> list = dbEntityNotSameService.findList(new Query().excludeAllSelectColumn().addExtendSelectColumn("test_boolean").distinct());
        Assert.assertEquals(list.size(), 2);
    }

    @Test
    public void distinct2_notDistinct() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> list = dbEntityNotSameService.findList(new Query().excludeAllSelectColumn().addExtendSelectColumn("test_boolean"));
        Assert.assertEquals(list.size(), 12);
    }

    @Test
    public void distinct3_countDistinct() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(new Query().excludeAllSelectColumn().addExtendSelectColumn("test_boolean").countColumn("test_boolean").distinct());
        Assert.assertEquals(count, 2);
    }

    @Test
    public void distinct4_countNotDistinct() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(new Query().excludeAllSelectColumn().addExtendSelectColumn("test_boolean"));
        Assert.assertEquals(count, 12);
    }

    @Test
    public void distinct5_countDistinctQueryIsNull() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(new DbEntityNotSame());
        Assert.assertEquals(count, 12);
    }

}
