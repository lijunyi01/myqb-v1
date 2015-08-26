package allcom.dao;

import allcom.entity.QuestionTag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface QuestionTagRepository extends CrudRepository<QuestionTag,Long> {
    //List<QuestionTag> findByUmid(int umid);
    List<QuestionTag> findByQuestionId(long questionId);
    List<QuestionTag> findByTagId(long tagId);
}
