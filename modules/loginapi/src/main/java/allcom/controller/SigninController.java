package allcom.controller;

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

    @RequestMapping(value = "/signin/sendsms")
    public RetMessage signIn(
            @RequestParam(value = "phoneNumber",required = true,defaultValue = "")String phoneNumber,
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
            ret = smsService.returnFail(area);
        }

        return ret;
    }

    @RequestMapping(value = "/signin/createuser")
    public RetMessage createUser(
            @RequestParam(value = "phoneNumber",required = true,defaultValue = "")String phoneNumber,
            @RequestParam(value = "smsVerifyCode",required = true,defaultValue = "")String smsVerifyCode,
            @RequestParam(value = "initPassword",required = true,defaultValue = "")String initPassword,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("createuser params:phoneNumber"+phoneNumber+";smsVerifyCode"+smsVerifyCode);
        RetMessage ret = null;
        String site = "";
        //按一定的逻辑决定用户的业务站点参数site



        return ret;
    }

}
