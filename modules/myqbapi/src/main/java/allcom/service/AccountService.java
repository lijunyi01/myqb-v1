package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountRepository;
import allcom.dao.AccountSessionRepository;

import allcom.entity.Account;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class AccountService {
    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Value("${sessionid.timeout}")
    private long sessionIdTimeout;

    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private AccountSessionRepository accountSessionRepository;


    public boolean createAccount(int umid){
        boolean ret =false;
        Account account = new Account(umid);
        if(accountRepository.save(account)!=null){
            ret = true;
        }
        return ret;
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }


}