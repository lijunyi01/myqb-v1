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
    //原始文件名，可能含中文
    private String orgName;

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
    public void setFileType(int fileType) {this.fileType = fileType;}

    public String getFilePath(){return this.filePath;}
    public void setFilePath(String filePath){this.filePath = filePath;}

    public String getOrgName(){return orgName;}
    public void setOrgName(String orgName){this.orgName = orgName;}

}
