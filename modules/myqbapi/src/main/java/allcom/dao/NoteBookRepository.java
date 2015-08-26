package allcom.dao;

import allcom.entity.NoteBook;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NoteBookRepository extends CrudRepository<NoteBook,Long> {
    List<NoteBook> findByUmid(int umid);
    NoteBook findByUmidAndName(int umid,String name);
    List<NoteBook> findByUmidAndGroupId(int umid,long groupId);

}
