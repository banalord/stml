package icu.banalord.shuatimalou.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import icu.banalord.shuatimalou.model.entity.Question;
import org.apache.ibatis.annotations.Select;

import java.util.Date;
import java.util.List;

/**
 * 题目数据库操作
 *
 */
public interface QuestionMapper extends BaseMapper<Question> {

    /**
     * 查询题目列表（包括已被删除的数据）
     */
    @Select("select * from question where updateTime >= #{minUpdateTime}")
    List<Question> listQuestionWithDelete(Date minUpdateTime);

}
