package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountSessionRepository;
import allcom.dao.SmsVerifyCodeRepository;
import allcom.entity.AccountSession;
import allcom.entity.SmsVerifyCode;
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
public class SmsService {
    private static Logger log = LoggerFactory.getLogger(SmsService.class);

    @Value("${systemparam.verifycodetimeout}")
    private long verifyCodeTimeout;
    @Value("${systemparam.verifysmscountlimit}")
    private int verifySmsCountLimit;

    @Autowired
    SmsVerifyCodeRepository smsVerifyCodeRepository;

    public RetMessage sendSms(String sessionId,int umid,String phoneNumber,String area){
        RetMessage retMessage = new RetMessage();
        //key 为纯数字6位随机串
        String key = GlobalTools.getRandomString(6,true);
        //调用短信发送接口发送短信，传入参数为phoneNumber和key

        if(umid>0 && (smsVerifyCodeRepository.findSuccessCountByUmid(umid)>verifySmsCountLimit)) {
            retMessage.setErrorCode("-15");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area, "-15"));
            log.info("send sms count exceeds limited number!!! umid is:" + umid);
        }else if(!sessionId.equals("") && (smsVerifyCodeRepository.findSuccessCountBySessionId(sessionId)>verifySmsCountLimit)){
            retMessage.setErrorCode("-15");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area, "-15"));
            log.info("send sms count exceeds limited number!!! sessionId is:" + sessionId);
        }else {
            boolean sendresult = true;
//        sendresult = ...

            Timestamp sendTime = new Timestamp(System.currentTimeMillis());
            SmsVerifyCode smsVerifyCode = new SmsVerifyCode(sessionId,umid,phoneNumber, key, sendTime, "0");

            if (sendresult) {
                retMessage.setErrorCode("0");
                retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            } else {
                retMessage.setErrorCode("-6");
                retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area, "-6"));
                smsVerifyCode.setSendResult("-6");
            }
            smsVerifyCodeRepository.save(smsVerifyCode);

            //删除1天前的短信验证码记录
            smsVerifyCodeRepository.deleteOldRecord(GlobalTools.getTimeBefore(3600 * 24));
        }

        return retMessage;
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    public boolean verifySmsVerifyCode(String phoneNumber,String key){
        boolean ret = false;

        List<SmsVerifyCode> smsVerifyCodeList = smsVerifyCodeRepository.findByPhoneNumberAndSmsContent(phoneNumber,key);
        for(SmsVerifyCode smsVerifyCode: smsVerifyCodeList){
            Timestamp sendTime = smsVerifyCode.getSendTime();
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if(GlobalTools.getTimeDifference(currentTime,sendTime) < verifyCodeTimeout ){
                ret = true;
                break;
            }
        }
        return ret;
    }

}