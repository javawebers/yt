package com.github.yt.mybatis.test.unittest;

import com.github.yt.mybatis.test.YtMybatisDemoApplication;
import com.github.yt.mybatis.test.example.entity.DbEntityNotSame;
import com.github.yt.mybatis.test.example.entity.DbEntitySame;
import com.github.yt.mybatis.test.example.service.DataBasicService;
import com.github.yt.mybatis.test.example.service.DbEntityNotSameService;
import com.github.yt.mybatis.test.example.service.DbEntitySameService;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.testng.AbstractTestNGSpringContextTests;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import javax.annotation.Resource;

@SpringBootTest(classes = {YtMybatisDemoApplication.class})
public class BaseServiceGetOneTests extends AbstractTestNGSpringContextTests {

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


    /**
     * getOne id 为空
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void sameNullId() {
        // null id
        dbEntitySameService.findOneById(null);
    }

    /**
     * getOne id 不为空，记录不存在
     */
    @Test(expectedExceptions = EmptyResultDataAccessException.class)
    public void sameNotExist() {
        dbEntitySameService.findOneById("xxxx_null");
    }

    /**
     * getOne id 不为空，记录存在
     */
    @Test
    public void sameExist() {
        DbEntitySame entity = dataBasicService.saveOneSame();
        DbEntitySame dbEntity = dbEntitySameService.findOneById(entity.getDbEntitySameId());
        Assert.assertNotNull(dbEntity);
    }

    @Test
    public void sameExist2() {
        DbEntitySame entity = dataBasicService.saveOneSame();
        DbEntitySame dbEntity = dbEntitySameService.findOneById(entity.getDbEntitySameId());
        Assert.assertNotNull(dbEntity);
    }

    /**
     * getOne id 为空
     */
    @Test(expectedExceptions = IllegalArgumentException.class)
    public void notSameNullId() {
        // null id
        dbEntityNotSameService.findOneById(null);
    }

    /**
     * getOne id 不为空，记录不存在
     */
    @Test(expectedExceptions = EmptyResultDataAccessException.class)
    public void notSameNotExist() {
        dbEntityNotSameService.findOneById("xxxx_null");
    }

    /**
     * getOne id 不为空，记录存在
     */
    @Test
    public void notSameExist() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        DbEntityNotSame dbEntity = dbEntityNotSameService.findOneById(entity.getDbEntityNotSameId());
        Assert.assertNotNull(dbEntity);
    }
    @Test(expectedExceptions = EmptyResultDataAccessException.class)
    public void notSameDefaultSettingDeleteFlag() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        dbEntityNotSameService.logicDelete(entity);
        dbEntityNotSameService.findOneById(entity.getDbEntityNotSameId());
    }

    @Test
    public void notSameDefaultSettingDeleteFlag2() {
        DbEntityNotSame entity = dataBasicService.saveOneNotSame();
        dbEntityNotSameService.logicDelete(entity);
        DbEntityNotSame dbEntity = dbEntityNotSameService.findOneById(entity.getDbEntityNotSameId(), false);
        Assert.assertNotNull(dbEntity);
    }
}
