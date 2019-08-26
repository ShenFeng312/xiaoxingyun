package com.xiaoxinyun.answerserviceinterface.service;

import com.xiaoxinyun.answerserviceinterface.entity.User;

import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-26 9:35:58
 */
public interface UserService {


    User insert(User user);
    User update(User user);

    List<User> getList(String keyWord,Integer pageNum,Integer pageSize);

}
