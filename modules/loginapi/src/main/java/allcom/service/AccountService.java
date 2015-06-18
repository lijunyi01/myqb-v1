package allcom.service;

import allcom.App;
import allcom.controller.RetMessage;
import allcom.dao.AccountRepository;
import allcom.entity.Account;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean createAccount(String userName,String password,String role,String site){
        boolean ret =false;
        String encodepassword = passwordEncoder.encode(password);
        Account account = new Account(userName,encodepassword,role,site);
        if(accountRepository.save(account)!=null){
            ret = true;
        }
        return ret;
    }

    public boolean auth(String userName,String password){
        boolean ret =false;
        Account account = accountRepository.findOne(userName);
        if(account!=null) {
            if (passwordEncoder.matches(password, account.getPassword())) {
                log.info(userName + " auth success!");
                ret = true;
            } else {
                log.info(userName + " auth failed!");
            }
        }
        return ret;
    }

    public RetMessage auth2(String userName,String password){
        RetMessage ret = new RetMessage();
        String retContent="";
        Account account = accountRepository.findOne(userName);
        if(account!=null) {
            if (passwordEncoder.matches(password, account.getPassword())) {
                log.info(userName + " auth success!");
                ret.setErrorCode("0");
                ret.setErrorMessage("auth success");
                //生成sessionid
                String sessionid = "sessionid";
                retContent = sessionid + "<{DATA}>" +account.getSite();
                ret.setRetContent(retContent);
            } else {
                log.info(userName + " auth failed!");
                ret.setErrorCode("-2");
                ret.setErrorMessage("auth failed");
            }
        }
        return ret;
    }


}
