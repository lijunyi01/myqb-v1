package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountRepository;

import allcom.entity.Account;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class AccountService {
    private static Logger log = LoggerFactory.getLogger(AccountService.class);

    @Value("${sessionid.timeout}")
    private long sessionIdTimeout;

    @Value("${systemparam.loginurl}")
    private String loingUrl;

    @Autowired
    private AccountRepository accountRepository;

    //账号已存在或者新建账号成功都返回Account
    public Account createAccountIfNotExist(int umid){
        Account account = accountRepository.findOne(umid);
        if(account==null) {
            Account accounttmp = new Account(umid);
            account = accountRepository.save(accounttmp);
            if (account == null) {
                log.info("save account failed! umid is:"+umid);
            }
        }
        return account;
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    public RetMessage getBaseInfo(String area,int umid,String sessionId){
        RetMessage retMessage = null;
        RestTemplate restTemplate = new RestTemplate();
        String url = loingUrl + "gi?functionId=6&generalInput=&umid="+umid+"&sessionId="+sessionId+"&area="+area;
        retMessage = restTemplate.getForObject(url, RetMessage.class);
        if(retMessage==null){
            retMessage = returnFail(area,"-16");
            log.info("get baseinfo failed,umid is:"+umid);
        }
        return retMessage;
    }

    public RetMessage getLocalInfo(String area,int umid){
        RetMessage retMessage = new RetMessage();
        Account account = accountRepository.findOne(umid);
        if(account!=null){
            retMessage.setErrorCode("0");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
            retMessage.setRetContent("address="+account.getAddress()+"<[CDATA]>grade="+account.getGrade()+"<[CDATA]>nickName="+account.getNickName());

        }else{
            retMessage.setErrorCode("-11");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"-11"));
        }
        return retMessage;
    }

    public RetMessage setLocalInfo(String area,int umid,Map<String, String> map,int grade){
        RetMessage retMessage = new RetMessage();
        Account account = accountRepository.findOne(umid);
        if(account!=null){
            account.setAddress(map.get("address"));
            //account.setCity(map.get("city"));
            //account.setProvince(map.get("province"));
            account.setGrade(grade);
            account.setNickName(map.get("nickName"));
            if(accountRepository.save(account)!=null) {
                retMessage.setErrorCode("0");
                retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            }

        }else{
            retMessage.setErrorCode("-11");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area, "-11"));
        }
        return retMessage;
    }


}