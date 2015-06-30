package allcom.dao;

import allcom.entity.AccountSession;
import org.springframework.data.repository.CrudRepository;

public interface AccountSessionRepository extends CrudRepository<AccountSession, Integer> {

    //List<Account> findByLastName(String lastName);
}
