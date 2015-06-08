package myqb.cn.filters;

//import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ljy on 15/5/29.
 * 自定义过滤器
 */
public class MyFilter extends OncePerRequestFilter {
    private boolean forcePrintLog = false;

    public MyFilter() {
    }


    public void setForcePrintLog(boolean forcePrintLog) {
        this.forcePrintLog = forcePrintLog;
    }

    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String clientip = request.getRemoteAddr();
        if (this.forcePrintLog) {
                System.out.print("show function of my own filter!!! and client ip is:"+ clientip);
        }

        //通过抛出异常可以起到拦截作用
//        if(!clientip.equals("211.95.73.111")){
//            throw new AccessDeniedException("Access has been denied for your IP address: "+request.getRemoteAddr());
//        }
        //交给filterChain的其它过滤器继续处理（如果还有后续过滤器的话）
        filterChain.doFilter(request, response);
    }
}
