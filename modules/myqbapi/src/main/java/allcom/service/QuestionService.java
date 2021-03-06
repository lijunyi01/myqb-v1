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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

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
public class QuestionService {
    private static Logger log = LoggerFactory.getLogger(QuestionService.class);

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
    @Autowired
    private QuestionTagRepository questionTagRepository;
    @Autowired
    private TagRepository tagRepository;
    @Autowired
    private NoteBookRepository noteBookRepository;

    //@Transactional
    //保存一个题目(一部分内容存入数据库，一部分存入xml文件)
    //public boolean createQuestion(int umid,int grade,int multiplexFlag,int questionType,int classType,int classSubType,String content){
    public long createQuestion(int umid,Map<String,String> inputMap){
        long ret = -1;
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
                    contentPath = saveInXml(umid,question1.getId(),inputMap,false);
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
                        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
                        question1.setCreateTime(currentTime);
                        if (questionRepository.save(question1) != null) {
                            //保存正确答案，心得备注等信息至数据库
                            saveAnswerAndNote(umid,question1.getId(),inputMap.get("subQuestions"));
                            //保存检索内容至myqb_qestioncontent
                            modifyQuestionContent(umid,questionContentId,inputMap.get("subject"),inputMap.get("contentHeader"),inputMap.get("subQuestions"));
                            //处理附件以及附件表
                            modifyAttachment(umid,question1.getId(),inputMap.get("attachmentIds"),inputMap.get("subQuestions"));
                            ret = question1.getId();
                        }
                    }
                }
            }
        }
        return ret;
    }

    //删除题目
    public boolean deleteQuestion(int umid,String questionIdS){
        boolean ret = false;
        boolean fileDeleted = false;
        long questionId=GlobalTools.convertStringToLong(questionIdS);
        if(questionId!=-10000) {
            Question question = questionRepository.findOne(questionId);
            if (question != null && question.getUmid() == umid && question.getStatus() ==1) {

                //删附件
                List<Attachment> attachmentList = attachmentRepository.findByUmidAndQuestionId(umid,questionId);
                if(attachmentList.isEmpty()){
                    fileDeleted = true;
                }else {
                    for (Attachment attachment : attachmentList) {
                        String filePath1 = attachment.getFilePath();
                        if (filePath1 != null) {
                            File attachmentFile = new File(filePath1);
                            if (attachmentFile.exists()) {
                                if (!attachmentFile.delete()) {
                                    fileDeleted = false;
                                    break;
                                } else {
                                    fileDeleted = true;
                                }
                            }
                        }
                    }
                }

                //在附件都删成功的情况下删xml文件
                if(fileDeleted) {
                    fileDeleted = false;
                    String filePath = question.getContentPath();
                    if (filePath != null) {
                        File cgFile = new File(filePath);
                        if (cgFile.exists()) {
                            if (cgFile.delete()) {
                                fileDeleted = true;
                            }
                        }
                    }
                }

                //在删除文件成功的前提下，删myqb_answerandnote表、myqb_attachment表、myqb_questioncontent表和myqb_question表里的相关内容
                if(fileDeleted){
                    long questionContentId = question.getQuestionContentId();
                    deleteQuestionTable(questionId, umid,questionContentId);
                    if(!questionRepository.exists(questionId)){
                        ret = true;
                    }
                }
            }

        }
        return ret;
    }

    @Transactional
    private void deleteQuestionTable(long questionId,int umid,long questionContentId){
        answerAndNoteRepository.deleteByQuestionIdAndUmid(questionId,umid);
        questionContentRepository.delete(questionContentId);
        attachmentRepository.deleteByUmidAndQuestionId(questionId,umid);
        questionRepository.delete(questionId);
    }

    //预删除题目
    public boolean deleteQuestion2(int umid,String questionIdS){
        boolean ret = false;
        boolean fileDeleted = false;
        long questionId=GlobalTools.convertStringToLong(questionIdS);
        if(questionId!=-10000) {
            Question question = questionRepository.findOne(questionId);
            if (question != null && question.getUmid() == umid && question.getStatus() ==0) {
                question.setStatus(1);
                if(questionRepository.save(question)!=null){
                    ret = true;
                }
            }
        }
        return ret;
    }

    //完整地修改题目
    public boolean modifyQuestion(int umid,Map<String,String> inputMap){
        boolean ret = false;

        if(checkInputMapOfModifyQuestion(inputMap)==false){
            log.info("param check failed in checkInpuMap! inputMap is:"+ inputMap);
        }else{
            long questionId=GlobalTools.convertStringToLong(inputMap.get("questionId"));
            if(questionId!=-10000) {
                Question question = questionRepository.findOne(questionId);
                if (question != null && question.getUmid() == umid) {
                    //先修改xml文件，文件名不会变，以questionId命名
                    String contentPath = "";
                    contentPath = saveInXml(umid, questionId, inputMap,false);
                    if (contentPath != null && !contentPath.equals("")) {
                        //该xml成功再处理表
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
                            modifyQuestionContent(umid, question.getQuestionContentId(), inputMap.get("subject"), inputMap.get("contentHeader"), inputMap.get("subQuestions"));
                            //处理附件以及附件表
                            modifyAttachment(umid, questionId, inputMap.get("attachmentIds"), inputMap.get("subQuestions"));
                            ret = true;
                        }
                    }
                }
            }
        }
        return ret;
    }

    //修改题目中的答案和心得
    public boolean modifyAnswerAndNote(int umid,Map<String,String> inputMap){
        boolean ret = false;
        long questionId=GlobalTools.convertStringToLong(inputMap.get("questionId"));
        if(questionId!=-10000) {
            Question question = questionRepository.findOne(questionId);
            if (question != null && question.getUmid() == umid) {
                //保存正确答案，心得备注等信息至数据库
                saveAnswerAndNote(umid, questionId, inputMap.get("subQuestions"));
                //在myqb_qestioncontent修改检索内容
                modifyQuestionContent2(umid, question.getQuestionContentId(), inputMap.get("subject"), inputMap.get("contentHeader"), inputMap.get("subQuestions"));
                ret = true;
            }
        }

        return ret;
    }

    public RetMessage getIdsByType(int umid,Map<String,String> inputMap,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<Question> questionList = questionRepository.findByUmidAndStatus(umid, 0);
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
                Question question = questionRepository.findByUmidAndStatusAndQuestionContentId(umid, 0, questionContent.getId());
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

    //用于新题目创建以及老题目修改,要求传入所有子题的答案及心得，不能只传入部分
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

    //传入的部分包含题目的内容
    private boolean modifyQuestionContent(int umid,long questionContentId,String subject,String contentHeader,String subQuestionString){
        boolean ret = false;
        QuestionContent questionContent = questionContentRepository.findOne(questionContentId);
        if(questionContent != null) {
            String searchContent = subject;
            String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
            String searchNote = "||"; //用“||“ 分割搜索字段的内容和心得，便于之后分别修改

            if (!contentHeader.equals("")) {
                searchContent = searchContent + "|" + contentHeader;
            }

            for (String str : a) {
                //一个map就是一个子题
                Map<String, String> map = GlobalTools.parseInput(str, "\\<\\[CDATA2\\]\\>");
                String note = map.get("note");
                searchContent = searchContent + "|" + map.get("content");

                if (note != null && !note.equals("")) {
                    if (searchNote.equals("||")) {
                        searchNote = searchNote + note;
                    } else {
                        searchNote = searchNote + "|" + note;
                    }
                }
            }
            questionContent.setContent(searchContent + searchNote);
            if (questionContentRepository.save(questionContent) != null) {
                ret = true;
            }
        }

        return ret;
    }

    //传入的部分只包含题目的答案及心得
    private boolean modifyQuestionContent2(int umid,long questionContentId,String subject,String contentHeader,String subQuestionString){
        boolean ret = false;
        QuestionContent questionContent = questionContentRepository.findOne(questionContentId);
        if(questionContent !=null) {
            String searchContent = questionContent.getContent();
            // searchContent1是||分割的两部分中的第一部分，不含心得
            String searchContent1 = searchContent.split("\\|\\|")[0];
            String[] a = subQuestionString.split("\\<\\[CDATA1\\]\\>");
            String searchNote = "||"; //用“||“ 分割搜索字段的内容和心得

            for (String str : a) {
                //一个map就是一个子题的答案和心得
                Map<String, String> map = GlobalTools.parseInput(str, "\\<\\[CDATA2\\]\\>");
                String note = map.get("note");
                if (note != null && !note.equals("")) {
                    if (searchNote.equals("||")) {
                        searchNote = searchNote + note;
                    } else {
                        searchNote = searchNote + "|" + note;
                    }
                }
            }
            questionContent.setContent(searchContent1 + searchNote);
            if (questionContentRepository.save(questionContent) != null) {
                ret = true;
            }
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
        List<AnswerAndNote> answerAndNoteList = answerAndNoteRepository.findByQuestionIdAndUmid(questionId,umid);
        return answerAndNoteList;
    }

    public List<QuestionTag> getQuestionTagList(long questionId){
        List<QuestionTag> questionTagList = questionTagRepository.findByQuestionId(questionId);
        return questionTagList;
    }

    public List<Tag> getTagListByQuestionTagList(List<QuestionTag> questionTagList){
        List<Tag> tagList = new ArrayList<Tag>();
        for(QuestionTag questionTag:questionTagList){
            Tag tag = tagRepository.findOne(questionTag.getTagId());
            if(tag !=null){
                tagList.add(tag);
            }
        }
        return tagList;
    }

    public RetQuestionSummary getQuestionSummary(int umid,int pageNumber,int pageSize,String area){
        RetQuestionSummary retQuestionSummary = new RetQuestionSummary("-1");
        //创建分页请求
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageNumber-1,pageSize,sort);
        //分页查询
        Page<Question> questionPage = questionRepository.findByUmidAndStatus(umid, 0, pageRequest);
        if(questionPage!= null) {
            List<QuestionSummaryBean> questionSummaryBeanList = new ArrayList<QuestionSummaryBean>();
            for (Question question : questionPage) {
                QuestionSummaryBean questionSummaryBean = new QuestionSummaryBean();
                questionSummaryBean.setId(question.getId());
                questionSummaryBean.setSubject(question.getSubject());
                questionSummaryBean.setCreateTime(question.getCreateTime());
                questionSummaryBeanList.add(questionSummaryBean);
            }
            retQuestionSummary.setErrorCode("0");
            retQuestionSummary.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            retQuestionSummary.setQuestionSummaryBeanList(questionSummaryBeanList);
            retQuestionSummary.setCurrentCounts(questionPage.getNumberOfElements());
            retQuestionSummary.setPageNumber(pageNumber);
            retQuestionSummary.setPageNumberSummary(questionPage.getTotalPages());
            retQuestionSummary.setSummary(questionPage.getTotalElements());
        }else{
            //没有查到内容
            retQuestionSummary.setErrorCode("-24");
            retQuestionSummary.setErrorMessage(GlobalTools.getMessageByLocale(area,"-24"));
        }
        return retQuestionSummary;
    }

    public RetQuestionSummary getTrashSummary(int umid,int pageNumber,int pageSize,String area){
        RetQuestionSummary retQuestionSummary = new RetQuestionSummary("-1");
        //创建分页请求
        Sort sort = new Sort(Sort.Direction.DESC, "id");
        PageRequest pageRequest = new PageRequest(pageNumber-1,pageSize,sort);
        //分页查询
        Page<Question> questionPage = questionRepository.findByUmidAndStatus(umid,1,pageRequest);
        if(questionPage!= null) {
            List<QuestionSummaryBean> questionSummaryBeanList = new ArrayList<QuestionSummaryBean>();
            for (Question question : questionPage) {
                QuestionSummaryBean questionSummaryBean = new QuestionSummaryBean();
                questionSummaryBean.setId(question.getId());
                questionSummaryBean.setSubject(question.getSubject());
                questionSummaryBean.setCreateTime(question.getCreateTime());
                questionSummaryBeanList.add(questionSummaryBean);
            }
            retQuestionSummary.setErrorCode("0");
            retQuestionSummary.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            retQuestionSummary.setQuestionSummaryBeanList(questionSummaryBeanList);
            retQuestionSummary.setCurrentCounts(questionPage.getNumberOfElements());
            retQuestionSummary.setPageNumber(pageNumber);
            retQuestionSummary.setPageNumberSummary(questionPage.getTotalPages());
            retQuestionSummary.setSummary(questionPage.getTotalElements());
        }else{
            //没有查到内容
            retQuestionSummary.setErrorCode("-24");
            retQuestionSummary.setErrorMessage(GlobalTools.getMessageByLocale(area,"-24"));
        }
        return retQuestionSummary;
    }


    //获取废件箱题目数量
    public RetMessage getTrashNumber(int umid,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        List<Question> questionList = questionRepository.findByUmidAndStatus(umid,1);
        if(questionList != null) {
            retContent += "summary:" + questionList.size();
            ret.setErrorCode("0");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
            ret.setRetContent(retContent);
        }else{
            //数据库查询异常了
            ret.setErrorCode("-30");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
            log.info("questionRepository.findByUmidAndStatus(umid,1):failed!!!");
        }
        return ret;
    }

    //按订正本获取题目数量(bookId为空时取所有题目数量（不含废件）)
    public RetMessage getQuestionNumber(int umid,String bookId,String area){
        RetMessage ret = new RetMessage();
        String retContent="";
        long bId = GlobalTools.convertStringToLong(bookId);
        if(!isBookIdValid(umid,bId,true)){
            ret.setErrorCode("-29");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"-29"));
        }else {
            List<Question> questionList = null;
            if(bId == -10000){
                questionList = questionRepository.findByUmidAndStatus(umid,0);
            }else{
                questionList = questionRepository.findByUmidAndStatusAndNotebookId(umid,0,bId);
            }
            if (questionList != null) {
                retContent += "summary:" + questionList.size();
                ret.setErrorCode("0");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "0"));
                ret.setRetContent(retContent);
            } else {
                //数据库查询异常了
                ret.setErrorCode("-30");
                ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-30"));
                log.info("questionRepository.findByUmidAndStatusAndNotebookId(umid,0,bId):failed!!!");
            }
        }
        return ret;
    }

    //判断订正本Id是否合法（主要判断客户端提供的bookId是否是在订正本表里存在的，且与该用户相关）
    private boolean isBookIdValid(int umid,long bookId,boolean isNullValid){
        boolean ret = false;
        if(bookId == -10000){
            if(isNullValid) {
                ret = true;
            }
        }else{
            NoteBook noteBook = noteBookRepository.findOne(bookId);
            if(noteBook !=null && noteBook.getUmid() == umid){
                ret = true;
            }
        }
        return ret;
    }

}