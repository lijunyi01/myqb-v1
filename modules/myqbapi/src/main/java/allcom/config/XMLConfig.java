package allcom.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;

/**
 * Created by ljy on 15/7/10.
 * ok
 */
@Configuration
//以下可引入多个xml,例如：{"classpath:/helloworld.xml","classpath:/helloworld2.xml""}
//注意xml文件名不能为application.xml,可能是和ApplicationConfig.java冲突，导致启动报错
@ImportResource({"classpath:/application1.xml"})
public class XMLConfig {
}

