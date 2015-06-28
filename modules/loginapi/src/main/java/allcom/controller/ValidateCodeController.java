package allcom.controller;

import allcom.toolkit.GlobalTools;
import com.github.bingoohuang.patchca.color.ColorFactory;
import com.github.bingoohuang.patchca.custom.ConfigurableCaptchaService;
import com.github.bingoohuang.patchca.filter.predefined.*;
import com.github.bingoohuang.patchca.utils.encoder.EncoderHelper;
import com.github.bingoohuang.patchca.word.RandomWordFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.io.*;
import java.util.Locale;
import java.util.Random;

/**
 * 以流的形式输出验证码图片
 */

@Controller
public class ValidateCodeController {

    private static Logger log = LoggerFactory.getLogger(ValidateCodeController.class);
    private static ConfigurableCaptchaService cs = new ConfigurableCaptchaService();
    private static Random random = new Random();

    static {
//        cs.setColorFactory(new SingleColorFactory(new Color(25, 60, 170)));
        cs.setColorFactory(new ColorFactory() {
            @Override
            public Color getColor(int x) {
                int[] c = new int[3];
                int i = random.nextInt(c.length);
                for (int fi = 0; fi < c.length; fi++) {
                    if (fi == i) {
                        c[fi] = random.nextInt(71);
                    } else {
                        c[fi] = random.nextInt(256);
                    }
                }
                return new Color(c[0], c[1], c[2]);
            }
        });
        RandomWordFactory wf = new RandomWordFactory();
        wf.setCharacters("23456789abcdefghigkmnpqrstuvwxyzABCDEFGHIGKLMNPQRSTUVWXYZ");
        wf.setMaxLength(4);
        wf.setMinLength(4);
        cs.setWordFactory(wf);
    }

//    图片验证码获取接口（输出图片流）
    @RequestMapping(value = "/validatecode")
    public void validateCode(HttpServletRequest request, HttpServletResponse response) {

        String token = "";

        switch (random.nextInt(5)) {
            case 0:
                cs.setFilterFactory(new CurvesRippleFilterFactory(cs.getColorFactory()));
                break;
            case 1:
                cs.setFilterFactory(new MarbleRippleFilterFactory());
                break;
            case 2:
                cs.setFilterFactory(new DoubleRippleFilterFactory());
                break;
            case 3:
                cs.setFilterFactory(new WobbleRippleFilterFactory());
                break;
            case 4:
                cs.setFilterFactory(new DiffuseRippleFilterFactory());
                break;
        }
        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
        }
        setResponseHeaders(response);
        try{
            token = EncoderHelper.getChallangeAndWriteImage(cs, "png", response.getOutputStream());
        }catch (Exception e){
            e.printStackTrace();
        }

        session.setAttribute("captchaToken", token);
        log.info("当前的SessionID=" + session.getId() + "，验证码=" + token);

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

//    图片验证码验证接口
    @RequestMapping(value = "/vcodeverify")
    @ResponseBody
    public RetMessage vcodeVerify(
            @RequestParam(value = "vcode",required = true,defaultValue = "")String vcode,
            @RequestParam(value = "area",required = false,defaultValue = "cn")String area,
            HttpServletRequest request
    ) {

        RetMessage ret = new RetMessage();
        String vcodetmp = vcode.toLowerCase();

        HttpSession session = request.getSession(false);
        if (session == null) {
            session = request.getSession();
        }
        String vcodeInSession = (String)session.getAttribute("captchaToken");
        if(vcodeInSession == null){
            vcodeInSession = "";
        }
        String sessionId = session.getId();

        if (vcodetmp.equals(vcodeInSession.toLowerCase())){
            ret.setErrorCode("0");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area,"0"));
            //设置图片验证码校验成功标志
            session.setAttribute("vcodeverifyflag", "success");
            log.info("vcodeVerify success,sessionId is:"+ sessionId +" and vcode in session is:"+vcodeInSession + " and vcode in request is:"+vcode);
        }else{
            ret.setErrorCode("-2");
            ret.setErrorMessage(GlobalTools.getMessageByLocale(area, "-2"));
            log.info("vcodeVerify failed,sessionId is:" + sessionId + " and vcode in session is:" + vcodeInSession + " and vcode in request is:" + vcode);
        }

        return ret;
    }


}
