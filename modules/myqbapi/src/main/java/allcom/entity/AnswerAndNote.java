package allcom.entity;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_answerandnote",indexes = {@Index(name = "i_1",columnList = "umid",unique = false),@Index(name = "i_2",columnList = "questionId,subQuestionId",unique = true)})
public class AnswerAndNote implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private long questionId;    //题目id
    private int subQuestionId;  //子题序号
    @Column(length = 1024)
    private String correctAnswer;  //正确答案
    @Column(length = 1024)
    private String wrongAnswer;  //错误答案
    @Column(length = 2048)
    private String note;         //备注及心得

    protected AnswerAndNote() {
    }

    public AnswerAndNote(int umid, long questionId, int subQuestionId) {
        this.umid = umid;
        this.questionId = questionId;
        this.subQuestionId = subQuestionId;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){ return this.umid; }
    public void setUmid(int umid) { this.umid = umid; }

    public long getQuestionId(){return this.questionId;}
    public void setQuestionId(long questionId) {this.questionId = questionId;}

    public int getSubQuestionId(){return this.subQuestionId;}
    public void setSubQuestionId(int subQuestionId) {this.subQuestionId = subQuestionId;}

    public String getWrongAnswer(){return this.wrongAnswer;}
    public void setWrongAnswer(String wrongAnswer){this.wrongAnswer = wrongAnswer;}

    public String getNote(){return this.note;}
    public void setNote(String note){this.note = note;}

    public String getCorrectAnswer(){return this.correctAnswer;}
    public void setCorrectAnswer(String correctAnswer) {this.correctAnswer = correctAnswer;}

}
