package allcom.toolkit;

import org.springframework.context.support.ResourceBundleMessageSource;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;
import java.util.Random;

/**
 * Created by ljy on 15/6/18.
 * ok
 */
public class GlobalTools {

    private static ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();


//    生成随机字符串
    public static String getRandomString(int length,boolean numberflag) { //length表示生成字符串的长度;numberflag表示是否纯数字
        String base = "";
        if(numberflag){
            base = "0123456789";
        }else {
            base = "abcdefghijklmnopqrstuvwxyz0123456789";
        }
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 格式化时间
     * Locale是设置语言敏感操作
     * @param formatTime
     * @return
     */
    public static String getTimeStampNumberFormat(Timestamp formatTime) {
//        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss", new Locale("zh", "cn"));
        SimpleDateFormat m_format = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
        return m_format.format(formatTime);
    }

    /**
     * 计算两个日期的时间差
     * @param formatTime1
     * @param formatTime2
     * @return
     */
    public static long getTimeDifference(Timestamp formatTime1, Timestamp formatTime2) {
        SimpleDateFormat timeformat = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
        long t1 = 0L;
        long t2 = 0L;
        try {
            t1 = timeformat.parse(getTimeStampNumberFormat(formatTime1)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        try {
            t2 = timeformat.parse(getTimeStampNumberFormat(formatTime2)).getTime();
        } catch (ParseException e) {
            e.printStackTrace();
        }
//因为t1-t2得到的是毫秒级,所以要初3600000得出小时.算天数或秒同理
//        int hours=(int) ((t1 - t2)/3600000);
//        int minutes=(int) (((t1 - t2)/1000-hours*3600)/60);
//        int second=(int) ((t1 - t2)/1000-hours*3600-minutes*60);
//        return ""+hours+"小时"+minutes+"分"+second+"秒";
        return (t1-t2)/1000;
    }

//    获取ip前三段;对于ipv6，就是去除最后一段，分隔符是：
    public static String getIpSegment(String ip){
        String ret="";
        if(ip.indexOf(".")>0) {
            ret = ip.substring(0, ip.lastIndexOf("."));
        }else if(ip.indexOf(":")>0){
            ret = ip.substring(0,ip.lastIndexOf(":"));
        }else{
            ret = ip;
        }
        return  ret;
    }

//    获取国际化信息内容
    public static String getMessageByLocale(String area,String key){
        resourceBundleMessageSource.setBasename("resource");

        String ret = "";
        Object[] params = {""};
        Locale locale = null;
        if(area.equals("en")){
            locale = Locale.US;
        }else{
            locale = Locale.CHINA;
        }
        ret = resourceBundleMessageSource.getMessage(key,params,locale);
        return  ret;
    }

    public static void main(String[] args){
//        1.测试随机串
//        String rs = GlobalTools.getRandomString(6,true);
//        System.out.print("测试随机串:"+rs);

//        2.测试时间差
//        Timestamp currentTime = new Timestamp(System.currentTimeMillis());
//        System.out.print("timestamp now:" + currentTime);
//
//        try {
//            sleep(5000);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        Timestamp currentTime2 = new Timestamp(System.currentTimeMillis());
//        System.out.print("timestamp now:" + currentTime2);
//
//        System.out.print("timestamp diff:");
//
//        System.out.println(getTimeDifference(currentTime2, currentTime));

//        测试获取ip前三段
//        String ip = "192.168.0.112";
//        System.out.print(getIpSegment(ip));

//        测试通过错误码获取错误信息
//        String s = getMessageByLocale("cn","-2");
//        System.out.print(s);



    }
}
