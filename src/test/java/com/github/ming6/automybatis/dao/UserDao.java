package com.github.ming6.automybatis.dao;

import org.springframework.stereotype.Repository;

import com.github.ming6.automybatis.MybatisDao;
import com.github.ming6.automybatis.model.User;

@Repository
public interface UserDao extends MybatisDao<User, Long> {

}