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

    protected Account() {

    }

    public Account(int umid) {
        this.umid = umid;
    }

    public int getId(){
        return this.umid;
    }

    public void setUmid(int umid) {
        this.umid = umid;
    }
}
