package allcom.controller;

import allcom.service.AccountService;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.NamedThreadLocal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created by ljy on 15/5/12.
 * class 上的@RequestMapping("/aaa")注释和方法上的@RequestMapping("/greeting")要叠加
 * http://localhost:8080/aaa/greeting?name=ljy  才能正常访问；若果class上没有@RequestMapping("/aaa")，则
 * 访问http://localhost:8080/greeting?name=ljy 即可
 */

@RestController
public class LoginController {

    @Autowired
    private AccountService accountService;

    private static Logger log = LoggerFactory.getLogger(LoginController.class);

    //  客户端访问URL    http://localhost:8080/login?username=ljy&password=sdf
    @RequestMapping(value = "/login")
    public RetMessage loginAuth(
            @RequestParam(value="username",required = true) String username,
            @RequestParam(value="password",required = true) String password,
            @RequestParam(value = "devicetype",required = false,defaultValue = "web") String devicetype,
            @RequestParam(value = "deviceinfo",required = false,defaultValue = "")String deviceinfo,
            @RequestParam(value = "ip",required = false,defaultValue = "")String ip,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {

        RetMessage ret = null;
        log.info("userlogin,username is:"+username +" and password is:"+password + " and ip is:"+ip);
        ret = accountService.auth(username,password,area);
        int umid=0;
        String errorCode = ret.getErrorCode();
        if(errorCode.equals("0")||errorCode.equals("-9")){
            String s[] = ret.getRetContent().split("<[CDATA]>");
            umid= GlobalTools.convertStringToInt(s[0]);
        }
        accountService.recordLogin(umid,username,ip,errorCode,devicetype,deviceinfo);
        return ret;
    }

    @RequestMapping(value = "/errorpage")
    public String getError(){
        return "error page!!!";
    }


    @RequestMapping(value = "/")
    public String getHome(){
        return "home page!!!";
    }


}
