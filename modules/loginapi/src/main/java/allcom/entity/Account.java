package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
//email and nickName 可能为null，但唯一索引可以容纳多个null
@Table(name = "myqbauth_account",indexes = {@Index(name = "i_1",columnList = "phoneNumber",unique = true),@Index(name = "i_2",columnList = "email",unique = true)})
public class Account implements java.io.Serializable {

    @Id
    @GeneratedValue
    private int umid;

    @JsonIgnore    // Jackson默认是针对get方法来生成JSON字符串的; 用于排除作jason转化
    private String password;

    private String role = "";
    private String site = "";
    private String phoneNumber = "";
    private String email;
    private int emailVerifyFlag = 0;
    //private String nickName;

    protected Account() {

    }

    public Account(String phoneNumber,String password, String role,String site) {
        this.phoneNumber = phoneNumber;
        this.password = password;
        this.role = role;
        this.site = site;
    }

    public int getId(){
        return this.umid;
    }

    public String getPassword() {
        return this.password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }

    public String getSite() {return this.site;}
    public void setSite(String site) { this.site = site;}

    public String getPhoneNumber(){return this.phoneNumber;}
    public void setPhoneNumber(String phoneNumber){this.phoneNumber = phoneNumber;}

    public String getEmail(){return this.email;}
    public void setEmail(String email){this.email = email;}

    //public String getNickName(){return this.nickName;}
    //public void setNickName(String nickName){this.nickName = nickName;}

    public int getEmailVerifyFlag(){return this.emailVerifyFlag;}
    public void setEmailVerifyFlag(int emailVerifyFlag){this.emailVerifyFlag = emailVerifyFlag;}
}
