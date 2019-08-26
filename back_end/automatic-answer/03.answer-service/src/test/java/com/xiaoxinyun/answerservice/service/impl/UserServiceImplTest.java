package com.xiaoxinyun.answerservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaoxinyun.answerserviceinterface.entity.User;
import com.xiaoxinyun.answerserviceinterface.service.UserService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-26 9:40:49
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class UserServiceImplTest {

    @Autowired
    UserService  userService;

    @Test
    public void getList() {

        List<User> list = userService.getList("",1,10);

        System.out.println(JSONObject.toJSONString(list));
    }
}