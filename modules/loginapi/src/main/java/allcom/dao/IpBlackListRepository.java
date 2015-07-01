package allcom.dao;

import allcom.entity.IpBlackList;
import allcom.entity.LoginHistory;
import org.springframework.data.repository.CrudRepository;
import org.springframework.transaction.annotation.Transactional;

@Transactional(readOnly = true)
public interface IpBlackListRepository extends CrudRepository<IpBlackList, String> {

    //List<Account> findByLastName(String lastName);
}
