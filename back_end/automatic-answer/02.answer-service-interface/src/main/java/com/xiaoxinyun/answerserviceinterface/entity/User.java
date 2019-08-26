package com.xiaoxinyun.answerserviceinterface.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;

import java.util.Date;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-26 9:34:16
 */
@Data
public class User {
    @Id
    private Integer id;

    private String userName;
    private Integer age;
    private Date createTime;
    private Date updateTime;
}
