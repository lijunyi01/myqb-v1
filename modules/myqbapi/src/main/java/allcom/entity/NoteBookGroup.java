package allcom.entity;

import javax.persistence.*;

//订正本组表
@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_notebookgroup",indexes = {@Index(name = "i_1",columnList = "umid,name",unique = true)})
public class NoteBookGroup implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    private String name;   //组名

    protected NoteBookGroup(){}

    public NoteBookGroup(int umid, String name) {
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

}
