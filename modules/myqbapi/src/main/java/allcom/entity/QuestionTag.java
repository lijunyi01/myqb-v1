package allcom.entity;

import javax.persistence.*;

//题目与标签对应关系表
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_questiontag",indexes = {@Index(name = "i_1",columnList = "questionId,tagId",unique = true)})
public class QuestionTag implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private long questionId;
    private long tagId;


    public QuestionTag(int umid, long questionId,long tagId) {
        this.umid = umid;
        this.questionId = questionId;
        this.tagId = tagId;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){return this.umid;}
    public void setUmid(int umid){this.umid = umid;}

    public long getQuestionId(){return this.questionId;}
    public void setQuestionId(long questionId){this.questionId = questionId;}

    public long getTagId(){return this.tagId;}
    public void setTagId(long tagId){this.tagId = tagId;}

}
