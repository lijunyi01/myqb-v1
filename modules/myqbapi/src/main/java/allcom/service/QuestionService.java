package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountRepository;
import allcom.dao.QuestionContentRepository;
import allcom.dao.QuestionRepository;
import allcom.entity.Account;
import allcom.entity.Question;
import allcom.entity.QuestionContent;
import allcom.oxmapper.QuestionBean;
import allcom.oxmapper.QuestionOmxService;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.Map;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class QuestionService {
    private static Logger log = LoggerFactory.getLogger(QuestionService.class);

//    @Value("${sessionid.timeout}")
//    private long sessionIdTimeout;
//
//    @Value("${systemparam.loginurl}")
//    private String loingUrl;

    //可以理解QuestionContent是Question的附属表，分离出来是为了性能
    @Autowired
    private QuestionRepository questionRepository;
    @Autowired
    private QuestionContentRepository questionContentRepository;
    @Autowired
    private QuestionOmxService questionOmxService;

    //@Transactional
    //保存一个题目(一部分内容存入数据库，一部分存入xml文件)
    //public boolean createQuestion(int umid,int grade,int multiplexFlag,int questionType,int classType,int classSubType,String content){
    public boolean createQuestion(int umid,Map<String,String> inputMap){
        boolean ret = false;
        long questContentId=-1;

        if(checkInputMapOfCreateQuestion(inputMap)==false){
            log.info("param check failed in checkInpuMap! inputMap is:"+ inputMap);
        }else{
            //将供检索的题目内容保存到myqb_questioncontent表
            QuestionContent questionContent = new QuestionContent(umid);
            questionContent.setContent(inputMap.get("content"));
            QuestionContent questionContent1 = questionContentRepository.save(questionContent);
            if (questionContent1 != null) {
                questContentId = questionContent1.getId();
            }

            if (questContentId != -1) {
                //根据传入的信息生成xml文件并保存到指定的路径
                String contentPath = "";
                contentPath = saveInXml(questContentId,inputMap);

                //将题目相关信息保存到myqb_question表
                if (contentPath != null && !contentPath.equals("")) {
                    int grade = Integer.parseInt(inputMap.get("grade"));
                    int multiplexFlag = Integer.parseInt(inputMap.get("multiplexFlag"));
                    int questionType = Integer.parseInt(inputMap.get("questionType"));
                    int classType = Integer.parseInt(inputMap.get("classType"));
                    int classSubType = Integer.parseInt(inputMap.get("classSubType"));
                    Question question = new Question(umid, grade, multiplexFlag, questionType, classType, classSubType, questContentId);
                    question.setContentPath(contentPath);
                    //...
                    if (questionRepository.save(question) != null) {
                        ret = true;
                    }
                }

            }
        }
        return ret;
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }

    //专用于题目数据的保存，不用与其它functionId的generalInput生成的inputMap的校验
    private boolean checkInputMapOfCreateQuestion(Map<String,String> inputMap){
        boolean ret = false;
        String content = inputMap.get("content");
        if(content==null || content.equals("") ){
            log.info("fail in checkInputMapOfCreateQuestion: content is empty!");
        }else if(!GlobalTools.isNumeric(inputMap.get("grade"))){
            log.info("fail in checkInputMapOfCreateQuestion: param grade error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("multiplexFlag"))){
            log.info("fail in checkInputMapOfCreateQuestion: param multiplexFlag error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("questionType"))){
            log.info("fail in checkInputMapOfCreateQuestion: param questionType error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("classType"))){
            log.info("fail in checkInputMapOfCreateQuestion: param classType error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("classSubType"))){
            log.info("fail in checkInputMapOfCreateQuestion: param classSubType error!");
        }else{
            ret = true;
        }
        return ret;
    }

    //返回生成的xml文件的完整路径
    private String saveInXml(long questContentId,Map<String,String> inputMap){
        String ret ="";
        QuestionBean questionBean = new QuestionBean(questContentId,inputMap.get("classType"),inputMap.get("classSubType"),inputMap.get("content"));
        try {
            ret = questionOmxService.saveQuestionBean(questionBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

}