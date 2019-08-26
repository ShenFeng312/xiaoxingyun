package com.xiaoxinyun.answerservice.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.xiaoxinyun.answerserviceinterface.entity.Answer;
import com.xiaoxinyun.answerserviceinterface.service.AnswerService;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchTemplate;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.util.List;

import static org.junit.Assert.*;

/**
 * @Description
 * @auther machunsen
 * @create 2019-08-24 13:51:53
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class AnswerServiceImplTest {
    @Autowired
    private AnswerService answerService;

    @Autowired
    private ElasticsearchTemplate elasticsearchTemplate;



    @Test
    public void test1() {
        Answer answer = new Answer();
        answer.setQuestionTitle("喉咙不舒服，异物感。伴有红肿，鼻子也不舒服。");
        answer.setQuestionDesc("我这个喉咙不舒服已经有三年左右了，去医院医生说是咽炎。喉咙一直是红红的，好像还有点肿。两侧的扁桃体好像也有点红肿，感觉吃什么药都没效果。鼻子有时候痒，右侧鼻孔时常堵，出气量很小，偶尔左侧也会堵，但右侧鼻塞情况多点，说他堵巴又不完全是，我特意把左侧堵上用力用右侧呼吸也会吸入少量气体。也不知道怎么回事。我现在都怀疑是不是什么其它的疾病，心里压力好大。");
        answer.setAnswer("需要去看医生");
        answer.setAnswer1(" 病情分析：\n" +
                "      这位患者你好，根据你的病情叙述，和你上传的口腔照片来看看，现在可以明显看出你口腔小舌头和咽喉壁附近有明显的红肿。\n" +
                "      指导意见：\n" +
                "      根据你的病情叙述，这个可以诊断为慢性咽炎，上呼吸道感染，慢性咽炎，上呼吸道感染，都可以引起，鼻粘膜充血，这样的话，你就会出现鼻塞的症状，你自己压力不用太大，就是一个慢性咽炎，治疗办法的话，首先要保持你室内空气清洁，然后你一定要戒烟，有时间的话，你可以同时做一个头部CT检查，同时，抽血化验一个血常规检查做头部CT检查主要是看一下你有没有鼻窦炎和筛窦炎？血常规化验检查，看一下你现在炎症感染到底有多重？然后根据检查结果对症治疗，如果有炎症，病毒感染的话，可以口服抗生素和抗病毒的药物，比如说头孢克肟颗粒，奥司他韦颗粒，有鼻塞症状的话，你可以购买麻黄碱溶液滴鼻，可以缓解鼻粘膜水肿，有咽痛症状的话，你可以含服草珊瑚含片或者是口服金嗓子喉宝，然后注意清淡饮食，多喝凉开水就可以，要保证每天晚上的睡眠，这样的话，你身体免疫力才能提高。\n" +
                "\n" +
                "2019-08-09 15:43ask73416584iw 追问\n" +
                "那咽喉部位需要做检查吗？因为时间很长了，感觉就没好过一样。经常上网查有的说咽喉癌，也挺担心的，都不知道怎么办了，现在发现自己有疑病情况了。\n" +
                "\n" +
                "2019-08-09 15:45医生回答：\n" +
                "现在打不到咽喉癌的程度，你要是实在不放心，可以抽时间去你们当地大医院的口腔科做一个喉镜检查看一下，但是我感觉没必要，就是一个慢性咽炎，但是你始终没有接触过系统的治疗，我上班已经跟你说了，你可以先抽时间去做一个血常规和头部CT检查，首先看一下你现在炎症感染是细菌感染还是病毒感染？然后头部CT检查主要是看一下你除了慢性咽炎以外，有没有鼻窦炎和筛窦炎？因为这两种疾病也会引起你出现鼻塞的症状，还会引起你出现头痛，这个需要明确诊断之后同时治疗。\n" +
                "\n" +
                "2019-08-09 15:50ask73416584iw 追问\n" +
                "谢谢医生，做这个喉镜和头部CT一般费用多少阿\n" +
                "\n" +
                "2019-08-09 16:03医生回答：\n" +
                "好的好的，不用客气，头部CT检查一般在170元到250元左右，喉镜检查这个你就得具体去耳鼻喉科打听一下了，检查费用应该也不会超过300元。");
        answer.setAnswer2("   您好，青年男性，咽喉部不适3年左右。咽部异物感，伴有红肿，鼻塞，鼻痒，看图片咽喉部红肿，淋巴滤泡增生，考虑咽炎，鼻炎，不除外过敏引起的，建议正规医院耳鼻喉科就诊，注意休息，避免进食辛辣刺激性食物及冷食，忌烟酒。");
        answer.setAnswer3("  你好，根据你提供的情况，目前这种症状建议你多喝水，多吃蔬菜水果，别吃辛辣刺激性的食物，并且可以化验个血常规，看是病毒感染还是细菌感染，然后合理的使用药物治疗，这种情况有慢性咽炎病有点上呼吸道感染症状，化验一下，用药效果会更好，可以配合雾化吸入见效快，祝你健康快乐");
        answer.setAnswer4(" 病情分析：\n" +
                "      这种情况考虑还是由于扁桃体炎引起，到必要时，我建议你通过手术的方法处理掉。\n" +
                "      指导意见：\n" +
                "      平时要养成良好的生活习惯和饮食习惯，不要吃辛辣刺激性食物油腻的食物。清淡规律饮食，多喝水保持口腔的卫生和健康。");
        answer.setAnswer5("   根据您的描述分析，您的这个情况可能是慢性咽炎，建议您到当地医院耳鼻喉科就诊，行血常规检查，喉镜检查，根据检查结果确定病因，确定治疗方案，多喝水，可以口服蒲地蓝消炎口服药，注意休息，不要喝酒，不要吃辣。");


        answer = answerService.save(answer);
        System.out.println(JSONObject.toJSONString(answer));
    }


    @Test
    public void Test1()
    {
        String content = "迹远只香留";
        Integer userId = null;
        Pageable pageable = PageRequest.of(0, 10);

        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if (!StringUtils.isEmpty(content)) {
            //queryBuilder.must(QueryBuilders.matchQuery("content", content));

            //{"from":0,"size":10,"query":{"bool":{"must":[{"match_phrase":{"content":{"query":"迹远只香留","slop":0,"zero_terms_query":"NONE","boost":1.0}}}],"adjust_pure_negative":true,"boost":1.0}},"version":true,"highlight":{"fields":{"content":{},"title":{}}}}
            queryBuilder.must(QueryBuilders.matchPhraseQuery("content", content));
        }

        SearchQuery searchQuery = new NativeSearchQueryBuilder()
//                .withQuery(multiMatchQuery(content, "content", "title").operator(Operator.OR))
                .withQuery(queryBuilder)
                .withHighlightFields(new HighlightBuilder.Field("content"), new HighlightBuilder.Field("title"))
                .withPageable(pageable)
                .build();
        List bookinfo = elasticsearchTemplate.queryForList(searchQuery, Answer.class);
        System.out.println(JSONObject.toJSONString(bookinfo));
    }




}