package allcom.oxmapper;

/**
 * Created by ljy on 15/7/17.
 * ok
 */
public class SubQuestionBean {
    private String  sequenceId;
    private String questionType;
    private String content;

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



}
