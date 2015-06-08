package myqb.cn.config;


import myqb.cn.filters.MyFilter;

import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

import javax.servlet.*;

//spring4 framework 下的入口类，参见笔记《SpringMVC4零配置（入口分析）》
//spring DispatcherServlet的配置,其它servlet和监听器等需要额外声明，用@Order注解设定启动顺序
public class WebAppInitializer extends AbstractAnnotationConfigDispatcherServletInitializer {

    /*
      * DispatcherServlet的映射路径
      */
    @Override
    protected String[] getServletMappings() {
        return new String[]{"/"};
    }

    /*
      * 应用上下文，除web部分
      */
    @Override
    protected Class<?>[] getRootConfigClasses() {
        //加载配置文件类，需要使用@Configuration注解进行标注；其中XMLConfig.class实现了从xml配置应用上下文
        //return new Class<?>[] {ApplicationConfig.class, JpaConfig.class, XMLConfig.class};
        return new Class<?>[] {ApplicationConfig.class, JpaConfig.class};
    }

    /*
      * web上下文；其中InterceptorsConfig.class是自定义的从xml配置web上下文，主要配置了各拦截器
      */
    @Override
    protected Class<?>[] getServletConfigClasses() {
        return new Class<?>[] {WebMvcConfig.class,InterceptorsConfig.class};
    }

    /*
      * 注册过滤器，映射路径与DispatcherServlet一致，路径不一致的过滤器需要注册到另外的WebApplicationInitializer中
      */
    @Override
    protected Filter[] getServletFilters() {
        CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
        characterEncodingFilter.setEncoding("UTF-8");
        characterEncodingFilter.setForceEncoding(true);

        //DelegatingFilterProxy securityFilterChain = new DelegatingFilterProxy("springSecurityFilterChain");

        //自定义的过滤器
        MyFilter myFilter = new MyFilter();
//        myFilter1.setForcePrintLog(true);

        //加入系统过滤器以及自定义的过滤器
        //return new Filter[] {characterEncodingFilter, securityFilterChain,myFilter};
        return new Filter[] {characterEncodingFilter,myFilter};
    }

    @Override
    protected void customizeRegistration(ServletRegistration.Dynamic registration) {
        registration.setInitParameter("defaultHtmlEscape", "true");
        registration.setInitParameter("spring.profiles.active", "default");
    }


}