package allcom.bean;

import allcom.toolkit.GlobalTools;
import com.fasterxml.jackson.annotation.JsonIgnore;

import java.sql.Timestamp;

/**
 * Created by ljy on 15/8/21.
 * ok
 */
public class QuestionSummaryBean {
    private long id;    //题目id（草稿或者正式题目）
    private String subject;
    @JsonIgnore    // Jackson默认是针对get方法来生成JSON字符串的; 用于排除作jason转化
    private Timestamp createTime;

    public QuestionSummaryBean(){}

    public long getId(){return this.id;}
    public void setId(long id){this.id = id;}

    public String getSubject(){return this.subject;}
    public void setSubject(String subject){this.subject = subject;}

    public Timestamp getCreateTime(){return this.createTime;}
    public void setCreateTime(Timestamp createTime){this.createTime = createTime;}

    //@controller里返回该类对象时，因为有该方法，返回的json里会有一项“createTimeS”(不必有对应的成员变量)
    public String getCreateTimeS(){
        String ret="";
        if(this.createTime != null){
            ret = GlobalTools.getTimeStampNumberFormat(this.createTime);
        }
        return ret;
    }
}
