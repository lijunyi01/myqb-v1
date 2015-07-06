package allcom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by ljy on 15/5/27.
 * 演示了如何混合使用配置类和xml来配置spring容器；实际在本项目中，由于helloworld.xml没有实质内容，因此实际不起作用
 */
@Configuration
//以下可引入多个xml,例如：{"classpath:/helloworld.xml","classpath:/helloworld2.xml""}
@ImportResource({"classpath:/interceptors.xml"})
public class InterceptorsConfig {
}
