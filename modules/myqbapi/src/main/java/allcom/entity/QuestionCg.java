package allcom.entity;

import javax.persistence.*;

//草稿表
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_questioncg",indexes = {@Index(name = "i_1",columnList = "umid,grade",unique = false),@Index(name = "i_2",columnList = "classType,classSubType",unique = false),@Index(name = "i_3",columnList = "questionType",unique = false)})
public class QuestionCg implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private int grade;          //年级   －30：小班；－20:中班；－10:大班；10:小学一年级...50:小学5年级；60:小学6年级；61:初中预备班；70:初一...90:初三；100:高一...120:高三；130:大一...170:大五；180:研一...200:研三
    private int multiplexFlag;   // 0:独立的题目；1:复合题目(含子题)
    private int questionType;   //题型 1:选择题；2:填空题； 3:判断题；4:问答题；100:未归类的复合题目；101:阅读理解
    private int classType;      //科目大类 1:基础科目；2:数学专业科目；3:计算机专业科目；4:化学专业科目；5:物理专业科目...
    private int classSubType;      //科目子类型 1:语文；2:数学；3:英语；4:历史；5:地理；6:生物；7:物理；8:化学...
    //private int knownFlag;      //0:正常的题目   1:已经完全理解了的题目
    private String contentPath;     //题目内容文件（xml）的路径
    //private String optionItem;  //选择题的待选项  (置入xml)
    //private String zqda;        //正确答案   (置入xml)
    //private String cwda;        //之前的错误解答 (置入xml)
    //private String jtxd;        //解题心得   (置入xml)
    //private int attachments;    //附件数   (置入xml)
    //private long questionContentId;     //对应的内容表的Id,分离出内容表是为了提升本表查询性能；该字段实际是内容表的外键，但考虑性能未定义外键，由应用管理两表关系
    private String subject;

    protected QuestionCg() {
    }

    public QuestionCg(int umid) {
        this.umid = umid;
    }

    public QuestionCg(int umid, int grade, int multiplexFlag, int questionType, int classType, int classSubType, String subject) {
        this.umid = umid;
        this.grade = grade;
        this.multiplexFlag = multiplexFlag;
        this.classType = classType;
        this.classSubType = classSubType;
        this.subject = subject;
        this.questionType = questionType;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){ return this.umid; }
    public void setUmid(int umid) { this.umid = umid; }

    public int getGrade(){return this.grade;}
    public void setGrade(int grade){ this.grade = grade;}

    public int getMultiplexFlag(){return this.multiplexFlag;}
    public void setMultiplexFlag(int multiplexFlag){ this.multiplexFlag = multiplexFlag;}

    public int getQuestionType(){return this.questionType;}
    public void setQuestionType(int questionType){this.questionType = questionType;}

    public int getClassType(){return this.classType;}
    public void setClassType(int classType){this.classType = classType;}

    public int getClassSubType(){return this.classSubType;}
    public void setClassSubType(int classSubType){this.classSubType = classSubType;}

    public String getContentPath(){return this.contentPath;}
    public void setContentPath(String contentPath){this.contentPath = contentPath;}

    public String getSubject(){return this.subject;}
    public void setSubject(String subject){this.subject = subject;}

}
