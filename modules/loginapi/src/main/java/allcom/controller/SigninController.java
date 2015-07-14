package allcom.controller;

import allcom.entity.Account;
import allcom.service.AccountService;
import allcom.service.EmailService;
import allcom.service.SessionService;
import allcom.service.SmsService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    @Autowired
    private EmailService emailService;

    @Value("${systemparam.debug}")
    private int systemdebugflag;

    //发送短信验证码的接口
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

    //忘记密码时，通过短信验证码重置密码的接口
    @RequestMapping(value = "/signin/resetpassbyphone")
    public RetMessage resetPassword(
            @RequestParam(value = "phoneNumber",required = true)String phoneNumber,
            @RequestParam(value = "smsVerifyCode",required = true)String smsVerifyCode,
            @RequestParam(value = "newPassword",required = true)String newPassword,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("resetpassword params:phoneNumber:"+phoneNumber+";verifyCode:"+smsVerifyCode);
        RetMessage ret = null;

        //getNumberOfUsersByPhoneNumber()方法返回 非0即1
        if (accountService.getNumberOfUsersByPhoneNumber(phoneNumber) == 0){
            log.info("user not exists! phoneNumber:" + phoneNumber);
            ret = smsService.returnFail(area,"-11");
        }else {
            if(!smsService.verifySmsVerifyCode(phoneNumber, smsVerifyCode)) {
                log.info("verify VerifyCode failed! phonenumber is:" + phoneNumber + " and verifycode is:"+smsVerifyCode);
                ret = smsService.returnFail(area, "-7");
            }else{
                if(accountService.resetPassword(phoneNumber,newPassword)){
                    ret = smsService.returnFail(area,"0");
                    log.info("resetpassword success! phonenumber is:" + phoneNumber);
                }else {
                    ret = smsService.returnFail(area,"-1");
                    log.info("resetpassword failed! phonenumber is:" + phoneNumber);
                }
            }
        }
        return ret;
    }

    //忘记密码时，通过邮件验证码重置密码的接口
    @RequestMapping(value = "/signin/resetpassbymail")
    public RetMessage resetPassword2(
            @RequestParam(value = "email",required = true)String email,
            @RequestParam(value = "mailVerifyCode",required = true)String mailVerifyCode,
            @RequestParam(value = "newPassword",required = true)String newPassword,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("resetpassword params:email:"+email+";verifyCode:"+mailVerifyCode);
        RetMessage ret = null;

        if (accountService.getNumberOfUsersByEmail(email) == 0){
            log.info("user not exists! email:" + email);
            ret = emailService.returnFail(area,"-11");
        }else {
            if(!emailService.verifyEmailVerifyCode(email, mailVerifyCode)) {
                log.info("verify VerifyCode failed! email is:" + email + " and verifycode is:"+mailVerifyCode);
                ret = emailService.returnFail(area, "-7");
            }else{
                if(accountService.resetPasswordByMail(email,newPassword)){
                    ret = emailService.returnFail(area,"0");
                    log.info("resetpassword success! email is:" + email);
                }else {
                    ret = emailService.returnFail(area,"-1");
                    log.info("resetpassword failed! email is:" + email);
                }
            }
        }
        return ret;
    }

    //获取手机号码是否是已有用户的接口（用于通过手机重置密码）
    @RequestMapping(value = "/signin/isphonenumberexist")
    public RetMessage isPhoneNumberExist(
            @RequestParam(value = "phoneNumber",required = true)String phoneNumber,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("ask if phoneNumber exists, phoneNumber:"+phoneNumber);
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
            if(accountService.getNumberOfUsersByPhoneNumber(phoneNumber)>0){
                ret= accountService.returnFail(area,"0");
            }else{
                ret= accountService.returnFail(area,"-11");
            }
        }else{
            ret = accountService.returnFail(area, "-5");
        }

        return ret;
    }

    //获取邮件地址是否经过验证的接口
    @RequestMapping(value = "/signin/isemailverified")
    public RetMessage isEmailVerified(
            @RequestParam(value = "email",required = true)String email,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("ask if email is verified, email:"+email);
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
            ret = accountService.isEmailVerified(email, area);
        }else{
            ret = accountService.returnFail(area, "-5");
        }

        return ret;
    }

    //发送邮件（内含重置密码的联接）的接口
    @RequestMapping(value = "/signin/sendpassresetmail")
    public RetMessage sendEmail(
            @RequestParam(value = "email",required = true)String email,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("send verify email, email:"+email);
        RetMessage ret = null;

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
        }
        String vcodeverifyflag=(String)session.getAttribute("vcodeverifyflag");
        if(vcodeverifyflag == null){
            vcodeverifyflag = "";
        }

        if(systemdebugflag==1){
            vcodeverifyflag = "success";
        }

        if(vcodeverifyflag.equals("success")){
            ret = emailService.sendEmail(email,"passReset",area);
        }else{
            ret = emailService.returnFail(area, "-5");
        }

        return ret;
    }

    //判断邮件地址是否有效
    @RequestMapping(value = "/signin/ismailverified")
    public RetMessage isMailAddressVerified(
            @RequestParam(value = "email",required = true)String email,
            @RequestParam(value = "verifyCode",required = true)String verifyCode,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("verify email address, email:"+email + " and verifyCode:"+verifyCode);
        RetMessage ret = null;
        if(emailService.verifyEmailVerifyCode(email,verifyCode)){
            ret = accountService.setMailVerified(email,area);
        }else{
            ret = emailService.returnFail(area, "-2");
        }

        return  ret;
    }



}
