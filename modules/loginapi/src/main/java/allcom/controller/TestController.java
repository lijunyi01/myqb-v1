package allcom.controller;

import allcom.service.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by ljy on 15/5/12.
 * class 上的@RequestMapping("/aaa")注释和方法上的@RequestMapping("/greeting")要叠加
 * http://localhost:8080/aaa/greeting?name=ljy  才能正常访问；若果class上没有@RequestMapping("/aaa")，则
 * 访问http://localhost:8080/greeting?name=ljy 即可
 */

@RestController
public class TestController {

    @Autowired
    private AccountService accountService;

    private static Logger log = LoggerFactory.getLogger(TestController
            .class);

    //  客户端访问URL    http://localhost:8080/login?username=ljy&password=sdf
    @RequestMapping(value = "/login")
    public RetMessage loginAuth(@RequestParam(value="username") String username,@RequestParam(value="password") String password) {
        String errorCode="-1";
        String errorMessage="失败";
        String retContent="";

        log.info("userlogin,username is:"+username +" and password is:"+password);

        if(accountService.auth(username,password)) {
            errorCode = "0";
            errorMessage = "成功";
        }
        return new RetMessage(errorCode,errorMessage,retContent);
    }

    // 测试用，客户端访问URL    http://localhost:8080/createuser?username=ljy&password=sdf
    @RequestMapping(value = "/createuser")
    public RetMessage createUser(@RequestParam(value="username") String username,@RequestParam(value="password") String password) {
        String errorCode="-1";
        String errorMessage="失败";
        String retContent="";

        log.info("createUser,username is:"+username +" and password is:"+password);
        //AccountService accountService = new AccountService();
        if(accountService.createAccount(username,password)){
            errorCode="0";
            errorMessage="成功";
        }
        return new RetMessage(errorCode,errorMessage,retContent);
    }

    @RequestMapping(value = "/errorpage")
    public String getError(){
        return "error page!!!";
    }

//
}
