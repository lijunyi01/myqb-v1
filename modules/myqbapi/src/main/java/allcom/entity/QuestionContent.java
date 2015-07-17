package allcom.entity;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_questioncontent",indexes = {@Index(name = "i_1",columnList = "umid",unique = false),@Index(name = "i_2",columnList = "content",unique = false)})
public class QuestionContent implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    @Column(length = 65532)
    private String content;     //题目的文本内容，供检索用

    protected QuestionContent() {
    }

    public QuestionContent(int umid) {
        this.umid = umid;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){ return this.umid; }
    public void setUmid(int umid) { this.umid = umid; }

    public String getContent() {return this.content;}
    public void setContent(String content) {this.content = content;}

}
