package allcom.controller;

import allcom.entity.*;
import allcom.oxmapper.QuestionBean;
import allcom.oxmapper.QuestionOmxService;
import allcom.oxmapper.SubQuestionBean;
import allcom.service.AccountService;
import allcom.service.QuestionCgService;
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
    private QuestionCgService questionCgService;
    @Autowired
    private QuestionOmxService questionOmxService;

    // 通过id获得题目（json形式）
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
                            retQuestionBean.setNotebookId(question.getNotebookId());
                            List<AnswerAndNote> answerAndNoteList = questionService.getAnswerAndNoteList(umid,id);
                            if(answerAndNoteList !=null){
                                retQuestionBean.setAnswerAndNoteList(answerAndNoteList);
                            }
                            List<QuestionTag> questionTagList = questionService.getQuestionTagList(id);
                            if(questionTagList !=null){
                                List<Tag> tagList = questionService.getTagListByQuestionTagList(questionTagList);
                                if(tagList != null){
                                    retQuestionBean.setTagList(tagList);
                                }
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


    // 通过id获得题目草稿（json形式）
    // http://localhost:8080/getquestioncg?umid=1&id=19&sessionId=111
    @RequestMapping(value = "/getquestioncg")
    public RetQuestionCgBean getRetQuestionCg(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "id",required = true) long id,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {
        log.info("cg id is:"+ id);
        RetQuestionCgBean retQuestionCgBean = new RetQuestionCgBean("-1");

        if (sessionService.verifySessionId(umid, sessionId)) {
            Account account = accountService.createAccountIfNotExist(umid);
            if (account != null) {
                QuestionCg questionCg = questionCgService.getQuestionCgById(id);
                if(questionCg!=null && questionCg.getUmid()==umid){
                    String xmlPath = questionCg.getContentPath();
                    try {
                        QuestionBean questionBean = questionOmxService.loadQuestionBean(xmlPath);
                        if(questionBean!=null) {
                            retQuestionCgBean.setErrorCode("0");
                            retQuestionCgBean.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                            retQuestionCgBean.setQuestionBean(questionBean);
                            List<AnswerAndNoteCg> answerAndNoteCgList = questionCgService.getAnswerAndNoteCgList(umid, id);
                            if(answerAndNoteCgList !=null){
                                retQuestionCgBean.setAnswerAndNoteCgList(answerAndNoteCgList);
                            }
                        }

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }else{
                    retQuestionCgBean.setErrorCode("-23");
                    retQuestionCgBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-23"));
                    log.info("umid:" + umid + " found no questioncg,questionId is:"+id);
                }
            }else{
                //获取account信息失败
                retQuestionCgBean.setErrorCode("-15");
                retQuestionCgBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-15"));
                log.info("umid:" + umid + " failed to get account");
            }
        }else {
            retQuestionCgBean.setErrorCode("-4");
            retQuestionCgBean.setErrorMessage(GlobalTools.getMessageByLocale(area,"-4"));
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }
        return retQuestionCgBean;
    }


    //分页获取草稿摘要
    //http://localhost:8080/getcgsummary?umid=1&sessionId=111&pageNumber=1&pageSize=2
    @RequestMapping(value = "/getcgsummary")
    public RetQuestionSummary getCgSummary(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "pageNumber",required = true) int pageNumber,
            @RequestParam(value = "pageSize",required = false,defaultValue = "20") int pageSize,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("in getcgsummary,umid is:"+umid +"pageNumber is:"+pageNumber +"pageSize is:"+pageSize);
        RetQuestionSummary ret = new RetQuestionSummary("-1");
        if (sessionService.verifySessionId(umid, sessionId)) {
            Account account = accountService.createAccountIfNotExist(umid);
            if (account != null) {
                ret = questionCgService.getCgSummary(umid, pageNumber, pageSize, area);
            }else{
                //获取account信息失败
                ret.setErrorCode("-15");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-15"));
                log.info("umid:" + umid + " failed to get account");
            }
        }else {
            ret.setErrorCode("-4");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-4"));
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }
        return ret;
    }

    //分页获取题目摘要
    //http://localhost:8080/getquestionsummary?umid=1&sessionId=111&pageNumber=1&pageSize=2
    @RequestMapping(value = "/getquestionsummary")
    public RetQuestionSummary getQuestionSummary(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "pageNumber",required = true) int pageNumber,
            @RequestParam(value = "pageSize",required = false,defaultValue = "20") int pageSize,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area
    ) {
        log.info("in getquestionsummary,umid is:"+umid +"pageNumber is:"+pageNumber +"pageSize is:"+pageSize);
        RetQuestionSummary ret = new RetQuestionSummary("-1");
        if (sessionService.verifySessionId(umid, sessionId)) {
            Account account = accountService.createAccountIfNotExist(umid);
            if (account != null) {
                ret = questionService.getQuestionSummary(umid, pageNumber, pageSize, area);
            }else{
                //获取account信息失败
                ret.setErrorCode("-15");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-15"));
                log.info("umid:" + umid + " failed to get account");
            }
        }else {
            ret.setErrorCode("-4");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-4"));
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }
        return ret;
    }

}
