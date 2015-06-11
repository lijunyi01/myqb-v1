package allcom.dao;

import allcom.entity.Account;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AccountRepository extends CrudRepository<Account, String> {

    //List<Account> findByLastName(String lastName);
}
