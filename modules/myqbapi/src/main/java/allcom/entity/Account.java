package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
//email and nickName 可能为null，但唯一索引可以容纳多个null
@Table(name = "myqb_account")
public class Account implements java.io.Serializable {

    @Id
    private int umid;
    private String address;
    private String city;
    private String province;
    private int grade;
    private String nickName;

    protected Account() {

    }

    public Account(int umid) {
        this.umid = umid;
        this.grade = 0;
    }

    public int getId(){
        return this.umid;
    }
    public void setUmid(int umid) {
        this.umid = umid;
    }

    public String getAddress(){return this.address;}
    public void setAddress(String address){this.address = address;}

    public String getCity(){return this.city;}
    public void setCity(String city) {this.city = city;}

    public String getProvince(){return  this.province;}
    public void setProvince(String province) {this.province = province;}

    public int getGrade(){return this.grade;}
    public void setGrade(int grade){this.grade = grade;}

    public String getNickName(){return this.nickName;}
    public void setNickName(String nickName) { this.nickName = nickName;}

}
