package com.xiaoxinyun.answerservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaoxinyun.answerservice.repository.elastic.AnswerRepository;
import com.xiaoxinyun.answerserviceinterface.entity.Answer;
import com.xiaoxinyun.answerserviceinterface.service.AnswerService;
import com.xiaoxinyun.commonutil.utils.CommUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.dubbo.config.annotation.Service;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 13:37:04
 */
@Service(version = "${dubbo.version}",group = "answer",filter = "VerifyFilter")
@Slf4j
public class AnswerServiceImpl implements AnswerService {

    @Autowired
    @Qualifier("answerRepository")
    AnswerRepository answerRepository;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;

    @Override
    public Answer save(Answer answer) {
        return answerRepository.save(answer);
    }

    @Override
    public void delete(Answer answer) {
        answerRepository.delete(answer);
    }

    @Override
    public List<Answer> getListByTitle(String title) {
        return answerRepository.getByQuestionTitle(title);
    }

    @Override
    public List<Answer> getAll() {
        Iterable<Answer> iterable = answerRepository.findAll();
        List<Answer> answers = new ArrayList<>();
        iterable.forEach(item -> {
            answers.add(item);
        });
        return answers;
    }

    @Override
    public List<Answer> getByKeyWord(String keyWord) {

        Pageable pageable = PageRequest.of(0, 5);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(keyWord)) {
            //queryBuilder.must(QueryBuilders.matchQuery("content", content));

            //{"from":0,"size":10,"query":{"bool":{"must":[{"match_phrase":{"content":{"query":"迹远只香留","slop":0,"zero_terms_query":"NONE","boost":1.0}}}],"adjust_pure_negative":true,"boost":1.0}},"version":true,"highlight":{"fields":{"content":{},"title":{}}}}
            queryBuilder.should(QueryBuilders.matchPhraseQuery("questionTitle", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("questionDesc", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("keyWord", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("answer", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("answer1", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("answer2", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("answer3", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("answer4", keyWord));
            queryBuilder.should(QueryBuilders.matchPhraseQuery("answer5", keyWord));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(multiMatchQuery(content, "content", "title").operator(Operator.OR))
                .withQuery(queryBuilder)
                .withHighlightFields(new HighlightBuilder.Field("questionTitle"), new HighlightBuilder.Field("questionDesc"))
                .withPageable(pageable)
                .build();
        List<Answer> answers = elasticsearchTemplate.queryForList(searchQuery, Answer.class);
        System.out.println(JSONObject.toJSONString(answers));

        return answers;
    }

    @Override
    public Integer saveList(List<Answer> list) {

        int successCount = 0;
        if (list == null) return 0;

        for (Answer answer : list) {
            try {
                answer.setId(CommUtil.getGUID());
                Answer answer1 = answerRepository.save(answer);
                successCount++;
            } catch (Exception ex) {
                log.error("导入异常", ex);
            }
        }
        return successCount;
    }
}
