package com.github.ltsopensource.admin.web.filter;

import com.github.ltsopensource.admin.access.domain.Account;
import com.github.ltsopensource.admin.cluster.BackendAppContext;
import com.github.ltsopensource.admin.request.AccountReq;
import com.github.ltsopensource.admin.support.AppConfigurer;
import com.github.ltsopensource.admin.support.ThreadLocalUtil;
import com.github.ltsopensource.admin.web.support.PasswordUtil;
import com.github.ltsopensource.admin.web.support.SpringContextHolder;
import com.github.ltsopensource.core.commons.utils.Base64;
import com.github.ltsopensource.core.commons.utils.StringUtils;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Created by ztajy on 2015-11-11.
 *
 * @author ztajy
 * @author Robert HG (254963746@qq.com)
 */
public class LoginAuthFilter implements Filter {
    public static final String AUTH_PREFIX = "Basic_";
    private AntPathMatcher pathMatcher = new AntPathMatcher();

    private static String username = "admin";

    private String password = "admin";

    private String[] excludedURLArray;

    private BackendAppContext appContext;

    @Override
    public void init(final FilterConfig filterConfig) throws ServletException {
        username = AppConfigurer.getProperty("console.username", username);
        password = PasswordUtil.encode(AppConfigurer.getProperty("console.password", password));

        String excludedURLs = filterConfig.getInitParameter("excludedURLs");
        if (StringUtils.isNotEmpty(excludedURLs)) {
            String[] arr = excludedURLs.split(",");
            excludedURLArray = new String[arr.length];
            for (int i = 0; i < arr.length; i++) {
                excludedURLArray[i] = StringUtils.trim(arr[i]);
            }
        }
    }

    @Override
    public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        ThreadLocalUtil.setAttr("authority",false);//默认不具备管理员权限

        if(appContext == null){
            appContext = SpringContextHolder.getBean(BackendAppContext.class);
        }

        if (isExclude(httpRequest.getRequestURI())) {
            chain.doFilter(request, response);
            return;
        }

        String authorization = this.getAuthorization((HttpServletRequest)request);
        if (null != authorization && authorization.startsWith(AUTH_PREFIX)) {
            authorization = authorization.split("_")[1];
            // Owen Jia at 20190319，修改登陆体系，增加帐户表
            String usernameAndPassword = new String(Base64.decodeFast(authorization));
            if(usernameAndPassword.equals("undefined")){
                needAuthenticate(httpResponse);
                return;
            }
            String username1 = usernameAndPassword.split(":")[0];
            ThreadLocalUtil.setAttr("username",username1);//登录名称

            if(!username1.equals(username)){
                //表中账户，非系统管理员
                AccountReq accountReq = new AccountReq();
                accountReq.setUsername(username1);
                Account account = appContext.getBackendAccountAccess().selectOne(accountReq);
                String password1 = usernameAndPassword.split(":")[1];
                if(account != null){
                    if(PasswordUtil.conformPassword(password1,account.getPassword())){
                        authenticateSuccess(httpResponse);
                        chain.doFilter(httpRequest, httpResponse);
                    } else
                        needAuthenticate(httpResponse);
                } else {
                    needAuthenticate(httpResponse);
                }
            } else {
                if ((username + ":" + password).equals(usernameAndPassword)) {
                    //管理员账户
                    ThreadLocalUtil.setAttr("authority",true);
                    authenticateSuccess(httpResponse);
                    chain.doFilter(httpRequest, httpResponse);
                } else {
                    needAuthenticate(httpResponse);
                }
            }
        } else {
            needAuthenticate(httpResponse);
        }
    }

    public static String getUsername() {
        return username;
    }

    private boolean isExclude(String path) {
        if (excludedURLArray != null) {
            for (String page : excludedURLArray) {
                //判断是否在过滤url中
                if (pathMatcher.match(page, path)) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * 验证权限cookie是否有效
     * @param request
     * @return boolean true有效,false无效
     */
    private String getAuthorization(final HttpServletRequest request){
        Cookie[] cookies = request.getCookies();
        for(int i = 0; i< cookies.length; i++){
            Cookie t = cookies[i];
            if(t.getName().equals("authorization")){
                return t.getValue();
            }
        }
        return request.getHeader("authorization");
    }

    private void authenticateSuccess(final HttpServletResponse response) {
        response.setStatus(200);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-store");
        response.setDateHeader("Expires", 0);
    }

    private void needAuthenticate(final HttpServletResponse response) {
        try {
            response.setHeader("Cache-Control", "no-store");
            response.setDateHeader("Expires", 0);
            response.sendRedirect("/login.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void destroy() {
    }
}
