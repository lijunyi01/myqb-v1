package allcom.dao;

import allcom.entity.Attachment;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AttachmentRepository extends CrudRepository<Attachment,Long> {
    List<Attachment> findByUmid(int umid);
    List<Attachment> findByUmidAndQuestionId(int umid,long questionId);

    //delete update时一定要加@Modifying和@Transactional，否则运行期报错;以下@Transactional相当于@Transactional(readOnly=false)
    @Transactional
    @Modifying
    @Query("update Attachment a set a.questionId=-1 where a.questionId = :questionId and a.umid = :umid")
    void resetQuestionIdByQuestionId(@Param("questionId") long questionId,@Param("umid") int umid);

    @Transactional
    @Modifying
    @Query("update Attachment a set a.questionId=:questionId where a.id = :id and a.umid = :umid")
    void setQuestionIdById(@Param("questionId") long questionId,@Param("umid") int umid,@Param("id") long id);
}
