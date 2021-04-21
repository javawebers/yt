package com.github.yt.mybatis.test.unittest;

import com.github.yt.mybatis.test.YtMybatisDemoApplication;
import com.github.yt.mybatis.test.example.entity.DbEntityNotSame;
import com.github.yt.mybatis.test.example.entity.DbEntitySame;
import com.github.yt.mybatis.test.example.po.DbEntityNotSamePO;
import com.github.yt.mybatis.test.example.po.DbEntitySameTestEnumEnum;
import com.github.yt.mybatis.test.example.service.DataBasicService;
import com.github.yt.mybatis.test.example.service.DbEntityNotSameService;
import com.github.yt.mybatis.test.example.service.DbEntitySameService;
import com.github.yt.mybatis.query.Query;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest(classes = {YtMybatisDemoApplication.class})
public class BaseServiceFindListTests extends AbstractTestNGSpringContextTests {

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
        List<DbEntitySame> dbList = dbEntitySameService.findList(new DbEntitySame().setTestEnum(DbEntitySameTestEnumEnum.FEMALE));
        Assert.assertEquals(6, dbList.size());
    }

    @Test
    public void sameNotExist() {
        dataBasicService.save12Same();
        List<DbEntitySame> dbList = dbEntitySameService.findList(new DbEntitySame().setDbEntitySameId("xxx"));
        Assert.assertEquals(0, dbList.size());
    }

    @Test
    public void sameMoreCondition() {
        dataBasicService.save12Same();
        List<DbEntitySame> dbList = dbEntitySameService.findList(
                new DbEntitySame()
                        .setTestEnum(DbEntitySameTestEnumEnum.FEMALE).setTestBoolean(true));
        Assert.assertEquals(6, dbList.size());
    }

    @Test
    public void sameQueryExist() {
        dataBasicService.save12Same();
        List<DbEntitySame> dbList = dbEntitySameService.findList(new DbEntitySame(),
                new Query().addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", DbEntitySameTestEnumEnum.FEMALE));
        Assert.assertEquals(6, dbList.size());
        dbList = dbEntitySameService.findList(new Query().addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", DbEntitySameTestEnumEnum.FEMALE));
        Assert.assertEquals(6, dbList.size());

    }

    @Test
    public void sameQueryNotExist() {
        dataBasicService.save12Same();
        List<DbEntitySame> dbList = dbEntitySameService.findList(new DbEntitySame(),
                new Query().addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", DbEntitySameTestEnumEnum.FEMALE)
                        .addWhere("testBoolean = #{testBoolean}")
                        .addParam("testBoolean", false));
        Assert.assertEquals(0, dbList.size());
        dbList = dbEntitySameService.findList(
                new Query().addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", DbEntitySameTestEnumEnum.FEMALE)
                        .addWhere("testBoolean = #{testBoolean}")
                        .addParam("testBoolean", false));
        Assert.assertEquals(0, dbList.size());
    }


    @Test
    public void notSameExist() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> dbList = dbEntityNotSameService.findList(
                new DbEntityNotSame().setTestBoolean(true));
        Assert.assertEquals(6, dbList.size());
    }

    @Test
    public void notSameNotExist() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> dbList = dbEntityNotSameService.findList(new DbEntityNotSame().setDbEntityNotSameId("xxx"));
        Assert.assertEquals(0, dbList.size());
    }

    @Test
    public void notSameMoreCondition() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> dbList = dbEntityNotSameService.findList(
                new DbEntityNotSame().setTestBoolean(true).setTestInt(0));
        Assert.assertEquals(2, dbList.size());
    }

    @Test
    public void notSameQueryExist() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> dbList = dbEntityNotSameService.findList(new DbEntityNotSame(),
                new Query().addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true));
        Assert.assertEquals(6, dbList.size());
        dbList = dbEntityNotSameService.findList(new Query().addWhere("test_boolean = #{testBoolean}")
                .addParam("testBoolean", true));
        Assert.assertEquals(6, dbList.size());
    }

    @Test
    public void notSameDefaultSettingDeleteFlag() {
        List<DbEntityNotSame> list = dataBasicService.save12NotSame();
        dbEntityNotSameService.logicDeleteBatchByIds(list.stream().map(DbEntityNotSamePO::getDbEntityNotSameId).collect(Collectors.toSet()));
        List<DbEntityNotSame> dbList = dbEntityNotSameService.findList(new DbEntityNotSame(),
                new Query().addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true));
        Assert.assertEquals(dbList.size(), 0);
        dbList = dbEntityNotSameService.findList(new DbEntityNotSame(),
                new Query().addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true), false);
        Assert.assertEquals(dbList.size(), 6);

    }

    @Test
    public void notSameQueryNotExist() {
        dataBasicService.save12NotSame();
        List<DbEntityNotSame> dbList = dbEntityNotSameService.findList(new DbEntityNotSame(),
                new Query().addWhere("test_int = #{testInt}")
                        .addParam("testInt", 0)
                        .addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true));
        Assert.assertEquals(2, dbList.size());
        dbList = dbEntityNotSameService.findList(
                new Query().addWhere("test_int = #{testInt}")
                        .addParam("testInt", 0)
                        .addWhere("test_boolean = #{testBoolean}")
                        .addParam("testBoolean", true));
        Assert.assertEquals(2, dbList.size());
    }

}