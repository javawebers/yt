package com.github.yt.mybatis.service;

import com.github.yt.mybatis.YtMybatisDemoApplication;
import com.github.yt.mybatis.business.entity.DbEntityNotSame;
import com.github.yt.mybatis.business.entity.DbEntitySame;
import com.github.yt.mybatis.business.po.DbEntitySameTestEnumEnum;
import com.github.yt.mybatis.business.service.DbEntityNotSameService;
import com.github.yt.mybatis.business.service.DbEntitySameService;
import com.github.yt.mybatis.query.Query;
import org.mybatis.spring.MyBatisSystemException;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;
import java.util.List;

/**
 * 查询一条
 * 查询为空
 * 多条结果
 * 多条件
 */

@SpringBootTest(classes = {YtMybatisDemoApplication.class})
public class BaseServiceFindTests extends AbstractTestNGSpringContextTests {

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
        DbEntitySame entity = dataBasicService.saveOneSame();
        DbEntitySame dbEntity = dbEntitySameService.find(
                new DbEntitySame().setDbEntitySameId(entity.getDbEntitySameId()));
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void sameNotExist() {
        DbEntitySame dbEntity = dbEntitySameService.find(
                new DbEntitySame().setDbEntitySameId("sameNotExist_xxx"));
        Assert.assertNull(dbEntity);
    }

    @Test(expectedExceptions = MyBatisSystemException.class)
    public void sameMultiResult() {
        List<DbEntitySame> list = dataBasicService.save12Same();
        dbEntitySameService.find(new DbEntitySame().setTestEnum(DbEntitySameTestEnumEnum.FEMALE));
    }

    @Test
    public void sameMoreCondition() {
        DbEntitySame entity = dataBasicService.saveOneSame();
        DbEntitySame dbEntity = dbEntitySameService.find(entity);
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void sameQueryExist() {
        DbEntitySame entity = dataBasicService.saveOneSame();
        DbEntitySame dbEntity = dbEntitySameService.find(new DbEntitySame().setTestVarchar(entity.getTestVarchar()),
                new Query().addWhere("testVarchar = #{testVarchar}")
                        .addParam("testVarchar", entity.getTestVarchar())
                        .addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", entity.getTestEnum()));
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void sameQueryNotExist() {
        DbEntitySame entity = dataBasicService.saveOneSame();
        DbEntitySame dbEntity = dbEntitySameService.find(new DbEntitySame().setTestVarchar(entity.getTestVarchar()),
                new Query().addWhere("testVarchar = #{testVarchar}")
                        .addParam("testVarchar", entity.getTestVarchar() + "_sss")
                        .addWhere("testEnum = #{testEnum}")
                        .addParam("testEnum", entity.getTestEnum()));
        Assert.assertNull(dbEntity);
    }


    @Test
    public void notSameExist() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        DbEntityNotSame dbEntity = dbEntityNotSameService.find(
                new DbEntityNotSame().setDbEntityNotSameId(entity.getDbEntityNotSameId()));
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void notSameNotExist() {
        DbEntityNotSame dbEntity = dbEntityNotSameService.find(
                new DbEntityNotSame().setDbEntityNotSameId("notSameNotExist_xxx"));
        Assert.assertNull(dbEntity);
    }

    @Test(expectedExceptions = MyBatisSystemException.class)
    public void notSameMultiResult() {
        List<DbEntityNotSame> list = dataBasicService.save12NotSame();
        dbEntityNotSameService.find(new DbEntityNotSame().setTestBoolean(true));
    }

    @Test
    public void notSameMoreCondition() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        DbEntityNotSame dbEntity = dbEntityNotSameService.find(entity);
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void notSameQueryExist() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        DbEntityNotSame dbEntity = dbEntityNotSameService.find(new DbEntityNotSame().setTestVarchar(entity.getTestVarchar()),
                new Query().addWhere("test_varchar = #{testVarchar}")
                        .addParam("testVarchar", entity.getTestVarchar()));
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void notSameQueryNotExist() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        DbEntityNotSame dbEntity = dbEntityNotSameService.find(new DbEntityNotSame().setTestVarchar(entity.getTestVarchar()),
                new Query().addWhere("test_varchar = #{testVarchar}")
                        .addParam("testVarchar", entity.getTestVarchar() + "_sss"));
        Assert.assertNull(dbEntity);
    }

}
