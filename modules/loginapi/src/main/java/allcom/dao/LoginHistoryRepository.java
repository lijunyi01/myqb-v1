package allcom.dao;

import allcom.entity.LoginHistory;
import org.springframework.data.repository.CrudRepository;

public interface LoginHistoryRepository extends CrudRepository<LoginHistory, Integer> {

    //List<Account> findByLastName(String lastName);
}
