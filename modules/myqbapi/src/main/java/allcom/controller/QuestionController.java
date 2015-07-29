package allcom.controller;

import allcom.entity.Account;
import allcom.entity.AnswerAndNote;
import allcom.entity.Question;
import allcom.oxmapper.QuestionBean;
import allcom.oxmapper.QuestionOmxService;
import allcom.oxmapper.SubQuestionBean;
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
import java.util.*;

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

//    // 通过id获得题目列表（同时获得多个题目，数组＋json形式）
//    @RequestMapping(value = "/getquestions")
//    public List<RetQuestionBean> getRetQuestions(
//            @RequestParam(value = "umid",required = true)int umid,
//            @RequestParam(value = "ids",required = true) String ids,
//            @RequestParam(value = "sessionId",required = true)String sessionId,
//            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
//            HttpServletRequest request
//    ) {
//        log.info("ids is:"+ ids);
//        List<RetQuestionBean> retQuestionBeanList = new ArrayList<RetQuestionBean>();
//
//        if (sessionService.verifySessionId(umid, sessionId)) {
//            String[]  a= ids.split(":");
//            Account account = accountService.createAccountIfNotExist(umid);
//
//            if (account != null) {
//                for(String str:a){
//                    Question question = questionService.getQuestionById(GlobalTools.convertStringToLong(str));
//                    if(question!=null){
//                        String xmlPath = question.getContentPath();
//                        QuestionBean questionBean = null;
//                        try {
//                            questionBean = questionOmxService.loadQuestionBean(xmlPath);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        if(questionBean!=null) {
//                            questionBeanList.add(questionBean);
//                        }
//                    }
//                }
//
//            }else{
//                //获取account信息失败
//                retQuestionBean.setErrorCode("-15");
//                retQuestionBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-15"));
//                log.info("umid:" + umid + " failed to get account");
//            }
//
//        }else {
//            //ret = sessionService.returnFail(area, "-4");
//            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
//        }
//
//        return questionBeanList;
//    }

    // 通过id获得题目列表（同时获得多个题目，数组＋json形式）
    // http://localhost:8080/getquestion?umid=1&id=19&sessionId=111
    @RequestMapping(value = "/getquestion")
    public RetQuestionBean getRetQuestion(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "id",required = true) long id,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("id is:"+ id);
        RetQuestionBean retQuestionBean = new RetQuestionBean("-1");

        if (sessionService.verifySessionId(umid, sessionId)) {
            Account account = accountService.createAccountIfNotExist(umid);
            if (account != null) {
                Question question = questionService.getQuestionById(id);
                if(question!=null && question.getUmid()==umid){
                    String xmlPath = question.getContentPath();
                    try {
                        QuestionBean questionBean = questionOmxService.loadQuestionBean(xmlPath);
                        if(questionBean!=null) {
                            retQuestionBean.setErrorCode("0");
                            retQuestionBean.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                            retQuestionBean.setQuestionBean(questionBean);
                            List<AnswerAndNote> answerAndNoteList = questionService.getAnswerAndNoteList(umid,id);
                            if(answerAndNoteList !=null){
                                retQuestionBean.setAnswerAndNoteList(answerAndNoteList);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    retQuestionBean.setErrorCode("-23");
                    retQuestionBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-23"));
                    log.info("umid:" + umid + " found no question,questionId is:"+id);
                }
            }else{
                //获取account信息失败
                retQuestionBean.setErrorCode("-15");
                retQuestionBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-15"));
                log.info("umid:" + umid + " failed to get account");
            }
        }else {
            retQuestionBean.setErrorCode("-4");
            retQuestionBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-4"));
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }
        return retQuestionBean;
    }

}
