package allcom.oxmapper;

/**
 * Created by ljy on 15/7/17.
 * ok
 */
public class SubQuestionBean {
    //子题序号
    private String  sequenceId;
    //题型 1:选择题；2:填空题； 3:判断题；4:问答题；100:未归类的复合题目；101:阅读理解
    private String questionType;
    private String content;
    //用于存储题目的附加信息,如选择题的选项
    private String attachedInfo;
    //附件的ID，对应于附件表，形如：1:3:9
    private String attachmentIds;


    public SubQuestionBean(){}

    public SubQuestionBean(String sequenceId, String questionType, String content){
        this.sequenceId = sequenceId;
        this.questionType = questionType;
        this.content = content;
    }

    public String getSequenceId(){ return this.sequenceId;}
    public void setSequenceId(String sequenceId) {this.sequenceId = sequenceId;}

    public String getQuestionType(){return questionType;}
    public void setQuestionType(String questionType){this.questionType = questionType;}

    public String getContent(){return this.content;}
    public void setContent(String content){this.content = content;}

    public String getAttachedInfo(){return this.attachedInfo;}
    public void setAttachedInfo(String attachedInfo) {this.attachedInfo = attachedInfo;}

    public String getAttachmentIds(){return this.attachmentIds;}
    public void setAttachmentIds(String attachmentIds) {this.attachmentIds = attachmentIds;}

//    public String getCorrectAnswer(){return this.correctAnswer;}
//    public void setCorrectAnswer(String correctAnswer){this.correctAnswer = correctAnswer;}
//
//    public String getWrongAnswer(){return this.wrongAnswer;}
//    public void setWrongAnswer(String wrongAnswer){this.wrongAnswer = wrongAnswer;}
//
//    public String getNote(){return this.note;}
//    public void setNote(String note){this.note = note;}

}
