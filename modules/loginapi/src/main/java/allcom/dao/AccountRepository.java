package allcom.dao;

import allcom.entity.Account;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Transactional(readOnly = true)
public interface AccountRepository extends CrudRepository<Account, Integer> {
    //JPA的查询语句：JPQL
    @Query("from Account a where a.email = :userName or a.phoneNumber = :userName")
    List<Account> findByUserName(@Param("userName")String userName);

    Account findByPhoneNumber(String phoneNumber);

    Account findByEmail(String email);

    //Account findByNickName(String nickName);
}
