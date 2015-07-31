package allcom.dao;

import allcom.entity.ClassSubType;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface ClassSubTypeRepository extends CrudRepository<ClassSubType,Long> {
    List<ClassSubType> findByClassType(int classType);
}
