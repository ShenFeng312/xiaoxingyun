package com.xiaoxinyun.answerserviceinterface.service;

import com.xiaoxinyun.answerserviceinterface.entity.Answer;

import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 13:32:39
 */
public interface AnswerService {

    Answer save(Answer answer);
    void delete(Answer answer);


    List<Answer> getListByTitle(String title);

    List<Answer> getAll();

    List<Answer> getByKeyWord(String keyWord);

}
