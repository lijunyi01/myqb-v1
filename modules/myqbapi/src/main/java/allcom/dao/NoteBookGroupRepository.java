package allcom.dao;

import allcom.entity.NoteBookGroup;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface NoteBookGroupRepository extends CrudRepository<NoteBookGroup,Long> {
    List<NoteBookGroup> findByUmid(int umid);
    NoteBookGroup findByUmidAndName(int umid, String name);
}
