package allcom.dao;

import allcom.entity.LoginHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface LoginHistoryRepository extends CrudRepository<LoginHistory, Integer> {

    //List<Account> findByLastName(String lastName);
}
