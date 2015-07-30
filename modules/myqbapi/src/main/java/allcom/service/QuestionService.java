package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.*;
import allcom.entity.*;
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
import java.io.File;
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
    private AnswerAndNoteRepository answerAndNoteRepository;
    @Autowired
    private QuestionOmxService questionOmxService;
    @Autowired
    private AttachmentRepository attachmentRepository;

    //@Transactional
    //保存一个题目(一部分内容存入数据库，一部分存入xml文件)
    //public boolean createQuestion(int umid,int grade,int multiplexFlag,int questionType,int classType,int classSubType,String content){
    public boolean createQuestion(int umid,Map<String,String> inputMap){
        boolean ret = false;
        long questionContentId=-1;
        //long questionId=-1;

        if(checkInputMapOfCreateQuestion(inputMap)==false){
            log.info("param check failed in checkInpuMap! inputMap is:"+ inputMap);
        }else{
            //先在myqb_questioncontent表里插条记录
            //待之后解析子题后再将子题内容，心得等update进myqb_questioncontent （modifyQuestionContent）
            QuestionContent questionContent = new QuestionContent(umid);
            QuestionContent questionContent1 = questionContentRepository.save(questionContent);
            if (questionContent1 != null) {
                questionContentId = questionContent1.getId();
            }

            if (questionContentId != -1) {
                //在myqb_question表先生成一条记录，以获取questionId(即该表的id字段；如果该记录大量字段为空，则说明xml文件未生成或后续数据更新未完成)
                Question question = new Question(umid,questionContentId);
                Question question1 = questionRepository.save(question);
                if(question1 != null){
                    //根据传入的信息生成xml文件并保存到指定的路径
                    String contentPath = "";
                    contentPath = saveInXml(umid,question1.getId(),inputMap);
                    //将题目相关具体信息保存到myqb_question表
                    if (contentPath != null && !contentPath.equals("")) {
                        int grade = Integer.parseInt(inputMap.get("grade"));
                        int multiplexFlag = Integer.parseInt(inputMap.get("multiplexFlag"));
                        int questionType = Integer.parseInt(inputMap.get("questionType"));
                        int classType = Integer.parseInt(inputMap.get("classType"));
                        int classSubType = Integer.parseInt(inputMap.get("classSubType"));
                        question1.setGrade(grade);
                        question1.setMultiplexFlag(multiplexFlag);
                        question1.setQuestionType(questionType);
                        question1.setClassType(classType);
                        question1.setClassSubType(classSubType);
                        question1.setContentPath(contentPath);
                        question1.setSubject(inputMap.get("subject"));
                        if (questionRepository.save(question1) != null) {
                            //保存正确答案，心得备注等信息至数据库
                            saveAnswerAndNote(umid,question1.getId(),inputMap.get("subQuestions"));
                            //保存检索内容至myqb_qestioncontent
                            modifyQuestionContent(umid,questionContentId,inputMap.get("subject"),inputMap.get("contentHeader"),inputMap.get("subQuestions"));
                            //处理附件以及附件表
                            modifyAttachment(umid,question1.getId(),inputMap.get("attachmentIds"),inputMap.get("subQuestions"));
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    public boolean modifyQuestion(int umid,Map<String,String> inputMap){
        boolean ret = false;

        if(checkInputMapOfModifyQuestion(inputMap)==false){
            log.info("param check failed in checkInpuMap! inputMap is:"+ inputMap);
        }else{
            long questionId=Long.parseLong(inputMap.get("questionId"));
            //先修改xml文件，文件名不会变，以questionId命名
            String contentPath = "";
            contentPath = saveInXml(umid,questionId,inputMap);
            if (contentPath != null && !contentPath.equals("")) {
                //该xml成功再处理表
                Question question = questionRepository.findOne(questionId);
                if(question !=null){
                    int grade = Integer.parseInt(inputMap.get("grade"));
                    int multiplexFlag = Integer.parseInt(inputMap.get("multiplexFlag"));
                    int questionType = Integer.parseInt(inputMap.get("questionType"));
                    int classType = Integer.parseInt(inputMap.get("classType"));
                    int classSubType = Integer.parseInt(inputMap.get("classSubType"));
                    question.setGrade(grade);
                    question.setMultiplexFlag(multiplexFlag);
                    question.setQuestionType(questionType);
                    question.setClassType(classType);
                    question.setClassSubType(classSubType);
                    question.setContentPath(contentPath);
                    question.setSubject(inputMap.get("subject"));
                    if (questionRepository.save(question) != null) {
                        //保存正确答案，心得备注等信息至数据库
                        saveAnswerAndNote(umid, questionId, inputMap.get("subQuestions"));
                        //保存检索内容至myqb_qestioncontent
                        modifyQuestionContent(umid,question.getQuestionContentId(),inputMap.get("subject"),inputMap.get("contentHeader"),inputMap.get("subQuestions"));
                        //处理附件以及附件表
                        modifyAttachment(umid,questionId,inputMap.get("attachmentIds"),inputMap.get("subQuestions"));
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

    //专用于题目数据的保存(createQuestion)，不用与其它functionId的generalInput生成的inputMap的校验
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

    //专用于题目数据的修改(modifyQuestion)，不用与其它functionId的generalInput生成的inputMap的校验
    private boolean checkInputMapOfModifyQuestion(Map<String,String> inputMap){
        boolean ret = false;
        String subject = inputMap.get("subject");
        if(subject==null || subject.equals("") ){
            log.info("fail in checkInputMapOfModifyQuestion: content is empty!");
        }else if(!GlobalTools.isNumeric(inputMap.get("grade"))){
            log.info("fail in checkInputMapOfModifyQuestion: param grade error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("multiplexFlag"))){
            log.info("fail in checkInputMapOfModifyQuestion: param multiplexFlag error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("classType"))){
            log.info("fail in checkInputMapOfModifyQuestion: param classType error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("questionType"))){
            log.info("fail in checkInputMapOfModifyQuestion: param questionType error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("classSubType"))){
            log.info("fail in checkInputMapOfModifyQuestion: param classSubType error!");
        }else if(!GlobalTools.isNumeric(inputMap.get("questionId"))){
            log.info("fail in checkInputMapOfModifyQuestion: param questionId error!");
        }else{
            ret = true;
        }
        return ret;
    }

    //返回生成的xml文件的完整路径
    private String saveInXml(int umid,long questionId,Map<String,String> inputMap){
        String ret ="";
        QuestionBean questionBean = new QuestionBean(questionId,inputMap.get("classType"),inputMap.get("classSubType"),inputMap.get("multiplexFlag"),inputMap.get("subQuestionCount"),inputMap.get("subject"));
        List<SubQuestionBean> subQuestionBeanList = getSubQuestionList(inputMap.get("subQuestions"));
        SubQuestion subQuestion = new SubQuestion();
        subQuestion.setSubQuestionBeanList(subQuestionBeanList);
        questionBean.setSubQuestion(subQuestion);
        questionBean.setContentHeader(inputMap.get("contentHeader"));
        questionBean.setAttachmentIds(inputMap.get("attachmentIds"));
        try {
            ret = questionOmxService.saveQuestionBean(umid,questionBean);
        } catch (IOException e) {
            log.info("save file error,umid is:"+umid+" and questionId is:"+questionId);
            e.printStackTrace();
        }
        return ret;
    }

    private List<SubQuestionBean> getSubQuestionList(String subQuestionString){
        List<SubQuestionBean> retList = new ArrayList<SubQuestionBean>();
        String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
        for(String str:a){
            //一个map就是一个子题
            Map<String, String> map = GlobalTools.parseInput(str,"\\<\\[CDATA2\\]\\>");
            SubQuestionBean subQuestionBean = new SubQuestionBean(map.get("seqId"),map.get("qType"),map.get("content"));
            subQuestionBean.setAttachedInfo(map.get("attachedInfo"));
            subQuestionBean.setAttachmentIds(map.get("attachmentIds"));
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

    //用于新题目创建以及老题目修改
    private void saveAnswerAndNote(int umid,long questionId,String subQuestionString){
        String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
        //修改的情况下，先删除原记录
        if(!answerAndNoteRepository.findByQuestionIdAndUmid(questionId,umid).isEmpty()){
            answerAndNoteRepository.deleteByQuestionIdAndUmid(questionId,umid);
        }

        for(String str:a){
            //一个map就是一个子题
            Map<String, String> map = GlobalTools.parseInput(str,"\\<\\[CDATA2\\]\\>");
            int sequenceId = Integer.parseInt(map.get("seqId"));
            String correctAnswer_s = map.get("correctAnswer");
            String wrongAnswer_s = map.get("wrongAnswer");
            String note = map.get("note");

            if((correctAnswer_s!=null && !correctAnswer_s.equals("")) || (wrongAnswer_s!=null && !wrongAnswer_s.equals("")) || (note!=null && !note.equals(""))){
                AnswerAndNote answerAndNote = new AnswerAndNote(umid,questionId,sequenceId);
                if(correctAnswer_s!=null && !correctAnswer_s.equals("")){
                    answerAndNote.setCorrectAnswer(correctAnswer_s);
                }
                if(wrongAnswer_s!=null && !wrongAnswer_s.equals("")){
                    answerAndNote.setWrongAnswer(wrongAnswer_s);
                }
                if(note!=null && !note.equals("")){
                    answerAndNote.setNote(note);
                }
                answerAndNoteRepository.save(answerAndNote);
            }
        }
    }

    private boolean modifyQuestionContent(int umid,long questionContentId,String subject,String contentHeader,String subQuestionString){
        boolean ret = false;
        QuestionContent questionContent = questionContentRepository.findOne(questionContentId);
        String searchContent=subject;
        String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
        String searchNote = "||"; //用“||“ 分割搜索字段的内容和心得，便于之后分别修改

        if(!contentHeader.equals("")){
            searchContent = searchContent +"|"+ contentHeader;
        }

        for(String str:a){
            //一个map就是一个子题
            Map<String, String> map = GlobalTools.parseInput(str,"\\<\\[CDATA2\\]\\>");
            String note = map.get("note");
            searchContent = searchContent + "|" + map.get("content");

            if(note!=null && !note.equals("")){
                if(searchNote.equals("||")) {
                    searchNote = searchNote + note;
                }else{
                    searchNote = searchNote +"|"+ note;
                }
            }
        }
        questionContent.setContent(searchContent+searchNote);
        if(questionContentRepository.save(questionContent)!=null){
            ret = true;
        }

        return ret;
    }

    //attachment表里设置questionId，并处理无效的文件和记录
    private boolean modifyAttachment(int umid,long questionId,String ids,String subQuestionString){
        boolean ret = false;
        //所有附件id的合集，形如：1:3:5:7；传入参数ids是复合题目的题头的附件，各子题可能还有附件
        String attachmentIds="";
        if(ids!=null && !ids.equals("")){
            attachmentIds=ids;
        }
        String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
        for(String str:a){
            //一个map就是一个子题
            Map<String, String> map = GlobalTools.parseInput(str,"\\<\\[CDATA2\\]\\>");
            String ids1 = map.get("attachmentIds");
            if(ids1!=null && !ids1.equals("")){
                attachmentIds = attachmentIds+":"+ids1;
            }
        }
        String[] b = attachmentIds.split(":");
        //将符合条件的attachment记录的questionId字段都设为－1，便于后续处理
        attachmentRepository.resetQuestionIdByQuestionId(questionId,umid);
        for(String str1:b){
            long attatchmentId = GlobalTools.convertStringToLong(str1);
            if(attatchmentId!=-10000){
                attachmentRepository.setQuestionIdById(questionId,umid,attatchmentId);
            }
        }
        //获得questionId为－1的所有记录
        List<Attachment> attachmentList = attachmentRepository.findByUmidAndQuestionId(umid,-1);
        for(Attachment attachment:attachmentList){
            String filePath = attachment.getFilePath();
            File file = new File(filePath);
            if(file.exists()){
                file.delete();
            }
            attachmentRepository.delete(attachment.getId());
        }
        return ret;
    }

    public List<AnswerAndNote> getAnswerAndNoteList(int umid,long questionId){
        List<AnswerAndNote> answerAndNoteList = null;
        answerAndNoteList = answerAndNoteRepository.findByQuestionIdAndUmid(questionId,umid);
        return answerAndNoteList;
    }

}