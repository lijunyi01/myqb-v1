package allcom.oxmapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;

import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ljy on 15/7/17.
 * ok
 */
public class QuestionOmxService {

    private static Logger log = LoggerFactory.getLogger(QuestionOmxService.class);

    @Value("${systemparam.xmlBaseDir}")
    private String xmlBaseDir;
    @Value("${systemparam.cgXmlBaseDir}")
    private String cgXmlBaseDir;

    private Marshaller marshaller;
    private Unmarshaller unmarshaller;

    public QuestionOmxService(){}

    public void setMarshaller(Marshaller marshaller) {
        this.marshaller = marshaller;
    }

    public void setUnmarshaller(Unmarshaller unmarshaller) {
        this.unmarshaller = unmarshaller;
    }

    //    object -> xml
    public String saveQuestionBean(int umid,QuestionBean questionBean,boolean cgFlag) throws IOException {
        String filePath = "";
        if(cgFlag){
            filePath = cgXmlBaseDir+umid+"/"+questionBean.getQuestionId()+".xml";
        }else{
            filePath = xmlBaseDir+umid+"/"+questionBean.getQuestionId()+".xml";
        }

        FileOutputStream os = null;
        try {
            File file = new File(filePath);
            //必须先以下列方式准备好目录，否则os = new FileOutputStream(file,false); 会抛出异常
            File parent = file.getParentFile();
            if (parent != null && !parent.exists()) {
                parent.mkdirs();
            }
            os = new FileOutputStream(file, false);  //false表示覆盖写文件；true表示从文件尾写文件
            this.marshaller.marshal(questionBean, new StreamResult(os));
        }finally {
            if (os != null) {
                os.close();
            }
        }
        return filePath;
    }

    //    xml - > object
    public QuestionBean loadQuestionBean(String filePath) throws IOException {
        QuestionBean questionBean = null;
        FileInputStream is = null;
        try {
            is = new FileInputStream(filePath);
            questionBean
                    = (QuestionBean) this.unmarshaller.unmarshal(new StreamSource(is));
        } finally {
            if (is != null) {
                is.close();
            }
        }
        return questionBean;
    }

}
