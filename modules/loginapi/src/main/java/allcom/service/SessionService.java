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

    @Value("${sessionid.timeout}")
    private long sessionIdTimeout;

    @Value("${systemparam.debug}")
    private int systemdebugflag;

    @Autowired
    private AccountSessionRepository accountSessionRepository;

    public boolean verifySessionId(int umid,String sessionId){
        boolean ret =false;
        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
        AccountSession accountSession = accountSessionRepository.findOne(umid);
        if(accountSession !=null){
            long timediff = GlobalTools.getTimeDifference(currentTime,accountSession.getTimestamp());

            if(timediff<sessionIdTimeout){
                if(sessionId.equals(accountSession.getSessionId())){
                    ret = true;
//                    更新sessionid最近使用时间
                    accountSession.setTimestamp(currentTime);
                    accountSessionRepository.save(accountSession);
                }
            }
        }

        if(systemdebugflag ==1){
            ret = true;
        }
        return ret;
    }

    public boolean verifyIp(int functionId,String clientIp){
        boolean ret = true;
        if(functionId == 1 || functionId == 6){
            //TODO：校验ip是否来自于设定的业务平台，实际要做得更灵活，而不是写死
            if(!true){
                ret = false;
            }
        }
        if(systemdebugflag ==1){
            ret = true;
        }
        return ret;
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorMessage(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }


}