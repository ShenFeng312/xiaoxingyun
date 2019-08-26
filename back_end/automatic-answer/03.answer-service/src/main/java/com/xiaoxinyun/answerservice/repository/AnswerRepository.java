package com.xiaoxinyun.answerservice.repository;

import com.xiaoxinyun.answerserviceinterface.entity.Answer;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 13:40:45
 */
@Repository
public interface AnswerRepository extends ElasticsearchRepository<Answer, String> {

    List<Answer> getByQuestionTitle(String title);


}
