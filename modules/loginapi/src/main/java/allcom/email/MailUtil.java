package allcom.email;

import allcom.toolkit.GlobalTools;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;


import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;


/**
 * Created by ljy on 15/7/9.
 * ok
 */
@Component("mailUtil")
public class MailUtil {
    private static Logger log = LoggerFactory.getLogger(MailUtil.class);

    @Autowired
    private JavaMailSender javaMailSender;
    //private VelocityEngine velocityEngine;          //模板解析


    /**
     * @param mailBean
     * @return
     * @throws MessagingException
     */
    public boolean send(MailBean mailBean) throws MessagingException {

        boolean ret = false;
        MimeMessage msg = createMimeMessage(mailBean);

        if(msg !=null) {
            if (this.sendMail(msg, mailBean)) {
                ret = true;
            }
        }

        return ret;
    }

    private boolean sendMail(MimeMessage msg, MailBean mailBean){
        boolean ret = false;
        boolean catchException = false;
        try {
            javaMailSender.send(msg);
        }catch (Exception e){
            e.printStackTrace();
            catchException = true;
        }
        if(catchException){
            log.info("$$$ Send mail failed !!! Subject:" + mailBean.getSubject()
                    + ", TO:" + GlobalTools.arrayToStr(mailBean.getToEmails()));
        }else {
            ret = true;
            log.info("$$$ Send mail Subject:" + mailBean.getSubject()
                    + ", TO:" + GlobalTools.arrayToStr(mailBean.getToEmails()));
        }
        return  ret;

    }

    /*
     * 根据 mailBean 创建 MimeMessage
     */
    private MimeMessage createMimeMessage(MailBean mailBean) throws MessagingException {
        if (!checkMailBean(mailBean)) {
            return null;
        }
        String text = getMessage(mailBean);
        if(text == null){
            log.warn("@@@ warn mail text is null (Thread name="
                    + Thread.currentThread().getName() + ") @@@ " +  mailBean.getSubject());
            return null;
        }
        MimeMessage msg = javaMailSender.createMimeMessage();
        MimeMessageHelper messageHelper = new MimeMessageHelper(msg, true, "UTF-8");
        //messageHelper.setFrom(mailBean.getFrom());
        try {
            messageHelper.setFrom(mailBean.getFrom(), mailBean.getFromName());
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        messageHelper.setSubject(mailBean.getSubject());
        messageHelper.setTo(mailBean.getToEmails());
        messageHelper.setText(text, true); // html: true

        return msg;
    }

    /*
     * 模板填充
     * @param mailBean
     * @return
     */
    private String getMessage(MailBean mailBean) {
//        StringWriter writer = null;
//        try {
//
//            writer = new StringWriter();
//            VelocityContext context = new VelocityContext(mailBean.getData());
//
//            velocityEngine.evaluate(context, writer, "", mailBean.getTemplate());
//
//            return writer.toString();
//        } catch (VelocityException e) {
//            log.error(" VelocityException : " + mailBean.getSubject() + "\n" + e);
////        } catch (IOException e) {
////            log.error(" IOException : " + mailBean.getSubject() + "\n" + e);
//        } finally {
//            if (writer != null) {
//                try {
//                    writer.close();
//                } catch (IOException e) {
//                    log.error("###StringWriter close error ... ");
//                }
//            }
//        }
//        return null;

        //TODO:这里要做得更通用
        String ret = null;
        String template_s = mailBean.getTemplate();
        if(template_s!=null){
            String replaceString = (String)mailBean.getData().get("emailVerifyCode");
            ret = template_s.replace("${emailVerifyCode}",replaceString);
        }
        return ret;
    }

    /*
     * check 邮件
     */
    private boolean checkMailBean(MailBean mailBean){
        if (mailBean == null) {
            log.warn("@@@ warn mailBean is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        if (mailBean.getSubject() == null) {
            log.warn("@@@ warn mailBean.getSubject() is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        if (mailBean.getToEmails() == null) {
            log.warn("@@@ warn mailBean.getToEmails() is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        if (mailBean.getTemplate() == null) {
            log.warn("@@@ warn mailBean.getTemplate() is null (Thread name="
                    + Thread.currentThread().getName() + ") ");
            return false;
        }
        return true;
    }


    /*===================== setter & getter =======================*/
//    public void setJavaMailSender(JavaMailSender javaMailSender) {
//        this.javaMailSender = javaMailSender;
//    }

//    public void setVelocityEngine(VelocityEngine velocityEngine) {
//        this.velocityEngine = velocityEngine;
//    }
}
