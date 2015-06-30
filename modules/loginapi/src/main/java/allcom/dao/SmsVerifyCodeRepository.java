package allcom.dao;

import allcom.entity.SmsVerifyCode;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SmsVerifyCodeRepository extends CrudRepository<SmsVerifyCode, Integer> {

    //List<Account> findByLastName(String lastName);

    //JPA的查询语句：JPQL
    @Query("from SmsVerifyCode a where a.phoneNumber = :phoneNumber and a.smsContent = :smsContent")
    List<SmsVerifyCode> findByPhoneNumberAndSmsContent(@Param("phoneNumber")String phoneNumber,@Param("smsContent")String smsContent);
}
