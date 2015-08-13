package allcom.controller;

import allcom.entity.AnswerAndNoteCg;
import allcom.oxmapper.QuestionBean;

import java.util.List;

/**
 * Created by ljy on 15/7/17.
 * 包含allcom.oxmapper.QuestionBean和List<AnswerAndNoteCg>,增加了错误码等字段用于返回信息
 */
public class RetQuestionCgBean {
    private String errorCode;
    private String errorMessage;
    private QuestionBean questionBean;
    //List对应json输出中的的数组
    private List<AnswerAndNoteCg> answerAndNoteCgList;

    public RetQuestionCgBean(){}

    public RetQuestionCgBean(String errorCode){
        this.errorCode = errorCode;
        this.errorMessage = "";
    }

    public String getErrorCode(){return this.errorCode;}
    public void setErrorCode(String errorCode) {this.errorCode = errorCode;}

    public String getErrorMessage(){return this.errorMessage;}
    public void setErrorMessage(String errorMessage){this.errorMessage = errorMessage;}

    public QuestionBean getQuestionBean(){return this.questionBean;}
    public void setQuestionBean(QuestionBean questionBean){this.questionBean = questionBean;}

    public List<AnswerAndNoteCg> getAnswerAndNoteCgList(){return this.answerAndNoteCgList;}
    public void setAnswerAndNoteCgList(List<AnswerAndNoteCg> answerAndNoteCgList){this.answerAndNoteCgList = answerAndNoteCgList;}

}
