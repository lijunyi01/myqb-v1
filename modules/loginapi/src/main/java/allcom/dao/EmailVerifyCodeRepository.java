package allcom.dao;

import allcom.entity.EmailVerifyCode;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.List;

//import javax.transaction.Transactional;

@Transactional(readOnly = true)
public interface EmailVerifyCodeRepository extends CrudRepository<EmailVerifyCode, Integer> {

    //List<Account> findByLastName(String lastName);

    //JPA的查询语句：JPQL
    @Query("from EmailVerifyCode a where a.email = :email and a.emailKey = :emailKey")
    List<EmailVerifyCode> findByEmailAndEmailKey(@Param("email") String email, @Param("emailKey") String emailKey);

    //delete update时一定要加@Modifying和@Transactional，否则运行期报错;以下@Transactional相当于@Transactional(readOnly=false)
    @Transactional
    @Modifying
    @Query("delete from EmailVerifyCode a where a.sendTime < :inputTime")
    void deleteOldRecord(@Param("inputTime") Timestamp inputTime);
}
