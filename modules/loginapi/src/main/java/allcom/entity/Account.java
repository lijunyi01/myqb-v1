package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqbauth_account")
//@NamedQuery(name = Account.FIND_BY_USERNAME, query = "select a from Account a where a.email = :userName or a.phoneNumber = :userName or a.nickName = :userName")
public class Account implements java.io.Serializable {

    //public static final String FIND_BY_USERNAME = "Account.findByUserName";

    @Id
    @GeneratedValue
    private int umid;

    @JsonIgnore    // Jackson默认是针对get方法来生成JSON字符串的; 用于排除作jason转化
    private String password;

    private String role = "ROLE_USER";
    private String site = "";
    private String phoneNumber = "";
    private String email = "";
    private String nickName = "";

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

    public String getNickName(){return this.nickName;}

    public void setNickName(String nickName){this.nickName = nickName;}
}
