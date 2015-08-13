package allcom.dao;

import allcom.entity.QuestionCg;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface QuestionCgRepository extends CrudRepository<QuestionCg,Long> {
    List<QuestionCg> findByUmid(int umid);
}
