package allcom.dao;

import allcom.entity.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface QuestionRepository extends PagingAndSortingRepository<Question,Long> {
    List<Question> findByUmidAndStatus(int umid,int status);
    Page<Question> findByUmidAndStatus(int umid,int status,Pageable pageRequest);

    Question findByUmidAndStatusAndQuestionContentId(int umid,int status,long questionContentId);
    List<Question> findByUmidAndNotebookId(int umid,long notebookId);
    List<Question> findByUmidAndStatusAndNotebookId(int umid,int status,long notebookId);

//    @Query("from Question a, b where b.umid=?1 and b.content like %?2 and a.questContentId = b.id")
//    List<Question> findByUmidAndContent(int umid,String content);
}
