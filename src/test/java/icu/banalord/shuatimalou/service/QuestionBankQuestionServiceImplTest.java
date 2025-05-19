package icu.banalord.shuatimalou.service;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import icu.banalord.shuatimalou.model.entity.Question;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@SpringBootTest
class QuestionBankQuestionServiceImplTest {
    @Resource
    private QuestionService questionService;
    @Resource
    private QuestionBankService questionBankService;

    @Test
    void mpapitest(){
        ArrayList<Long> questionIdList = new ArrayList<>(Arrays.asList(99L,9L,10L,11L));
        // 最终目的是为了得到id列表
        // select id from Question where id in (99,9,10,11);
        LambdaQueryWrapper<Question> lqw = Wrappers.lambdaQuery(Question.class)
                .select(Question::getId)
                .in(Question::getId, questionIdList);

        // 只有id的Question
        List<Question> list1 = questionService.list(lqw);
        System.out.println("list1 = " + list1);
        // list1 = [Question(id=9, title=null, content=null, tags=null, answer=null, userId=null, editTime=null, createTime=null, updateTime=null, isDelete=null), Question(id=10, title=null, content=null, tags=null, answer=null, userId=null, editTime=null, createTime=null, updateTime=null, isDelete=null), Question(id=11, title=null, content=null, tags=null, answer=null, userId=null, editTime=null, createTime=null, updateTime=null, isDelete=null)]
        List<Long> list2 = list1.stream().map(Question::getId).toList();
        // list2 = [9, 10, 11]
        System.out.println("list2 = " + list2);
        List<Object> obj = questionService.listObjs(lqw);
        System.out.println("obj = " + obj);
        // obj = [9, 10, 11]
        List<Long> list3 = questionService.listObjs(lqw, o -> (Long) o);
        System.out.println("list3 = " + list3);
        // list3 = [9, 10, 11]

    }

    @Test
    void testremoveall(){
        List<Long> list1 = Arrays.asList(1L,2L,3L,4L,5L,6L);
        List<Long> list2 = Collections.emptyList();

        list1.removeAll(list2);
        System.out.println("list1 = " + list1);
    }

}