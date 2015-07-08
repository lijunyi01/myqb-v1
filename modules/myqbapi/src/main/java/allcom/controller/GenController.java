package allcom.controller;

import allcom.entity.Account;
import allcom.service.AccountService;
import allcom.service.SessionService;
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


        if (sessionService.verifySessionId(umid, sessionId)) {
            Map<String, String> inputMap = GlobalTools.parseInput(generalInput);
            Account account = accountService.createAccountIfNotExist(umid);

            if (account != null) {
                if (functionId == 1) {
                    //登录验证，该步骤可以不用，非强制
                    ret = accountService.returnFail(area, "0");
                    log.info("login success! umid is:" + umid);
                } else if (functionId == 2) {
                    //设置本地存储的用户账户信息

                    int grade_i = -1;
                    String grade = inputMap.get("grade");
                    if(grade == null){
                        grade = "";
                    }
                    try{
                        grade_i = Integer.parseInt(grade);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                    if(grade_i <1 || grade_i >20){
                        ret = accountService.returnFail(area,"-14");
                    }else{
                        ret = accountService.setLocalInfo(area,umid,inputMap,grade_i);
                    }

                } else if (functionId == 3) {
                    //获取本地存储的用户账户信息
                    ret = accountService.getLocalInfo(area,umid);
                    log.info("umid:"+umid+" get localinfo result:" + ret.getErrorCode());
                } else if (functionId == 6) {
                    //获取phoneNumber,email等基本信息（从loginsite取）
                    ret = accountService.getBaseInfo(area, umid, sessionId);
                    log.info("umid:"+umid+" get baseinfo result:" + ret.getErrorCode());
                }

            }else{
                //获取account信息失败
                ret = accountService.returnFail(area, "-15");
                log.info("umid:" + umid + " failed to get account");
            }

        }else {
            ret = sessionService.returnFail(area, "-4");
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }


        return ret;
    }

}
