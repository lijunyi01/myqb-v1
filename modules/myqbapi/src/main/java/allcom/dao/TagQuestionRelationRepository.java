package allcom.dao;

import allcom.entity.TagQuestionRelation;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TagQuestionRelationRepository extends CrudRepository<TagQuestionRelation,Long> {
    List<TagQuestionRelation> findByUmid(int umid);
    List<TagQuestionRelation> findByUmidAndTagId(int umid,long tagId);
    List<TagQuestionRelation> findByUmidAndQuestionId(int umid,long questionId);
    TagQuestionRelation findByUmidAndTagIdAndQuestionId(int umid,long tagId,long questionId);
}
