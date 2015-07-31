package allcom.entity;

import javax.persistence.*;

//科目大类表
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_classtype",indexes = {@Index(name = "i_1",columnList = "classType",unique = true)})
public class ClassType implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int classType;      //科目大类 1:基础科目；2:数学专业科目；3:计算机专业科目；4:化学专业科目；5:物理专业科目...
    private String typeDesc;
    private String typeDescEn;   //英文描述

    protected ClassType() {
    }

    public ClassType(int classType) {
        this.classType = classType;
    }


    public long getId(){
        return this.id;
    }

    public int getClassType(){return this.classType;}
    public void setClassType(int classType){this.classType = classType;}

    public String getTypeDesc(){return this.typeDesc;}
    public void setTypeDesc(String typeDesc){this.typeDesc = typeDesc;}

    public String getTypeDescEn(){return this.typeDescEn;}
    public void setTypeDescEn(String typeDescEn){this.typeDescEn = typeDescEn;}

}
