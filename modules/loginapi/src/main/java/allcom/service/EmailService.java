package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.EmailVerifyCodeRepository;
import allcom.email.MailBean;
import allcom.email.MailUtil;
import allcom.entity.EmailVerifyCode;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class EmailService {
    private static Logger log = LoggerFactory.getLogger(EmailService.class);

    @Value("${systemparam.emailverifycodetimeout}")
    private long verifyEmailTimeout;
    @Value("${email.mailTemplatePath}")
    private String mailTemplatePath;

    @Autowired
    EmailVerifyCodeRepository emailVerifyCodeRepository;
    @Autowired
    MailUtil mailUtil;

    public RetMessage sendEmail(String email,String area){
        RetMessage retMessage = new RetMessage();
        //key 为纯数字6位随机串
        String key = GlobalTools.getRandomString(6,true);
        //调用email发送接口发送email，传入参数为email和key
        boolean sendresult = false;
        sendresult = mailSend(email,key);

        Timestamp sendTime = new Timestamp(System.currentTimeMillis());
        EmailVerifyCode emailVerifyCode = new EmailVerifyCode(email,key,sendTime,"0");

        if(sendresult){
            retMessage.setErrorCode("0");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
        }else{
            retMessage.setErrorCode("-1");
            retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,"-1"));
            emailVerifyCode.setSendResult("-1");
        }
        emailVerifyCodeRepository.save(emailVerifyCode);

        //删除1天前的邮件验证码记录
        emailVerifyCodeRepository.deleteOldRecord(GlobalTools.getTimeBefore(3600*24));

        return retMessage;
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    public boolean verifyEmailVerifyCode(String email,String emailKey){
        boolean ret = false;

        List<EmailVerifyCode> emailVerifyCodeList = emailVerifyCodeRepository.findByEmailAndEmailKey(email, emailKey);
        for(EmailVerifyCode emailVerifyCode: emailVerifyCodeList){
            Timestamp sendTime = emailVerifyCode.getSendTime();
            Timestamp currentTime = new Timestamp(System.currentTimeMillis());
            if(GlobalTools.getTimeDifference(currentTime,sendTime) < verifyEmailTimeout ){
                ret = true;
                break;
            }
        }
        return ret;
    }

    //email 实际发送
    private boolean mailSend(String emailto,String key){
        boolean ret =false;
        String template_s = "";
        String[] mailtos = {""};
        mailtos[0]=emailto;

        MailBean mailBean = new MailBean();
        mailBean.setFrom("lijunyi@allcomchina.com");
        mailBean.setFromName("MyqbService");
        mailBean.setSubject("password reset");
        mailBean.setToEmails(mailtos);

        //从模版文件读取内容至template_s
        template_s = GlobalTools.readFileToString(mailTemplatePath);
        mailBean.setTemplate(template_s);
        // map 用于填充模版数据
        Map map = new HashMap();
        map.put("emailVerifyCode", key);
        mailBean.setData(map);

        //发送邮件
        try {
            if(mailUtil.send(mailBean)){
                ret = true;
            }
        } catch (MessagingException e) {
            e.printStackTrace();
        }

        return ret;
    }

}