package allcom.entity;

import javax.persistence.*;
import java.sql.Timestamp;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_question",indexes = {@Index(name = "i_1",columnList = "umid,grade",unique = false),@Index(name = "i_2",columnList = "classType,classSubType",unique = false),@Index(name = "i_3",columnList = "questionType",unique = false),@Index(name = "i_4",columnList = "headId",unique = false)})
public class Question implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private int grade;          //年级   －30：小班；－20:中班；－10:大班；10:小学一年级...50:小学5年级；60:小学6年级；61:初中预备班；70:初一...90:初三；100:高一...120:高三；130:大一...170:大五；180:研一...200:研三
    private int questionFlag;   // 1:独立的题目；2:复合题目的题头；3：复合题目的子题
    private int questionType;   //题目类型 0:复合题目的题头；1:选择题；2:填空题； 3:判断题；4:问答题；5:阅读理解
    private int classType;      //科目大类 1:基础科目；2:数学专业科目；3:计算机专业科目；4:化学专业科目；5:物理专业科目...
    private int classSubType;      //科目子类型 1:语文；2:数学；3:英语；4:历史；5:地理；6:生物；7:物理；8:化学...
    private String content;     //题目内容
    private String optionItem;  //选择题的待选项
    private String zqda;        //正确答案
    private String cwda;        //之前的错误解答
    private String jtxd;        //解题心得
    private int attachments;    //附件数
    private int headId;         //复合题目的子题对应的题头的Id

    protected Question() {
    }

    public Question(int umid,int grade,int questionFlag,int questionType,int classType,int classSubType) {
        this.umid = umid;
        this.grade = grade;
        this.questionFlag = questionFlag;
        this.questionType = questionType;
        this.classType = classType;
        this.classSubType = classSubType;
        this.attachments = 0;
        this.headId = 0;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){ return this.umid; }
    public void setUmid(int umid) { this.umid = umid; }

    public int getGrade(){return this.grade;}
    public void setGrade(int grade){ this.grade = grade;}

    public int getQuestionFlag(){return this.questionFlag;}
    public void setQuestionFlag(int questionFlag){ this.questionFlag = questionFlag;}

    public int getQuestionType(){return this.questionType;}
    public void setQuestionType(int questionType){this.questionType = questionType;}

    public int getClassType(){return this.classType;}
    public void setClassType(int classType){this.classType = classType;}

    public int getClassSubType(){return this.classSubType;}
    public void setClassSubType(int classSubType){this.classSubType = classSubType;}

    public String getContent(){return this.content;}
    public void setContent(String content){this.content = content;}

    public String getOptionItem(){return this.optionItem;}
    public void setOptionItem(String optionItem) {this.optionItem = optionItem;}

    public String getZqda(){return this.zqda;}
    public void setZqda(String zqda) {this.zqda = zqda;}

    public String getCwda(){return this.cwda;}
    public void setCwda(String cwda) {this.cwda = cwda;}

    public String getJtxd(){return this.jtxd;}
    public void setJtxd(String jtxd) {this.jtxd = jtxd;}

    public int getAttachments(){return this.attachments;}
    public void setAttachments(int attachments){this.attachments = attachments;}

    public int getHeadId(){return this.headId;}
    public void setHeadId(int headId){this.headId = headId;}

}
