package allcom.oxmapper;

import allcom.entity.Question;

/**
 * Created by ljy on 15/7/17.
 * ok
 */
public class QuestionBean {
    private long questionId;
    private String classType;
    private String classSubType;
    private String content;

    public QuestionBean(){}

    public QuestionBean(long questionId,String classType,String classSubType,String content){
        this.questionId = questionId;
        this.classType = classType;
        this.classSubType = classSubType;
        this.content = content;
    }

    public long getQuestionId(){ return this.questionId;}
    public void setQuestionId(long questionId) {this.questionId = questionId;}

    public String getClassType(){return this.classType;}
    public void setClassType(String classType) {this.classType = classType;}

    public String getClassSubType(){return this.classSubType;}
    public void setClassSubType(String classSubType){this.classSubType = classSubType;}

    public String getContent(){return this.content;}
    public void setContent(String content){this.content = content;}

}
