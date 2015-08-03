package allcom.service;

import allcom.controller.RetMessage;
import allcom.dao.AccountRepository;
import allcom.dao.AttachmentRepository;
import allcom.entity.Account;
import allcom.entity.Attachment;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by ljy on 15/6/10.
 * ok
 */
@Service
public class AttachmentService {
    private static Logger log = LoggerFactory.getLogger(AttachmentService.class);

    @Autowired
    private AttachmentRepository attachmentRepository;

    //创建一条记录
    public Attachment createAttachment(int umid,String orgName,int fileType){

        Attachment attachment = new Attachment(umid);
        Attachment attachmentRet = null;
        attachment.setOrgName(orgName);
        attachment.setFileType(fileType);
        attachmentRet = attachmentRepository.save(attachment);
        return attachmentRet;
    }

    public boolean setFilePath(long id,String filePaht){
        boolean ret = false;
        Attachment attachment = attachmentRepository.findOne(id);
        if(attachment !=null){
            attachment.setFilePath(filePaht);
            if(attachmentRepository.save(attachment)!=null){
                ret = true;
            }
        }
        return  ret;
    }

    public String getPicFilePath(long id,int umid){
        String ret = "";
        Attachment attachment = attachmentRepository.findOne(id);
        if(attachment !=null){
            if(attachment.getUmid()==umid && attachment.getFileType() ==1){
                ret = attachment.getFilePath();
            }
        }
        return  ret;
    }

    public void deleteAttachment(long id){
        Attachment attachment = attachmentRepository.findOne(id);
        if(attachment !=null){
            attachmentRepository.delete(id);
        }
    }

    public RetMessage returnFail(String area,String errorCode){
        RetMessage retMessage = new RetMessage();
        retMessage.setErrorCode(errorCode);
        retMessage.setErrorMessage(GlobalTools.getMessageByLocale(area,errorCode));
        return retMessage;
    }



}