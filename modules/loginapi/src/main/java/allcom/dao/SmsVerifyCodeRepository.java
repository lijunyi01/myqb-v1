package allcom.dao;

import allcom.entity.SmsVerifyCode;
import org.springframework.data.repository.CrudRepository;

public interface SmsVerifyCodeRepository extends CrudRepository<SmsVerifyCode, Integer> {

    //List<Account> findByLastName(String lastName);
}
