package allcom.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
@Table(name = "account")
//@NamedQuery(name = Account.FIND_BY_EMAIL, query = "select a from Account a where a.email = :email")
public class Account implements java.io.Serializable {

    //public static final String FIND_BY_EMAIL = "Account.findByEmail";

    @Id
    private String userName;

    @JsonIgnore    // Jackson默认是针对get方法来生成JSON字符串的; 用于排除作jason转化
    private String password;

    private String role = "ROLE_USER";
    private String site = "";

    protected Account() {

    }

    public Account(String userName, String password, String role,String site) {
        this.userName = userName;
        this.password = password;
        this.role = role;
        this.site = site;
    }

    public String getId(){
        return this.userName;
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
}
