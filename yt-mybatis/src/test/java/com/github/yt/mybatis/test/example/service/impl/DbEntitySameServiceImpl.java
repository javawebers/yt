package com.github.yt.mybatis.test.example.service.impl;

import com.github.yt.mybatis.service.BaseService;
import com.github.yt.mybatis.test.example.dao.DbEntitySameMapper;
import com.github.yt.mybatis.test.example.entity.DbEntitySame;
import com.github.yt.mybatis.test.example.service.DbEntitySameService;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("dbEntitySameService")
@Transactional
public class DbEntitySameServiceImpl extends BaseService<DbEntitySame> implements DbEntitySameService {

    @Resource
    private DbEntitySameMapper mapper;

}
