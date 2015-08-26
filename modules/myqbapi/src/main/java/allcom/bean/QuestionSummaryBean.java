package allcom.bean;

import java.sql.Timestamp;

/**
 * Created by ljy on 15/8/21.
 * ok
 */
public class QuestionSummaryBean {
    private long id;    //题目id（草稿或者正式题目）
    private String subject;
    private Timestamp createTime;

    public QuestionSummaryBean(){}

    public long getId(){return this.id;}
    public void setId(long id){this.id = id;}

    public String getSubject(){return this.subject;}
    public void setSubject(String subject){this.subject = subject;}

    public Timestamp getCreateTime(){return this.createTime;}
    public void setCreateTime(Timestamp createTime){this.createTime = createTime;}
}
