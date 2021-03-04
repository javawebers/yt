package com.github.yt.mybatis.test.example.dao;

import com.github.yt.mybatis.test.example.entity.MysqlExample;
import com.github.yt.mybatis.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * MysqlExample Mapper
 *
 */
@Mapper
@Repository
public interface MysqlExampleMapper extends BaseMapper<MysqlExample> {


}
