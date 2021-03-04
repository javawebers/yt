package com.github.yt.mybatis.test.example.service.impl;

import com.github.yt.mybatis.service.BaseService;
import com.github.yt.mybatis.test.example.dao.DbEntityNotSameMapper;
import com.github.yt.mybatis.test.example.entity.DbEntityNotSame;
import com.github.yt.mybatis.test.example.service.DbEntityNotSameService;

import javax.annotation.Resource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service("dbEntityNotSameService")
@Transactional
public class DbEntityNotSameServiceImpl extends BaseService<DbEntityNotSame> implements DbEntityNotSameService {

    @Resource
    private DbEntityNotSameMapper mapper;

}
