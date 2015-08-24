package allcom.dao;

import allcom.entity.Question;
import allcom.entity.QuestionCg;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Transactional(readOnly = true)
//public interface PagingAndSortingRepository<T, ID extends Serializable> extends CrudRepository<T, ID>
//PagingAndSortingRepository 本身继承了CrudRepository
public interface QuestionCgRepository extends PagingAndSortingRepository<QuestionCg,Long> {
    List<QuestionCg> findByUmid(int umid);
    Page<QuestionCg> findByUmid(int umid,Pageable pageRequest);
}
