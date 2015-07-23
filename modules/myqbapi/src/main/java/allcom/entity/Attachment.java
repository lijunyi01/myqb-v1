package allcom.entity;

import javax.persistence.*;


@SuppressWarnings("serial")
@Entity
@Table(name = "myqb_attachment",indexes = {@Index(name = "i_1",columnList = "umid",unique = false)})
public class Attachment implements java.io.Serializable {

    @Id
    @GeneratedValue
    private long id;
    private int umid;
    //文件类型：1:jpg； 2:doc ...
    private int fileType;
    private String filePath;

    protected Attachment() {
    }

    public Attachment(int umid) {
        this.umid = umid;
    }

    public long getId(){
        return this.id;
    }

    public int getUmid(){ return this.umid; }
    public void setUmid(int umid) { this.umid = umid; }

    public int getFileType(){return this.fileType;}
    public void setFilePath(int fileType) {this.fileType = fileType;}

    public String getFilePath(){return this.filePath;}
    public void setFilePath(String filePath){this.filePath = filePath;}

}
