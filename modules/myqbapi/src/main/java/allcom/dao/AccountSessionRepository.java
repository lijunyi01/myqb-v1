package allcom.dao;

import allcom.entity.AccountSession;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountSessionRepository extends CrudRepository<AccountSession, Integer> {

    //List<Account> findByLastName(String lastName);
}
