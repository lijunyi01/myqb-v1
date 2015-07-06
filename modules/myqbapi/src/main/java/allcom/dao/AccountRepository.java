package allcom.dao;

import allcom.entity.Account;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface AccountRepository extends CrudRepository<Account, Integer> {

}
