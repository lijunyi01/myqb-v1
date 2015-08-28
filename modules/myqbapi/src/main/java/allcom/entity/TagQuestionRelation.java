package allcom.entity;

import javax.persistence.*;

//标签和题目对应关系表
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_tagquestion",indexes = {@Index(name = "i_1",columnList = "umid,tagId,questionId",unique = true)})
public class TagQuestionRelation implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private long tagId;        //标签ID
    private long questionId;   //题目ID


    protected TagQuestionRelation(){}

    public TagQuestionRelation(int umid,long tagId,long questionId) {
        this.umid = umid;
        this.tagId = tagId;
        this.questionId = questionId;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){return this.umid;}
    public void setUmid(int umid){this.umid = umid;}

    public long getTagId(){return this.tagId;}
    public void setTagId(long tagId){this.tagId = tagId;}

    public long getQuestionId(){return this.questionId;}
    public void setQuestionId(long questionId){this.questionId = questionId;}

}
