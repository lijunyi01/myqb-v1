package allcom.dao;

import allcom.entity.Question;
import allcom.entity.QuestionContent;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface QuestionContentRepository extends CrudRepository<QuestionContent, Integer> {

}
