package allcom.dao;

import allcom.entity.AnswerAndNoteCg;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AnswerAndNoteCgRepository extends CrudRepository<AnswerAndNoteCg,Long> {
    AnswerAndNoteCg findByQuestionIdAndSubQuestionId(long questionId, int subQuestionId);
    List<AnswerAndNoteCg> findByQuestionIdAndUmid(long questionId, int umid);

    //delete update时一定要加@Modifying和@Transactional，否则运行期报错;以下@Transactional相当于@Transactional(readOnly=false)
    @Transactional
    @Modifying
    @Query("delete from AnswerAndNoteCg a where a.questionId = :questionId and a.umid = :umid")
    void deleteByQuestionIdAndUmid(@Param("questionId") long questionId, @Param("umid") int umid);
}
