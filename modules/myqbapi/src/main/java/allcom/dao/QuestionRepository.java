package allcom.dao;

import allcom.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface QuestionRepository extends CrudRepository<Question, Integer> {

}
