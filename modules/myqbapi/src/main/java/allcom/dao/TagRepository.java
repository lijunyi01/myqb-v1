package allcom.dao;

import allcom.entity.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface TagRepository extends CrudRepository<Tag,Long> {
    List<Tag> findByUmid(int umid);
    Tag findByUmidAndName(int umid, String name);
}
