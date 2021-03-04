package com.github.yt.mybatis.test.example.dao;

import com.github.yt.mybatis.test.example.entity.DbEntityNotSame;
import com.github.yt.mybatis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * DbEntityNotSame Mapper
 *
 */
@Mapper
@Repository
public interface DbEntityNotSameMapper extends BaseMapper<DbEntityNotSame> {


}
