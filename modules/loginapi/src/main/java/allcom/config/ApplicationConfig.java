package allcom.config;

import allcom.App;
import allcom.email.MailUtil;
import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
//import org.apache.velocity.app.VelocityEngine;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
//import org.springframework.context.support.ResourceBundleMessageSource;
//import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.ui.velocity.VelocityEngineFactoryBean;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

/*
从spring3.0开始，Spring将JavaConfig整合到核心模块，普通的POJO只需要标注@Configuration注解，就可以成为spring配置类，
并通过在方法上标注@Bean注解的方式注入bean。
*/
@Configuration
@ComponentScan(basePackageClasses = App.class)
class ApplicationConfig {

    @Value("${passwordencoder.key}")
    private String encodekey;


    //在标注了@Configuration的java类中，通过在类方法标注@Bean定义一个Bean。方法必须提供Bean的实例化逻辑。
    //通过@Bean的name属性可以定义Bean的名称，未指定时默认名称为方法名。移至xml配置文件
//    @Bean
//	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
//		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
//        ppc.setLocation(new FileSystemResource("/appconf/loginapi/persistence.properties"));        //JPA的标准配置文件
//		return ppc;
//	}

    @Bean
    public static JoranConfigurator readLogbackPropertyFile(){
        File logbackFile = new File("/appconf/loginapi/logback.xml");
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        lc.reset();
        try {
            configurator.doConfigure(logbackFile);
        }
        catch (JoranException e) {
            e.printStackTrace(System.err);
            System.exit(-1);
        }
        return configurator;

    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new StandardPasswordEncoder(encodekey);
    }

     //邮件相关
//    @Bean
//    public MailUtil mailUtil(){
//        MailUtil mailUtil = new MailUtil();
//        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
//        javaMailSender.setHost(mailhost);
//        javaMailSender.setUsername(mailuser);
//        javaMailSender.setPassword(mailpass);
//        javaMailSender.setDefaultEncoding("UTF-8");
//        Properties javaMailProperties = new Properties();
//        javaMailProperties.setProperty("mail.smtp.auth","true");
//        javaMailProperties.setProperty("mail.smtp.timeout",mailtimeout);
//        javaMailSender.setJavaMailProperties(javaMailProperties);
//        mailUtil.setJavaMailSender(javaMailSender);
//        VelocityEngineFactoryBean velocityEngineFactoryBean = new VelocityEngineFactoryBean();
//        VelocityEngine velocityEngine=null;
//        try {
//            velocityEngine = velocityEngineFactoryBean.createVelocityEngine();
//            velocityEngine.setProperty("spring.velocity.checkTemplateLocation",false);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        mailUtil.setVelocityEngine(velocityEngine);
//        return mailUtil;
//    }

//    @Bean
//    public ResourceBundleMessageSource resourceBundleMessageSource(){
//        ResourceBundleMessageSource resourceBundleMessageSource = new ResourceBundleMessageSource();
//        resourceBundleMessageSource.setBasename("resource");
//        return resourceBundleMessageSource;
//    }
	
}