package allcom.controller;

import allcom.service.AccountService;
import allcom.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
public class GenController {


    private static Logger log = LoggerFactory.getLogger(GenController.class);

    @Autowired
    private SessionService sessionService;

    // 通用接口
    @RequestMapping(value = "/gi")
    public RetMessage loginAuth(
            @RequestParam(value="functionId") int functionId,
            @RequestParam(value = "username",required = true)String username,
            @RequestParam(value = "generalInput",required = true) String generalInput,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("In general interface,functionid is:"+functionId +"; general input params is:"+generalInput);
        RetMessage ret = null;

        //sessionId验证
        if(sessionService.verifySessionId(username,sessionId)){
            //开户
            if(functionId==1){

            }else if(functionId==2){
                ;
            }

        }else{
            ret = sessionService.returnFail(area);
            log.info("username:"+username+" failed to check sessionid:"+sessionId);
        }

        return ret;
    }

}
