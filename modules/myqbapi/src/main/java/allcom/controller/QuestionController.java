package allcom.controller;

import allcom.entity.Account;
import allcom.entity.Question;
import allcom.oxmapper.QuestionBean;
import allcom.oxmapper.QuestionOmxService;
import allcom.service.AccountService;
import allcom.service.QuestionService;
import allcom.service.SessionService;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ljy on 15/5/12.
 * class 上的@RequestMapping("/aaa")注释和方法上的@RequestMapping("/greeting")要叠加
 * http://localhost:8080/aaa/greeting?name=ljy  才能正常访问；若果class上没有@RequestMapping("/aaa")，则
 * 访问http://localhost:8080/greeting?name=ljy 即可
 */

@RestController
public class QuestionController {


    private static Logger log = LoggerFactory.getLogger(QuestionController.class);

    @Autowired
    private SessionService sessionService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private QuestionOmxService questionOmxService;

    // 通过id获得题目列表（json形式）
    @RequestMapping(value = "/getquestions")
    public List<QuestionBean> getQuestions(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "ids",required = true) String ids,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("ids is:"+ ids);
        List<QuestionBean> questionBeanList = new ArrayList<QuestionBean>();
        RetMessage ret = null;


        if (sessionService.verifySessionId(umid, sessionId)) {
            String[]  a= ids.split(":");
            Account account = accountService.createAccountIfNotExist(umid);

            if (account != null) {
                for(String str:a){
                    Question question = questionService.getQuestionById(GlobalTools.convertStringToLong(str));
                    if(question!=null){
                        String xmlPath = question.getContentPath();
                        QuestionBean questionBean = null;
                        try {
                            questionBean = questionOmxService.loadQuestionBean(xmlPath);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if(questionBean!=null) {
                            questionBeanList.add(questionBean);
                        }
                    }
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

        return questionBeanList;
    }

}
