package myqb.cn.config;

import static org.springframework.context.annotation.ComponentScan.Filter;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.joran.spi.JoranException;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Controller;

import myqb.cn.App;

import java.io.File;

/*
从spring3.0开始，Spring将JavaConfig整合到核心模块，普通的POJO只需要标注@Configuration注解，就可以成为spring配置类，
并通过在方法上标注@Bean注解的方式注入bean。
*/
@Configuration
@ComponentScan(basePackageClasses = App.class, excludeFilters = @Filter({Controller.class, Configuration.class}))
class ApplicationConfig {

    //在标注了@Configuration的java类中，通过在类方法标注@Bean定义一个Bean。方法必须提供Bean的实例化逻辑。
    //通过@Bean的name属性可以定义Bean的名称，未指定时默认名称为方法名。
    @Bean
	public static PropertyPlaceholderConfigurer propertyPlaceholderConfigurer() {
		PropertyPlaceholderConfigurer ppc = new PropertyPlaceholderConfigurer();
		//ppc.setLocation(new ClassPathResource("/persistence.properties"));
        ppc.setLocation(new FileSystemResource("/appconf/loginapi/persistence.properties"));   //应用配置文件，含JPA的配置文件
		return ppc;
	}

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
//    //从程序外部读取logbak的配置文件
//    private static void readLogbackPropertyFile1(){
//        File logbackFile = new File("/appconf/processmonitor/logback.xml");
//        if (logbackFile.exists()) {
//            LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
//            JoranConfigurator configurator = new JoranConfigurator();
//            configurator.setContext(lc);
//            lc.reset();
//            try {
//                configurator.doConfigure(logbackFile);
//            }
//            catch (JoranException e) {
//                e.printStackTrace(System.err);
//                System.exit(-1);
//            }
//        }
//
//    }1
	
}