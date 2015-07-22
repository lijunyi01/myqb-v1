package allcom.dao;

import allcom.entity.Question;
import allcom.entity.QuestionContent;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface QuestionContentRepository extends CrudRepository<QuestionContent, Long> {
    @Query("from QuestionContent a where a.umid = ?1 and a.content like %?2%")
    List<QuestionContent> findByUmidAndContent(int umid,String content);
}
