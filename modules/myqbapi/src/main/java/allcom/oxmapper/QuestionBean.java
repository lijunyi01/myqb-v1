package allcom.oxmapper;

import allcom.entity.Question;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ljy on 15/7/17.
 * 每个class allcom.oxmapper.QuestionBean实例对应于一个题目xml文件
 * 而每个class allcom.entity.Question实例对应于表myqb_question的一条记录
 */
public class QuestionBean {
    private long questionId;
    private String classType;
    private String classSubType;
    //题头，复合题目的题目内容（例如阅读理解的文章）；对于普通题目（multiplexFlag＝0）contentHeader＝“”
    private String contentHeader;
    //附件的ID，对应于附件表，形如：1:3:9
    private String attachmentIds;
    //multiplexFlag="0" 表示普通题目；＝“1”表示复合题目
    private String multiplexFlag;
    //子题数量，对于普通题目，subQuestionCount＝1；对于复合题目，subQuestionCount》＝1
    private String subQuestionCount;
    private SubQuestion subQuestion;
    private String subject;

    public QuestionBean(){}

    public QuestionBean(long questionId,String classType,String classSubType,String multiplexFlag,String subQuestionCount,String subject){
        this.questionId = questionId;
        this.classType = classType;
        this.classSubType = classSubType;
        this.multiplexFlag = multiplexFlag;
        this.subQuestionCount = subQuestionCount;
        this.contentHeader = "";
        this.attachmentIds = "";
        this.subject = subject;
    }

    public long getQuestionId(){ return this.questionId;}
    public void setQuestionId(long questionId) {this.questionId = questionId;}

    public String getClassType(){return this.classType;}
    public void setClassType(String classType) {this.classType = classType;}

    public String getClassSubType(){return this.classSubType;}
    public void setClassSubType(String classSubType){this.classSubType = classSubType;}

    public String getContentHeader(){return this.contentHeader;}
    public void setContentHeader(String contentHeader){this.contentHeader = contentHeader;}

    public String getAttachmentIds(){return this.attachmentIds;}
    public void setAttachmentIds(String attachmentIds) {this.attachmentIds = attachmentIds;}

    public SubQuestion getSubQuestion(){return this.subQuestion;}
    public void setSubQuestion(SubQuestion subQuestion){this.subQuestion = subQuestion;}

    public String getMultiplexFlag(){return this.multiplexFlag;}
    public void setMultiplexFlag(String multiplexFlag){this.multiplexFlag = multiplexFlag;}

    public String getSubQuestionCount(){return this.subQuestionCount;}
    public void setSubQuestionCount(String subQuestionCount){this.subQuestionCount = subQuestionCount;}

    public String getSubject(){return this.subject;}
    public void setSubject(String subject) {this.subject = subject;}

}
