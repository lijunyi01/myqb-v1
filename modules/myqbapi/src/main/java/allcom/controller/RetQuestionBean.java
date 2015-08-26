package allcom.controller;

import allcom.entity.AnswerAndNote;
import allcom.entity.Tag;
import allcom.oxmapper.QuestionBean;

import java.util.List;

/**
 * Created by ljy on 15/7/17.
 * 包含allcom.oxmapper.QuestionBean和List<AnswerAndNote>,增加了错误码等字段用于返回信息
 */
public class RetQuestionBean {
    private String errorCode;
    private String errorMessage;
    private QuestionBean questionBean;
    //List对应json输出中的的数组
    private List<AnswerAndNote> answerAndNoteList;
    //对应的订正本id
    private long notebookId;
    //对应的标签信息
    private List<Tag> tagList;

    //public RetQuestionBean(){}

    public RetQuestionBean(String errorCode){
        this.errorCode = errorCode;
        this.errorMessage = "";
        notebookId = -1;
    }

    public String getErrorCode(){return this.errorCode;}
    public void setErrorCode(String errorCode) {this.errorCode = errorCode;}

    public String getErrorMessage(){return this.errorMessage;}
    public void setErrorMessage(String errorMessage){this.errorMessage = errorMessage;}

    public QuestionBean getQuestionBean(){return this.questionBean;}
    public void setQuestionBean(QuestionBean questionBean){this.questionBean = questionBean;}

    public List<AnswerAndNote> getAnswerAndNoteList(){return this.answerAndNoteList;}
    public void setAnswerAndNoteList(List<AnswerAndNote> answerAndNoteList){this.answerAndNoteList = answerAndNoteList;}

    public long getNotebookId(){return this.notebookId;}
    public void setNotebookId(long notebookId){this.notebookId = notebookId;}

    public List<Tag> getTagList(){return this.tagList;}
    public void setTagList(List<Tag> tagList) {this.tagList = tagList;}

}
