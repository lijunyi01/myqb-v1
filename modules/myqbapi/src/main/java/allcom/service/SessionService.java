package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountSessionRepository;
import allcom.entity.AccountSession;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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

    @Value("${systemparam.loginurl}")
    private String loingUrl;

    @Autowired
    private AccountSessionRepository accountSessionRepository;

    public boolean verifySessionId(int umid,String sessionId,int functionId){
        boolean ret =false;
        //functionId ==1 表示是登录验证，此时必须进行远程验证
        if(functionId!=1) {
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            AccountSession accountSession = accountSessionRepository.findOne(umid);
            if (accountSession != null) {
                long timediff = GlobalTools.getTimeDifference(currentTime, accountSession.getTimestamp());

                if (timediff < sessionIdTimeout) {
                    if (sessionId.equals(accountSession.getSessionId())) {
                        ret = true;
//                    注意： 本地验证通过不更新sessionid最近使用时间，以保证过一定时间就去登录站点验证sessionid
//                    accountSession.setTimestamp(currentTime);
//                    accountSessionRepository.save(accountSession);
                    }
                }
            }
        }

        //本地校验未通过，则到登录站点进行校验
        if(ret == false){
            if(remoteVerifySessionId(umid,sessionId)){
                ret = true;
            }
        }

        return ret;
    }

//    public boolean verifyIp(int functionId,String clientIp){
//        boolean ret = true;
//        if(functionId == 1 || functionId == 6){
//            //TODO：校验ip是否来自于设定的业务平台，实际要做得更灵活，而不是写死
//            if(!true){
//                ret = false;
//            }
//        }
//        if(systemdebugflag ==1){
//            ret = true;
//        }
//        return ret;
//    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    public boolean remoteVerifySessionId(int umid,String sessionId){
        boolean ret = false;
        RestTemplate restTemplate = new RestTemplate();
        String url = loingUrl + "umid="+umid+"&sessionId="+sessionId;
        RetMessage retMessage = restTemplate.getForObject(url, RetMessage.class);
        if(retMessage.getErrorCode().equals("0")){
            ret = true;
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            AccountSession accountSession = accountSessionRepository.findOne(umid);
            if(accountSession!=null){
                accountSession.setTimestamp(currentTime);
                accountSessionRepository.save(accountSession);
            }else{
                accountSession = new AccountSession(umid,sessionId,currentTime);
                accountSessionRepository.save(accountSession);
            }
        }
        return  ret;
    }


}