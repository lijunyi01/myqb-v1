package allcom.controller;

import allcom.entity.Attachment;
import allcom.service.AttachmentService;
import allcom.service.SessionService;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.ArrayUtils;
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

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;

@Controller
public class ShowPicController {
    private static Logger log = LoggerFactory.getLogger(ShowPicController.class);
//    private final String [] extensionForbidden = {"exe", "js", "dmg","mp3"};
//
//    @Value("${systemparam.attachmentBaseDir}")
//    private String attachmentBaseDir;
//
    @Autowired
    private SessionService sessionService;
    @Autowired
    private AttachmentService attachmentService;

    //http://localhost:8080/showscaledpic?umid=1&sessionId=1&xsize=200&ysize=100&picId=16
    @RequestMapping(value = "/showscaledpic")
    public
    void showScaledPic(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "picId",required = true)long picId,
            @RequestParam(value = "xsize",required = false,defaultValue = "60")int xsize,
            @RequestParam(value = "ysize",required = false,defaultValue = "60")int ysize,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request,
            HttpServletResponse response

    ) {

        if (sessionService.verifySessionId(umid, sessionId)) {
            FileInputStream in = null;
            OutputStream outputStream = null;
            String filePath = "";
            int i=-1;
            byte[] data = null;

            filePath = attachmentService.getPicFilePath(picId, umid);
            if (!filePath.equals("")) {
                try {
                    in = new FileInputStream(filePath);
                    //用于读入原图
                    BufferedImage bufferedImage = ImageIO.read(in);
                    in.close();

                    //通过getScaledInstance得到一个一个xsize*ysize的Image对象
                    Image image= bufferedImage.getScaledInstance(xsize, ysize, BufferedImage.SCALE_DEFAULT);

                    setResponseHeaders(response);
                    //用于输出缩略图
                    BufferedImage bufferedImage1 = new BufferedImage(xsize,ysize,BufferedImage.TYPE_INT_RGB);
                    bufferedImage1.getGraphics().drawImage(image, 0, 0, null);
                    ImageIO.write(bufferedImage1, "jpg",response.getOutputStream());

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }

    //http://localhost:8080/showpic?umid=1&sessionId=1&picId=16
    //流程:通过FileInputStream将文件读到BufferedImage；再将BufferedImage传至OutputStream
    //与showPic2的差异在于BufferedImage和字节数组
    @RequestMapping(value = "/showpic")
    public
    void showPic(
            @RequestParam(value = "umid",required = true)int umid,
            @RequestParam(value = "sessionId",required = true)String sessionId,
            @RequestParam(value = "picId",required = true)long picId,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request,
            HttpServletResponse response

    ) {

        if (sessionService.verifySessionId(umid, sessionId)) {
            FileInputStream in = null;
            String filePath = "";

            filePath = attachmentService.getPicFilePath(picId, umid);
            if (!filePath.equals("")) {
                try {
                    in = new FileInputStream(filePath);
                    //读入原图
                    BufferedImage bufferedImage = ImageIO.read(in);
                    in.close();
                    //输出所读入的图
                    setResponseHeaders(response);
                    ImageIO.write(bufferedImage, "jpg",response.getOutputStream());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }


     //http://localhost:8080/showpic2?umid=1&sessionId=1&picId=16
     //流程:通过FileInputStream将文件读到字节数组；再将字节数组的内容传至OutputStream
     @RequestMapping(value = "/showpic2")
     public
     void showPic2(
                    @RequestParam(value = "umid",required = true)int umid,
                    @RequestParam(value = "sessionId",required = true)String sessionId,
                    @RequestParam(value = "picId",required = true)long picId,
                    @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
                    HttpServletRequest request,
                    HttpServletResponse response

            ) {

        if (sessionService.verifySessionId(umid, sessionId)) {
            FileInputStream in = null;
            OutputStream outputStream = null;
            String filePath = "";
            int i=-1;
            byte[] data = null;

            filePath = attachmentService.getPicFilePath(picId, umid);
            if (!filePath.equals("")) {
                try {
                    in = new FileInputStream(filePath);
                    i=in.available();
                    if(i!=-1) {
                        data = new byte[i];
                        in.read(data);
                        in.close();
                    }

                    if(data !=null){
                        setResponseHeaders(response);
                        outputStream =new BufferedOutputStream(response.getOutputStream());
                        outputStream.write(data);
                        outputStream.flush();
                        outputStream.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (Exception e){
                    e.printStackTrace();
                }

            }
        }
    }



    protected void setResponseHeaders(HttpServletResponse response) {
        response.setContentType("image/png");
        response.setHeader("Cache-Control", "no-cache, no-store");
        response.setHeader("Pragma", "no-cache");
        long time = System.currentTimeMillis();
        response.setDateHeader("Last-Modified", time);
        response.setDateHeader("Date", time);
        response.setDateHeader("Expires", time);
    }

}

