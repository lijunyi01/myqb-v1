package allcom.dao;

import allcom.entity.Question;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface QuestionRepository extends CrudRepository<Question,Long> {
    List<Question> findByUmid(int umid);

    Question findByUmidAndQuestionContentId(int umid,long questionContentId);

//    @Query("from Question a, b where b.umid=?1 and b.content like %?2 and a.questContentId = b.id")
//    List<Question> findByUmidAndContent(int umid,String content);
}
