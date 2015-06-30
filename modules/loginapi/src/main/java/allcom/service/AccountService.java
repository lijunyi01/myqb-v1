package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.*;
import allcom.entity.Account;
import allcom.entity.AccountSession;
import allcom.entity.LoginHistory;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.persistence.EntityManager;
import java.sql.Timestamp;
import java.util.List;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class AccountService {
    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Value("${session.timeout}")
    private long sessionTimeout;

    @Autowired
    private  AccountRepository accountRepository;
    @Autowired
    private AccountSessionRepository accountSessionRepository;
    @Autowired
    private LoginHistoryRepository loginHistoryRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public boolean createAccount(String phoneNumber,String password,String role,String site){
        boolean ret =false;
        String encodepassword = passwordEncoder.encode(password);
        Account account = new Account(phoneNumber,encodepassword,role,site);
        if(accountRepository.save(account)!=null){
            ret = true;
        }
        return ret;
    }

//    public boolean auth(String userName,String password){
//        boolean ret =false;
//        Account account = accountRepository.findOne(userName);
//        if(account!=null) {
//            if (passwordEncoder.matches(password, account.getPassword())) {
//                log.info(userName + " auth success!");
//                ret = true;
//            } else {
//                log.info(userName + " auth failed!");
//            }
//        }
//        return ret;
//    }

    public RetMessage auth2(String userName,String password,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<Account> accountList = accountRepository.findByUserName(userName);
        if(!accountList.isEmpty()) {
            if(accountList.size()==1) {
                Account account = accountList.get(0);
                int umid = account.getId();
                if (passwordEncoder.matches(password, account.getPassword())) {
                    log.info(userName + " auth success! umid is:"+ umid);
                    ret.setErrorCode("0");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                    //生成sessionid
                    String sessionid = getSessionId(umid);
                    retContent = umid + "<{DATA}>" + sessionid + "<{DATA}>" + account.getSite();
                    ret.setRetContent(retContent);
                } else {
                    log.info(userName + " auth failed!");
                    ret.setErrorCode("-9");
                    ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-9"));
                    ret.setRetContent(umid+ "<{DATA}>");
                }
            }else{
                log.info(userName + " auth failed!");
                ret.setErrorCode("-3");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-3"));
            }
        }else{
            log.info(userName + " auth failed,no such user!");
            ret.setErrorCode("-3");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-3"));
        }
        return ret;
    }

    private String getSessionId(int umid){
        String ret="";
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        //log.info("username:"+userName+" and timestamp now:" + currentTime);
        AccountSession accountSession = accountSessionRepository.findOne(umid);
        if(accountSession !=null){
            //log.info("username:"+userName+" and timestamp:" + accountSession.getTimestamp());
            long timediff = GlobalTools.getTimeDifference(currentTime,accountSession.getTimestamp());
            if(timediff<sessionTimeout){
                ret = accountSession.getSessionId();
            }else{
                ret = GlobalTools.getRandomString(16,false);
            }
            accountSession.setSessionId(ret);
            accountSession.setTimestamp(currentTime);
            //更新表里的sessionid和最后使用时间
            //accountSessionRepository.delete(userName);
            accountSessionRepository.save(accountSession);

        }else{
            ret = GlobalTools.getRandomString(16,false);
            accountSessionRepository.save(new AccountSession(umid,ret,currentTime));
        }


        return ret;
    }

    public void recordLogin(int umid,String userName,String ip,String errorCode,String deviceType,String deviceInfo){
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        LoginHistory loginHistory = new LoginHistory(umid,userName,currentTime);
        loginHistory.setIp(ip);
        loginHistory.setErrorCode(errorCode);
        loginHistory.setDeviceType(deviceType);
        loginHistory.setDeviceInfo(deviceInfo);
        loginHistoryRepository.save(loginHistory);
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }
}