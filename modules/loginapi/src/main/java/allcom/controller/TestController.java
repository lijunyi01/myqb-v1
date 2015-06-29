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

    private static Logger log = LoggerFactory.getLogger(TestController.class);


    // 测试用，客户端访问URL    http://localhost:8080/createuser?phoneNumber=13818002196&password=sdf&site=192.168.0.88
    @RequestMapping(value = "/createuser")
    public RetMessage createUser(
            @RequestParam(value="phoneNumber") String phoneNumber,
            @RequestParam(value="password") String password,
            @RequestParam(value="role",required = false,defaultValue = "ROLE_USER") String role,
            @RequestParam(value="site") String site
    ) {
        String errorCode="-1";
        String errorMessage="失败";
        String retContent="";

        //log.info("createUser,username is:"+username +" and password is:"+password);
        //AccountService accountService = new AccountService();
        if(accountService.createAccount(phoneNumber,password,role,site)){
            errorCode="0";
            errorMessage="成功";
        }
        return new RetMessage(errorCode,errorMessage,retContent);
    }

}
