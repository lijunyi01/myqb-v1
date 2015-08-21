package allcom.controller;

import allcom.bean.QuestionSummaryBean;
import allcom.entity.AnswerAndNote;
import allcom.oxmapper.QuestionBean;

import java.util.List;

/**
 * Created by ljy on 15/7/17.
 * 包含allcom.oxmapper.QuestionBean和List<AnswerAndNote>,增加了错误码等字段用于返回信息
 */
public class RetQuestionSummary {
    private String errorCode;
    private String errorMessage;
    //List对应json输出中的的数组
    private List<QuestionSummaryBean> questionSummaryBeanList;

    public RetQuestionSummary(){}

    public RetQuestionSummary(String errorCode){
        this.errorCode = errorCode;
        this.errorMessage = "";
    }

    public String getErrorCode(){return this.errorCode;}
    public void setErrorCode(String errorCode) {this.errorCode = errorCode;}

    public String getErrorMessage(){return this.errorMessage;}
    public void setErrorMessage(String errorMessage){this.errorMessage = errorMessage;}

    public List<QuestionSummaryBean> getQuestionSummaryBeanList(){return questionSummaryBeanList;}
    public void setQuestionSummaryBeanList(List<QuestionSummaryBean> questionSummaryBeanList){this.questionSummaryBeanList = questionSummaryBeanList;}

}
