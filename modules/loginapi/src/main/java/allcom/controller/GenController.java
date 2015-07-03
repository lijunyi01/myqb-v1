package allcom.controller;

import allcom.entity.Account;
import allcom.service.AccountService;
import allcom.service.SessionService;
import allcom.service.SmsService;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by ljy on 15/5/12.
 * class 上的@RequestMapping("/aaa")注释和方法上的@RequestMapping("/greeting")要叠加
 * http://localhost:8080/aaa/greeting?name=ljy  才能正常访问；若果class上没有@RequestMapping("/aaa")，则
 * 访问http://localhost:8080/greeting?name=ljy 即可
 */

@RestController
public class GenController {


    private static Logger log = LoggerFactory.getLogger(GenController.class);

    @Autowired
    private SessionService sessionService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private SmsService smsService;

    // 通用接口,用于已经完成登录验证后的其它请求
    @RequestMapping(value = "/gi")
    public RetMessage generalInterface(
            @RequestParam(value="functionId",required = true) int functionId,
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "generalInput",required = true) String generalInput,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("In general interface,functionid is:"+functionId +"; general input params is:"+generalInput);
        RetMessage ret = null;
        String clientip = request.getRemoteAddr();

        if(sessionService.verifyIp(functionId,clientip)) {
            if (sessionService.verifySessionId(umid, sessionId)) {
                Map<String,String> inputMap = GlobalTools.parseInput(generalInput);

                if (functionId == 1) {
                    //用于业务平台发起的sessionId验证；由于之前已经完成sessionId校验，此处再校验下ip即可
                    ret = sessionService.returnFail(area,"0");
                } else if (functionId == 2) {
                    //登录完成后的修改密码
                    if(inputMap.size()!=1){
                        ret = accountService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        String newPassword = inputMap.get("newPassword");
                        if(newPassword == null || newPassword.equals("")) {
                            ret = accountService.returnFail(area, "-14");
                            log.info("general input param error:" + generalInput);
                        }else{
                            if(accountService.resetPassword(umid,newPassword)){
                                ret = accountService.returnFail(area, "0");
                                log.info("reset pass success:" + umid);
                            }else{
                                ret = accountService.returnFail(area, "-1");
                                log.info("reset pass failed:" + umid);
                            }
                        }

                    }
                }else if(functionId == 3){
                    //修改手机号码，强制通过短信验证新的手机号码
                    if(inputMap.size()!=2){
                        ret = accountService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        String phoneNumber = inputMap.get("phoneNumber");
                        String smsVerifyCode = inputMap.get("smsVerifyCode");
                        if(phoneNumber == null || phoneNumber.equals("") ||smsVerifyCode == null || smsVerifyCode.equals("")){
                            ret = accountService.returnFail(area, "-14");
                            log.info("general input param error:" + generalInput);
                        }else if(accountService.getNumberOfUsersByPhoneNumber(phoneNumber)>0) {
                            ret = accountService.returnFail(area, "-10");
                            log.info("phoneNumber exists:" + phoneNumber);
                        }else{
                            if(!smsService.verifySmsVerifyCode(phoneNumber, smsVerifyCode)) {
                                log.info("verify SmsVerifyCode failed!");
                                ret = smsService.returnFail(area, "-7");
                            }else {
                                if (accountService.setPhoneNumber(umid, phoneNumber)) {
                                    ret = accountService.returnFail(area, "0");
                                    log.info("set phoneNumber success:" + umid);
                                } else {
                                    ret = accountService.returnFail(area, "-1");
                                    log.info("set phoneNumber failed:" + umid);
                                }
                            }
                        }

                    }

                }else if(functionId == 4){
                    //修改邮箱地址，不强制验证，修改的同时将邮件验证标志置为未验证
                    if(inputMap.size()!=1){
                        ret = accountService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else{
                        String email = inputMap.get("email");
                        if(email == null || email.equals("")){
                            ret = accountService.returnFail(area, "-14");
                            log.info("general input param error:" + generalInput);
                        }else if(accountService.getNumberOfUsersByEmail(email)>0) {
                            ret = accountService.returnFail(area, "-10");
                            log.info("email exists:" + email);
                        }else{
                            if(accountService.setEmail(umid, email)) {
                                ret = accountService.returnFail(area, "0");
                                log.info("set email success:" + umid);
                            }else{
                                ret = accountService.returnFail(area, "-1");
                                log.info("set email failed:" + umid);
                            }
                        }

                    }
                }else if(functionId == 5){
                    //修改昵称
                    if(inputMap.size()!=1){
                        ret = accountService.returnFail(area, "-14");
                        log.info("general input param error:" + generalInput);
                    }else {
                        String nickName = inputMap.get("nickName");
                        if (nickName == null || nickName.equals("")) {
                            ret = accountService.returnFail(area, "-14");
                            log.info("general input param error:" + generalInput);
                        }else if(accountService.getNumberOfUsersByNickName(nickName)>0){
                            ret = accountService.returnFail(area, "-10");
                            log.info("nickName exists:" + nickName);
                        }else{
                            if(accountService.setNickName(umid, nickName)){
                                ret = accountService.returnFail(area, "0");
                                log.info("set nickName success:" + umid);
                            }else{
                                ret = accountService.returnFail(area, "-1");
                                log.info("set nickName failed:" + umid);
                            }
                        }

                    }

                }else if(functionId == 6){
                    //查询phoneNUmber,email,nickName等基本信息
                    ret = accountService.getUserInfo(umid,area);
                    log.info("in getUserInfo,umid is:" + umid+" and result:" + ret.getErrorCode());
                }

            } else {
                ret = sessionService.returnFail(area, "-4");
                log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
            }
        }else{
            ret = sessionService.returnFail(area, "-12");
            log.info("umid:" + umid + " failed to check ip:" + clientip + " functionId is:" +functionId);
        }

        return ret;
    }

}
