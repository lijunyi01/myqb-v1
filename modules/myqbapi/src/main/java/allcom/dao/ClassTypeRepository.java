package allcom.dao;

import allcom.entity.ClassType;
import allcom.entity.Question;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClassTypeRepository extends CrudRepository<ClassType,Long> {

}
