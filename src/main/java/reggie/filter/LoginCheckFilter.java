package reggie.filter;

import com.alibaba.fastjson.JSON;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;
import reggie.common.R;
import reggie.common.ThreadLocalUtil;
import reggie.entity.User;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@WebFilter(filterName = "LoginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {
    public static final AntPathMatcher PATH_MATCHER=new AntPathMatcher();
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req=(HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        String url=req.getRequestURI();
        log.info("拦截了"+url);
        String[]urls=new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };
        if (isCheckUrl(urls,url)){
            chain.doFilter(req,res);
            return;
        }
        Long empId = (Long) req.getSession().getAttribute("emp");
        if (empId!=null){
            ThreadLocalUtil.setCurrentId(empId);
            chain.doFilter(req,res);
            return;
        }

        User user=(User) req.getSession().getAttribute("user");
        log.info("移动端用户是否登录==》"+user);
        if (user!=null){
            ThreadLocalUtil.setCurrentId(user.getId());
            chain.doFilter(req,res);
            return;
        }
        res.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }

    private Boolean isCheckUrl(String[] urls,String url){
        for(String s:urls){
            boolean match=PATH_MATCHER.match(s,url);
            if (match)return true;
        }
        return false;
    }
}
