package com.xiaoxinyun.answerweb.controller;

import com.xiaoxinyun.answerserviceinterface.entity.Answer;
import com.xiaoxinyun.answerserviceinterface.service.AnswerService;
import org.apache.dubbo.config.annotation.Reference;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 15:37:47
 */
@RequestMapping("/test")
@RestController
public class TestController {

    @Reference(version = "${dubbo.version}")
    AnswerService answerService;


    @GetMapping("/search")
    public List<Answer> search(@RequestParam(defaultValue = "") String keyWord) {

        keyWord = StringUtils.isEmpty(keyWord) ? "喉咙不舒服" : keyWord.trim();
        return answerService.getByKeyWord(keyWord);
    }


}
