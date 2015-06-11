package allcom.service;

import allcom.App;
import allcom.dao.AccountRepository;
import allcom.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Service;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class AccountService {
    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Autowired
    private  AccountRepository accountRepository;

    public boolean createAccount(String userName,String password){
        boolean ret =false;
        Account account = new Account(userName,password,"ROLE_USER");
        if(accountRepository.save(account)!=null){
            ret = true;
        }
        return ret;
    }

    public boolean auth(String userName,String password){
        boolean ret =false;
        Account account = accountRepository.findOne(userName);
        if(account!=null) {
            if (account.getPassword().equals(password)) {
                log.info(userName + " auth success!");
                ret = true;
            } else {
                log.info(userName + " auth failed!");
            }
        }
        return ret;
    }


}
