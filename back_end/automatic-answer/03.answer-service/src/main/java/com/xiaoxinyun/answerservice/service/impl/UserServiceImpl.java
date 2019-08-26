package com.xiaoxinyun.answerservice.service.impl;

import com.xiaoxinyun.answerservice.repository.mapper.UserMapper;
import com.xiaoxinyun.answerserviceinterface.entity.User;
import com.xiaoxinyun.answerserviceinterface.service.UserService;
import org.apache.dubbo.config.annotation.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-26 9:38:43
 */

@Service(filter = "VerifyFilter")
public class UserServiceImpl implements UserService {

    @Autowired
    UserMapper userMapper;


    @Override
    public User insert(User user) {
        return null;
    }

    @Override
    public User update(User user) {
        return null;
    }

    @Override
    public List<User> getList(String keyWord, Integer pageNum, Integer pageSize) {
        return userMapper.selectAll();
    }
}
