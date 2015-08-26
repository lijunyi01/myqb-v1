package allcom.entity;

import javax.persistence.*;

//订正本表，一个题目做多只能属于一个订正本
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_notebook",indexes = {@Index(name = "i_1",columnList = "umid,name",unique = true),@Index(name = "i_2",columnList = "groupId",unique = false)})
public class NoteBook implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private String name;   //订正本名
    private long groupId;  //订正本组的id

    protected NoteBook(){}

    public NoteBook(int umid,String name) {
        this.umid = umid;
        this.name = name;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){return this.umid;}
    public void setUmid(int umid){this.umid = umid;}

    public String getName(){return this.name;}
    public void setName(String name){this.name = name;}

    public long getGroupId(){return this.groupId;}
    public void setGroupId(long groupId) {this.groupId = groupId;}

}
