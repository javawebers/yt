package com.github.yt.mybatis.service;

import com.github.yt.mybatis.YtMybatisDemoApplication;
import com.github.yt.mybatis.example.entity.DbEntityNotSame;
import com.github.yt.mybatis.example.entity.DbEntitySame;
import com.github.yt.mybatis.example.po.DbEntitySameTestEnumEnum;
import com.github.yt.mybatis.example.service.DataBasicService;
import com.github.yt.mybatis.example.service.DbEntityNotSameService;
import com.github.yt.mybatis.example.service.DbEntitySameService;
import com.github.yt.mybatis.query.Query;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@SpringBootTest(classes = {YtMybatisDemoApplication.class})
public class BaseServiceCountTests extends AbstractTestNGSpringContextTests {

    @Resource
    private DataBasicService dataBasicService;

    @Resource
    DbEntitySameService dbEntitySameService;
    @Resource
    DbEntityNotSameService dbEntityNotSameService;

    @AfterMethod
    public void after() {
        dbEntitySameService.delete(new DbEntitySame());
        dbEntityNotSameService.delete(new DbEntityNotSame());
    }

    @Test
    public void sameExist() {
        dataBasicService.save12Same();
        int count = dbEntitySameService.count(new DbEntitySame().setTestEnum(DbEntitySameTestEnumEnum.FEMALE));
        Assert.assertEquals(6, count);
    }

    @Test
    public void sameNotExist() {
        dataBasicService.save12Same();
        int count = dbEntitySameService.count(new DbEntitySame().setDbEntitySameId("xxx"));
        Assert.assertEquals(0, count);
    }

    @Test
    public void sameMoreCondition() {
        dataBasicService.save12Same();
        int count = dbEntitySameService.count(
                new DbEntitySame()
                        .setTestEnum(DbEntitySameTestEnumEnum.FEMALE).setTestBoolean(true));
        Assert.assertEquals(6, count);
    }

    @Test
    public void sameQueryExist() {
        dataBasicService.save12Same();
        int count = dbEntitySameService.count(new DbEntitySame(),
                new Query().addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", DbEntitySameTestEnumEnum.FEMALE));
        Assert.assertEquals(6, count);
    }

    @Test
    public void sameQueryNotExist() {
        dataBasicService.save12Same();
        int count = dbEntitySameService.count(new DbEntitySame(),
                new Query().addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", DbEntitySameTestEnumEnum.FEMALE)
                        .addWhere("testBoolean = #{testBoolean}")
                        .addParam("testBoolean", false));
        Assert.assertEquals(0, count);
    }


    @Test
    public void notSameExist() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(
                new DbEntityNotSame().setTestBoolean(true));
        Assert.assertEquals(6, count);
    }

    @Test
    public void notSameExistDefaultSettingDeleteFlag() {
        List<DbEntityNotSame>  list = dataBasicService.save12NotSame();
        dbEntityNotSameService.logicDeleteBatchByIds(list.stream().map(DbEntityNotSame::getDbEntityNotSameId).collect(Collectors.toSet()));
        int count = dbEntityNotSameService.count(
                new DbEntityNotSame().setTestBoolean(true));
        Assert.assertEquals(count, 0);
        count = dbEntityNotSameService.count(
                new DbEntityNotSame().setTestBoolean(true), false);
        Assert.assertEquals(count, 6);

    }

    @Test
    public void notSameNotExist() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(new DbEntityNotSame().setDbEntityNotSameId("xxx"));
        Assert.assertEquals(0, count);
    }

    @Test
    public void notSameMoreCondition() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(
                new DbEntityNotSame().setTestBoolean(true).setTestInt(0));
        Assert.assertEquals(2, count);
    }

    @Test
    public void notSameQueryExist() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(new DbEntityNotSame(),
                new Query().addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true));
        Assert.assertEquals(6, count);
    }

    @Test
    public void notSameQueryNotExist() {
        dataBasicService.save12NotSame();
        int count = dbEntityNotSameService.count(new DbEntityNotSame(),
                new Query().addWhere("test_int = #{testInt}")
                        .addParam("testInt", 0)
                        .addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true));
        Assert.assertEquals(2, count);
    }

}
