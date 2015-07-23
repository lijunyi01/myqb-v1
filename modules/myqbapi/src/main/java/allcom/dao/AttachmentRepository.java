package allcom.dao;

import allcom.entity.Attachment;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AttachmentRepository extends CrudRepository<Attachment,Long> {
    List<Attachment> findByUmid(int umid);
}
