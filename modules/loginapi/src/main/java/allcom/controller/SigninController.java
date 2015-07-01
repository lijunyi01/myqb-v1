package allcom.controller;

import allcom.entity.Account;
import allcom.service.AccountService;
import allcom.service.SessionService;
import allcom.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

/**
 * Created by ljy on 15/5/12.
 * class 上的@RequestMapping("/aaa")注释和方法上的@RequestMapping("/greeting")要叠加
 * http://localhost:8080/aaa/greeting?name=ljy  才能正常访问；若果class上没有@RequestMapping("/aaa")，则
 * 访问http://localhost:8080/greeting?name=ljy 即可
 */

@RestController
public class SigninController {


    private static Logger log = LoggerFactory.getLogger(SigninController.class);

    @Autowired
    private SmsService smsService;
    @Autowired
    private AccountService accountService;

    @RequestMapping(value = "/signin/sendsms")
    public RetMessage signIn(
            @RequestParam(value = "phoneNumber",required = true)String phoneNumber,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("request to send verify sms to:"+phoneNumber);
        RetMessage ret = null;

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
        }
        String vcodeverifyflag=(String)session.getAttribute("vcodeverifyflag");
        if(vcodeverifyflag == null){
            vcodeverifyflag = "";
        }
        if(vcodeverifyflag.equals("success")){
            ret = smsService.sendSms(phoneNumber,area);
        }else{
            ret = smsService.returnFail(area,"-5");
        }

        return ret;
    }

    //http://192.168.8.104:8080/signin/createuser?phoneNumber=18001831657&smsVerifyCode=111111&initPassword=111111
    @RequestMapping(value = "/signin/createuser")
    public RetMessage createUser(
            @RequestParam(value = "phoneNumber",required = true)String phoneNumber,
            @RequestParam(value = "smsVerifyCode",required = true)String smsVerifyCode,
            @RequestParam(value = "initPassword",required = true)String initPassword,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("createuser params:phoneNumber:"+phoneNumber+";smsVerifyCode:"+smsVerifyCode);

        RetMessage ret = null;
        String clientip = request.getRemoteAddr();

        //验证短信验证码
        if(!smsService.verifySmsVerifyCode(phoneNumber, smsVerifyCode)){
            log.info("verify SmsVerifyCode failed!");
            ret = smsService.returnFail(area,"-7");
        } else if(accountService.getNumberOfUsersByPhoneNumber(phoneNumber)>0){   //该手机号码已经注册过
            log.info("phonenumber exists already! phonenumber is:" + phoneNumber);
            ret = accountService.returnFail(area,"-10");
        } else {
            String site = "";
            //按一定的逻辑决定用户的业务站点参数site
            site = accountService.getSite(clientip);
            if(accountService.createAccount(phoneNumber,initPassword,"ROLE_USER",site)){
                log.info("create user success! phonenumber is:" + phoneNumber);
                ret = accountService.returnFail(area,"0");
            }else{
                log.info("create user failed! phonenumber is:" + phoneNumber);
                ret = accountService.returnFail(area,"-8");
            }
        }
        return ret;
    }

    @RequestMapping(value = "/signin/resetpass")
    public RetMessage resetPassword(
            @RequestParam(value = "userName",required = true)String userName,
            @RequestParam(value = "smsverifyCode",required = true)String smsverifyCode,
            @RequestParam(value = "newPassword",required = true)String newPassword,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("resetpassword params:userName:"+userName+";verifyCode:"+smsverifyCode);
        RetMessage ret = null;

        int numberOfUsers = accountService.getNumberOfUsers(userName);
        if (numberOfUsers == 0){
            log.info("user not exists! username:" + userName);
            ret = smsService.returnFail(area,"-11");
        } else if(numberOfUsers >1){
            log.info("usernumber > 1 ! username:" + userName);
            ret = smsService.returnFail(area,"-12");
        }else {
            String phoneNumber = accountService.getPhoneNumber(userName);
            if(!smsService.verifySmsVerifyCode(phoneNumber, smsverifyCode)) {
                log.info("verify VerifyCode failed! phonenumber is:" + phoneNumber + " and verifycode is:"+smsverifyCode);
                ret = smsService.returnFail(area, "-7");
            }else{
                if(accountService.resetPassword(phoneNumber,newPassword)){
                    log.info("resetpassword success! phonenumber is:" + phoneNumber);
                }else {
                    log.info("resetpassword failed! phonenumber is:" + phoneNumber);
                }
            }
        }
        return ret;
    }

}
