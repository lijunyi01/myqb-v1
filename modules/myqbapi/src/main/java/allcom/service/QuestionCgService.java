package allcom.service;

import allcom.bean.QuestionSummaryBean;
import allcom.controller.RetMessage;
import allcom.controller.RetQuestionSummary;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class QuestionCgService {
    private static Logger log = LoggerFactory.getLogger(QuestionCgService.class);

    @Autowired
    private QuestionCgRepository questionCgRepository;
    @Autowired
    private AnswerAndNoteCgRepository answerAndNoteCgRepository;
    @Autowired
    private QuestionOmxService questionOmxService;


    //保存及修改题目草稿
    public long createQuestionCg(int umid,Map<String,String> inputMap){
        long ret = -1;

        if(checkInputMapOfCreateQuestion(inputMap)==false){
            log.info("param check failed in checkInpuMap2! inputMap is:"+ inputMap);
        }else{
            long questionCgId=GlobalTools.convertStringToLong(inputMap.get("questionId"));
            // -10000表示传入的questionId是空，是新增草稿，否则是修改草稿
            if(questionCgId==-10000) {
                QuestionCg questionCg = new QuestionCg(umid);
                QuestionCg questionCg1 = questionCgRepository.save(questionCg);
                if(questionCg1 != null){
                    //根据传入的信息生成xml文件并保存到指定的路径
                    String cgXmlPath = "";
                    cgXmlPath = saveInXml(umid,questionCg1.getId(),inputMap,true);
                    //将题目相关具体信息保存到myqb_question表
                    if (cgXmlPath != null && !cgXmlPath.equals("")) {
                        int grade = Integer.parseInt(inputMap.get("grade"));
                        int multiplexFlag = Integer.parseInt(inputMap.get("multiplexFlag"));
                        int questionType = Integer.parseInt(inputMap.get("questionType"));
                        int classType = Integer.parseInt(inputMap.get("classType"));
                        int classSubType = Integer.parseInt(inputMap.get("classSubType"));
                        questionCg.setGrade(grade);
                        questionCg.setMultiplexFlag(multiplexFlag);
                        questionCg.setQuestionType(questionType);
                        questionCg.setClassType(classType);
                        questionCg.setClassSubType(classSubType);
                        questionCg.setContentPath(cgXmlPath);
                        questionCg.setSubject(inputMap.get("subject"));
                        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                        questionCg.setCreateTime(currentTime);
                        if (questionCgRepository.save(questionCg) != null) {
                            //保存正确答案，心得备注等信息至数据库
                            saveAnswerAndNoteCg(umid, questionCg.getId(), inputMap.get("subQuestions"));
                            //草稿暂时不处理附件
                            ret = questionCg.getId();
                        }
                    }
                }
            }else{
                QuestionCg questionCg = questionCgRepository.findOne(questionCgId);
                if (questionCg != null && questionCg.getUmid() == umid) {
                    String cgXmlPath = "";
                    cgXmlPath = saveInXml(umid,questionCgId,inputMap,true);
                    //将题目相关具体信息保存到myqb_question表
                    if (cgXmlPath != null && !cgXmlPath.equals("")) {
                        int grade = Integer.parseInt(inputMap.get("grade"));
                        int multiplexFlag = Integer.parseInt(inputMap.get("multiplexFlag"));
                        int questionType = Integer.parseInt(inputMap.get("questionType"));
                        int classType = Integer.parseInt(inputMap.get("classType"));
                        int classSubType = Integer.parseInt(inputMap.get("classSubType"));
                        questionCg.setGrade(grade);
                        questionCg.setMultiplexFlag(multiplexFlag);
                        questionCg.setQuestionType(questionType);
                        questionCg.setClassType(classType);
                        questionCg.setClassSubType(classSubType);
                        questionCg.setContentPath(cgXmlPath);
                        questionCg.setSubject(inputMap.get("subject"));
                        if (questionCgRepository.save(questionCg) != null) {
                            //保存正确答案，心得备注等信息至数据库
                            saveAnswerAndNoteCg(umid, questionCg.getId(), inputMap.get("subQuestions"));
                            //草稿暂时不处理附件
                            ret = questionCg.getId();
                        }
                    }

                }

            }


        }
        return ret;
    }

    //删除题目草稿
    public boolean deleteQuestionCg(int umid,Map<String,String> inputMap){
        boolean ret = false;
        boolean fileDeleted = false;
        long questionCgId=GlobalTools.convertStringToLong(inputMap.get("questionId"));
        if(questionCgId!=-10000) {
            QuestionCg questionCg = questionCgRepository.findOne(questionCgId);
            if (questionCg != null && questionCg.getUmid() == umid) {
                //删xml文件
                String filePath = questionCg.getContentPath();
                if(filePath !=null){
                    File cgFile = new File(filePath);
                    if(cgFile.exists()){
                        if(cgFile.delete()){
                           fileDeleted = true;
                        }
                    }
                }
                //在删除文件成功的前提下，删myqb_answerandnotecg表和myqb_questioncg表里的相关内容
                if(fileDeleted){
                    deleteCgTable(questionCgId,umid);
                    if(!questionCgRepository.exists(questionCgId)){
                        ret = true;
                    }
                }
            }
        }
        return ret;
    }

    @Transactional
    private void deleteCgTable(long questionCgId,int umid){
        answerAndNoteCgRepository.deleteByQuestionIdAndUmid(questionCgId,umid);
        questionCgRepository.delete(questionCgId);
    }

    public RetMessage getCgIds(int umid,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<QuestionCg> questionCgList = questionCgRepository.findByUmid(umid);
        if(!questionCgList.isEmpty()){
            for(QuestionCg questionCg:questionCgList){
                if(retContent.equals("")){
                    retContent = questionCg.getId()+"";
                }else{
                    retContent = retContent + ":" +questionCg.getId();
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

    //返回生成的xml文件的完整路径,cgFlag 区分是草稿还是正式的，以区别存储路径
    private String saveInXml(int umid,long questionId,Map<String,String> inputMap,boolean cgFlag){
        String ret ="";
        QuestionBean questionBean = new QuestionBean(questionId,inputMap.get("classType"),inputMap.get("classSubType"),inputMap.get("multiplexFlag"),inputMap.get("subQuestionCount"),inputMap.get("subject"),inputMap.get("grade"));
        List<SubQuestionBean> subQuestionBeanList = getSubQuestionList(inputMap.get("subQuestions"));
        SubQuestion subQuestion = new SubQuestion();
        subQuestion.setSubQuestionBeanList(subQuestionBeanList);
        questionBean.setSubQuestion(subQuestion);
        questionBean.setContentHeader(inputMap.get("contentHeader"));
        questionBean.setAttachmentIds(inputMap.get("attachmentIds"));
        try {
            ret = questionOmxService.saveQuestionBean(umid,questionBean,cgFlag);
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


    public QuestionCg getQuestionCgById(long id){
        QuestionCg questionCg = questionCgRepository.findOne(id);
        return questionCg;
    }

    //用于草稿中的答案和心得
    private void saveAnswerAndNoteCg(int umid,long questionId,String subQuestionString){
        String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
        //修改的情况下，先删除原记录
        if(!answerAndNoteCgRepository.findByQuestionIdAndUmid(questionId,umid).isEmpty()){
            answerAndNoteCgRepository.deleteByQuestionIdAndUmid(questionId,umid);
        }

        for(String str:a){
            //一个map就是一个子题
            Map<String, String> map = GlobalTools.parseInput(str,"\\<\\[CDATA2\\]\\>");
            int sequenceId = Integer.parseInt(map.get("seqId"));
            String correctAnswer_s = map.get("correctAnswer");
            String wrongAnswer_s = map.get("wrongAnswer");
            String note = map.get("note");

            if((correctAnswer_s!=null && !correctAnswer_s.equals("")) || (wrongAnswer_s!=null && !wrongAnswer_s.equals("")) || (note!=null && !note.equals(""))){
                AnswerAndNoteCg answerAndNoteCg = new AnswerAndNoteCg(umid,questionId,sequenceId);
                if(correctAnswer_s!=null && !correctAnswer_s.equals("")){
                    answerAndNoteCg.setCorrectAnswer(correctAnswer_s);
                }
                if(wrongAnswer_s!=null && !wrongAnswer_s.equals("")){
                    answerAndNoteCg.setWrongAnswer(wrongAnswer_s);
                }
                if(note!=null && !note.equals("")){
                    answerAndNoteCg.setNote(note);
                }
                answerAndNoteCgRepository.save(answerAndNoteCg);
            }
        }
    }

    public List<AnswerAndNoteCg> getAnswerAndNoteCgList(int umid,long questionId){
        List<AnswerAndNoteCg> answerAndNoteCgList = null;
        answerAndNoteCgList = answerAndNoteCgRepository.findByQuestionIdAndUmid(questionId,umid);
        return answerAndNoteCgList;
    }

    public RetQuestionSummary getCgSummary(int umid,int pageNumber,int pageSize,String area){
        RetQuestionSummary retQuestionSummary = new RetQuestionSummary("-1");
        //创建分页请求
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageNumber-1,pageSize,sort);
        //分页查询
        Page<QuestionCg> questionCgPage = questionCgRepository.findByUmid(umid,pageRequest);
        if(questionCgPage!= null) {
            List<QuestionSummaryBean> questionSummaryBeanList = new ArrayList<QuestionSummaryBean>();
            for (QuestionCg questionCg : questionCgPage) {
                QuestionSummaryBean questionSummaryBean = new QuestionSummaryBean();
                questionSummaryBean.setId(questionCg.getId());
                questionSummaryBean.setSubject(questionCg.getSubject());
                questionSummaryBean.setCreateTime(questionCg.getCreateTime());
                questionSummaryBeanList.add(questionSummaryBean);
            }
            retQuestionSummary.setErrorCode("0");
            retQuestionSummary.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            retQuestionSummary.setQuestionSummaryBeanList(questionSummaryBeanList);
            retQuestionSummary.setCurrentCounts(questionCgPage.getNumberOfElements());
            retQuestionSummary.setPageNumber(pageNumber);
            retQuestionSummary.setPageNumberSummary(questionCgPage.getTotalPages());
            retQuestionSummary.setSummary(questionCgPage.getTotalElements());
        }else{
            //没有查到内容
            retQuestionSummary.setErrorCode("-24");
            retQuestionSummary.setErrorMessage(GlobalTools.getMessageByLocale(area,"-24"));
        }
        return retQuestionSummary;
    }

}