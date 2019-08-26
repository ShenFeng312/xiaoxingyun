package com.xiaoxinyun.answerserviceinterface.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.Serializable;

/**
 * @Description 答案表
 * @auther machunsen
 * @create 2019-08-24 13:26:33
 */
@Data
@Document(indexName = "xiaoxinyun", type = "answer", indexStoreType = "fs", shards = 5, replicas = 1, refreshInterval = "-1")
public class Answer implements Serializable {
    @Id
    private String id;

    private String deptName;
    private String deptId;
    private String diseaseName;
    private String questionTitle;
    private String questionDesc;
    private String keyWord;
    private String answer;
    private String answer1;
    private String answer2;
    private String answer3;
    private String answer4;
    private String answer5;
}
