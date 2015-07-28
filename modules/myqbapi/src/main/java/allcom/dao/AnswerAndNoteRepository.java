package allcom.dao;

import allcom.entity.AnswerAndNote;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AnswerAndNoteRepository extends CrudRepository<AnswerAndNote,Long> {
    AnswerAndNote findByQuestionIdAndSubQuestionId(long questionId, int subQuestionId);
}
