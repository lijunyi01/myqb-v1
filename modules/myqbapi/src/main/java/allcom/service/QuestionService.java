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
import allcom.oxmapper.SubQuestion;
import allcom.oxmapper.SubQuestionBean;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
            String searchString=inputMap.get("subject");
            if(!inputMap.get("contentHeader").equals("")){
                searchString = searchString + "|" + inputMap.get("contentHeader");
            }
            //TODO: inputMap.get("subQuestions") 需要进一步处理，提取内容信息，以获取更精准的查询效果；可考虑这里不置入子题信息，
            //TODO: 待之后解析子题后（getSubQuestionList方法里）再将子题内容update进去
            searchString = searchString + "|" + inputMap.get("subQuestions");

            QuestionContent questionContent = new QuestionContent(umid);
            questionContent.setContent(searchString);
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
                    Question question = new Question(umid, grade, multiplexFlag,questionType,classType, classSubType, questContentId,inputMap.get("subject"));
                    question.setContentPath(contentPath);

                    if (questionRepository.save(question) != null) {
                        ret = true;
                    }
                }

            }
        }
        return ret;
    }

    public RetMessage getIdsByType(int umid,Map<String,String> inputMap,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<Question> questionList = questionRepository.findByUmid(umid);
        if(!questionList.isEmpty()){
            for(Question question:questionList){
                if(questionHitOn(question,inputMap)){
                    if(retContent.equals("")){
                        retContent = question.getId()+"";
                    }else{
                        retContent = retContent + ":" +question.getId();
                    }
                }
            }
            ret.setRetContent(retContent);
        }
        ret.setErrorCode("0");
        ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
        return ret;
    }

    public RetMessage getIdsByContent(int umid,String content,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<QuestionContent> questionContentList = questionContentRepository.findByUmidAndContent(umid,content);
        if(!questionContentList.isEmpty()){
            for(QuestionContent questionContent:questionContentList){
                Question question = questionRepository.findByUmidAndQuestionContentId(umid,questionContent.getId());
                if(question != null){
                    if(retContent.equals("")){
                        retContent = question.getId()+"";
                    }else{
                        retContent = retContent + ":" +question.getId();
                    }
                }
            }
            ret.setRetContent(retContent);
        }
        ret.setErrorCode("0");
        ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
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
        String subject = inputMap.get("subject");
        if(subject==null || subject.equals("") ){
            log.info("fail in checkInputMapOfCreateQuestion: content is empty!");
        }else if(!GlobalTools.isNumeric(inputMap.get("grade"))){
            log.info("fail in checkInputMapOfCreateQuestion: param grade error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("multiplexFlag"))){
            log.info("fail in checkInputMapOfCreateQuestion: param multiplexFlag error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("classType"))){
            log.info("fail in checkInputMapOfCreateQuestion: param classType error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("questionType"))){
            log.info("fail in checkInputMapOfCreateQuestion: param questionType error!");
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
        QuestionBean questionBean = new QuestionBean(questContentId,inputMap.get("classType"),inputMap.get("classSubType"),inputMap.get("multiplexFlag"),inputMap.get("subQuestionCount"),inputMap.get("subject"));
        ArrayList<SubQuestionBean> subBeanList = getSubQuestionList(inputMap.get("subQuestions"));
        SubQuestion subQuestion = new SubQuestion();
        subQuestion.setSubQuestionBeanList(subBeanList);
        questionBean.setSubQuestion(subQuestion);
        questionBean.setContentHeader(inputMap.get("contentHeader"));
        try {
            ret = questionOmxService.saveQuestionBean(questionBean);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private ArrayList<SubQuestionBean> getSubQuestionList(String subQuestionString){
        ArrayList<SubQuestionBean> retList = new ArrayList<SubQuestionBean>();
        String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
        for(String str:a){
            //一个map就是一个子题
            Map<String, String> map = GlobalTools.parseInput(str,"\\<\\[CDATA2\\]\\>");
            SubQuestionBean subQuestionBean = new SubQuestionBean(map.get("seqId"),map.get("qType"),map.get("content"));
            subQuestionBean.setAttachedInfo(map.get("attachedInfo"));
            subQuestionBean.setAttachmentIds(map.get("attachmentIds"));
            subQuestionBean.setCorrectAnswer(map.get("correctAnswer"));
            subQuestionBean.setWrongAnswer(map.get("wrongAnswer"));
            subQuestionBean.setNote(map.get("note"));
            retList.add(subQuestionBean);
        }
        return retList;
    }

    //按question的各种类型核对，完全符合条件才返回true
    private boolean questionHitOn(Question question,Map<String,String> conditionMap){
        boolean ret = true;
        for (String key : conditionMap.keySet()) {
            //System.out.println("key= "+ key + " and value= " + conditionMap.get(key));
            if(key.equals("grade")){
                if(question.getGrade()!=GlobalTools.convertStringToInt(conditionMap.get("grade"))){
                    ret = false;
                    break;
                }
            }else if(key.equals("classType")){
                if(question.getClassType()!=GlobalTools.convertStringToInt(conditionMap.get("classType"))){
                    ret = false;
                    break;
                }
            }else if(key.equals("classSubType")){
                if(question.getClassSubType()!=GlobalTools.convertStringToInt(conditionMap.get("classSubType"))){
                    ret = false;
                    break;
                }
            }else if(key.equals("questionType")){
                if(question.getQuestionType()!=GlobalTools.convertStringToInt(conditionMap.get("questionType"))){
                    ret = false;
                    break;
                }
            }
        }

        return ret;
    }

    public Question getQuestionById(long id){
        Question question = questionRepository.findOne(id);
        return question;
    }


}