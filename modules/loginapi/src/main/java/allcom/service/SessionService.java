package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountRepository;
import allcom.dao.AccountSessionRepository;
import allcom.dao.LoginHistoryRepository;
import allcom.entity.Account;
import allcom.entity.AccountSession;
import allcom.entity.LoginHistory;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class SessionService {
    private static Logger log = LoggerFactory.getLogger(SessionService.class);

    @Value("${session.timeout}")
    private long sessionTimeout;

    @Autowired
    private AccountSessionRepository accountSessionRepository;

    public boolean verifySessionId(String userName,String sessionId){
        boolean ret =false;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        AccountSession accountSession = accountSessionRepository.findOne(userName);
        if(accountSession !=null){
            long timediff = GlobalTools.getTimeDifference(currentTime,accountSession.getTimestamp());

            if(timediff<sessionTimeout){
                if(sessionId.equals(accountSession.getSessionId())){
                    ret = true;
//                    更新sessionid最近使用时间
                    accountSession.setTimestamp(currentTime);
                    accountSessionRepository.save(accountSession);
                }
            }
        }
        return ret;
    }

    public RetMessage returnFail(String area){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorMessage("-4");
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"-4"));
        return retMessage;
    }

}