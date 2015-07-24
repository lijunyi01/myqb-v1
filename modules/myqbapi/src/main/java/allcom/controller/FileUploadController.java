package allcom.controller;

import allcom.entity.Attachment;
import allcom.service.AttachmentService;
import allcom.service.SessionService;
import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;

@Controller
public class FileUploadController {
    private static Logger log = LoggerFactory.getLogger(FileUploadController.class);
    private final String [] extensionForbidden = {"exe", "js", "dmg","mp3"};

    @Value("${systemparam.attachmentBaseDir}")
    private String attachmentBaseDir;

    @Autowired
    private SessionService sessionService;
    @Autowired
    private AttachmentService attachmentService;

//    @RequestMapping(value = "/upload", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String provideUploadInfo() {
//        return "You can upload a file by posting to this same URL.";
//    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    public
    @ResponseBody
    RetMessage handleFileUpload(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            @RequestParam("file") MultipartFile file
    ) {
        RetMessage ret = null;
        String name="";
        String fileExtension="";
        int fileType = -100;

        if (sessionService.verifySessionId(umid, sessionId)) {
            if (!file.isEmpty()) {
                name = file.getOriginalFilename();
                fileExtension = FilenameUtils.getExtension(name);
                if(fileExtension.equals("jpg") ||fileExtension.equals("bmp")|| fileExtension.equals("png")){
                    fileType = 1;
                }else if(fileExtension.equals("doc")||fileExtension.equals("docx")){
                    fileType = 2;
                }
                if (!ArrayUtils.contains(extensionForbidden, fileExtension) && fileType!=-100) {
                    Attachment attachment = attachmentService.createAttachment(umid,name,fileType);
                    if(attachment !=null) {
                        long attachmentId = attachment.getId();
                        try {
                            byte[] bytes = file.getBytes();
                            String saveName = attachmentBaseDir + umid + "/" + attachmentId + "." + fileExtension;
                            File saveFile = new File(saveName);
                            //必须先以下列方式准备好目录，否则new FileOutputStream()会抛出异常
                            File parent = saveFile.getParentFile();
                            if(parent!=null&&!parent.exists()){
                                parent.mkdirs();
                            }
                            BufferedOutputStream stream =
                                    new BufferedOutputStream(new FileOutputStream(new File(saveName)));
                            stream.write(bytes);
                            stream.close();
                            if(attachmentService.setFilePath(attachmentId,saveName)) {
                                ret = sessionService.returnFail(area, "0");
                                log.info("successfully uploaded " + name + "! umid is:" + umid);
                            }else{
                                //删除文件
                                if(saveFile.exists()){
                                    saveFile.delete();
                                }
                                attachmentService.deleteAttachment(attachmentId);
                                ret = sessionService.returnFail(area, "-22");
                                log.info("failed to save in db when upload " + name + "umid is:" + umid );
                            }
                        } catch (Exception e) {
                            attachmentService.deleteAttachment(attachmentId);
                            ret = sessionService.returnFail(area, "-21");
                            log.info("failed to save file when upload " + name + "umid is:" + umid + " and errormsg:" + e.getMessage());
                        }
                    }else{
                        ret = sessionService.returnFail(area, "-22");
                        log.info("failed to save in db when upload " + name + "umid is:" + umid );
                    }
                } else {
                    ret = sessionService.returnFail(area, "-19");
                    log.info("fileExtension forbidden,name: " + name + " umid is:" + umid);
                }
            } else {
                ret = sessionService.returnFail(area, "-20");
                log.info("umid:" + umid + " failed to upload " + name + " because the file was empty.");
            }
        }else{
            ret = sessionService.returnFail(area, "-4");
            log.info("umid:" + umid + " failed to check sessionid:" + sessionId);
        }
        return ret;
    }

//    @RequestMapping(value = "/batchupload", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String batchFileUpload(HttpServletRequest request) {
//        String name="";
//        String fileExtension="";
//        List<MultipartFile> files = ((MultipartHttpServletRequest)request).getFiles("file");
//        for (int i =0; i< files.size(); ++i) {
//            MultipartFile file = files.get(i);
//            if (!file.isEmpty()) {
//                name = file.getOriginalFilename();
//                fileExtension = FilenameUtils.getExtension(name);
//                if(!ArrayUtils.contains(extensionForbidden, fileExtension)) {
//                    try {
//                        byte[] bytes = file.getBytes();
//                        BufferedOutputStream stream =
//                                new BufferedOutputStream(new FileOutputStream(new File(name)));
//                        stream.write(bytes);
//                        stream.close();
//                    } catch (Exception e) {
//                        return "You failed to upload " + name + " => " + e.getMessage();
//                    }
//                }else{
//                    return "fileExtension forbidden,name: " + name ;
//                }
//            } else {
//                return "You failed to upload " + name + " because the file was empty.";
//            }
//        }
//        return "upload successful";
//
//    }



}

