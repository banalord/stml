package icu.banalord.shuatimalou.esdao;

import icu.banalord.shuatimalou.model.dto.post.PostEsDTO;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class TestPostEsDao {
    @Resource
    private PostEsDao postEsDao;

    @Test
    void testAdd() {
        PostEsDTO post = new PostEsDTO();
        post.setId(222L);
        post.setTitle("《霸道富婆爱上我》");
        post.setContent("今天讲的是霸道富婆爱上我的故事...");
        post.setTags(Arrays.asList("blades", " rich", " love"));
        post.setThumbNum(260);
        post.setFavourNum(33);
        post.setUserId(5L);
        post.setCreateTime(new Date());
        post.setUpdateTime(new Date());
        post.setIsDelete(0);
        postEsDao.save(post);
        System.out.println("post.getId() = " + post.getId());
    }

    @Test
    void testDelete() {
        PostEsDTO post = new PostEsDTO();
        post.setId(222L);
        // 只能通过id删除
        postEsDao.delete(post);
    }

    @Test
    void testUpdate() {
        // 只能查出来再改，跟mp不同。
        // 或者使用 elaticsearchRestTemplate
        PostEsDTO post = postEsDao.findById(222L).orElse(null);
        if (post == null) {
            return;
        }
        post.setThumbNum(2600);
        postEsDao.save(post);
    }

    @Test
    void testFindByUserId() {
        List<PostEsDTO> postEsDTOList = postEsDao.findByUserId(5L);
        System.out.println("postEsDTOList = " + postEsDTOList);
    }

    @Test
    void testQuery() {
        //List<PostEsDTO> list = postEsDao.findByTitle("霸道爱上");可以查出
        //List<PostEsDTO> list = postEsDao.findByTitle("霸道 爱上");可以查出
        List<PostEsDTO> list = postEsDao.findByTitle("霸道，爱上");
        System.out.println(list);
    }
}
