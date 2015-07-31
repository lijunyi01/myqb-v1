package allcom.entity;

import javax.persistence.*;

//科目子类表
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_classsubtype",indexes = {@Index(name = "i_1",columnList = "classType,classSubType",unique = true)})
public class ClassSubType implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int classType;      //科目大类 1:基础科目；2:数学专业科目；3:计算机专业科目；4:化学专业科目；5:物理专业科目...
    private int classSubType;   //科目子类型 1:语文；2:数学；3:英语；4:历史；5:地理；6:生物；7:物理；8:化学...
    private String subTypeDesc;
    private String subTypeDescEn;   //英文描述

    protected ClassSubType() {
    }

    public ClassSubType(int classType,int classSubType) {
        this.classType = classType;
        this.classSubType = classSubType;
    }


    public long getId(){
        return this.id;
    }

    public int getClassType(){return this.classType;}
    public void setClassType(int classType){this.classType = classType;}

    public String getSubTypeDesc(){return this.subTypeDesc;}
    public void setSubTypeDesc(String subTypeDesc){this.subTypeDesc = subTypeDesc;}

    public int getClassSubType(){return this.classSubType;}
    public void setClassSubType(int classSubType){this.classSubType = classSubType;}

    public String getSubTypeDescEn(){return this.subTypeDescEn;}
    public void setSubTypeDescEn(String subTypeDescEn){this.subTypeDescEn =subTypeDescEn;}

}
