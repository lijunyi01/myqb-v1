package allcom.dao;

import allcom.entity.SmsVerifyCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

//import javax.transaction.Transactional;
import java.sql.Timestamp;
import java.util.List;

@Transactional(readOnly = true)
public interface SmsVerifyCodeRepository extends CrudRepository<SmsVerifyCode, Integer> {

    //List<Account> findByLastName(String lastName);

    //JPA的查询语句：JPQL
    @Query("from SmsVerifyCode a where a.phoneNumber = :phoneNumber and a.smsContent = :smsContent")
    List<SmsVerifyCode> findByPhoneNumberAndSmsContent(@Param("phoneNumber")String phoneNumber,@Param("smsContent")String smsContent);

    //delete update时一定要加@Modifying和@Transactional，否则运行期报错;以下@Transactional相当于@Transactional(readOnly=false)
    @Transactional
    @Modifying
    @Query("delete from SmsVerifyCode a where a.sendTime < :inputTime")
    void deleteOldRecord(@Param("inputTime")Timestamp inputTime);
}
