package com.lusiwei.house.web.inteceptor;

import com.lusiwei.house.common.constants.Constant;
import com.lusiwei.house.common.pojo.User;
import com.lusiwei.house.common.response.ResponseResult;
import com.lusiwei.house.common.utils.CookieUtils;
import com.lusiwei.house.common.utils.ThreadLocalUtil;
import com.lusiwei.house.ds.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private IUserService userService;
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return super.preHandle(request, response, handler);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        //先从session取，session里取不到就去cookie里取
        User user = (User)request.getSession().getAttribute(Constant.SESSION_KEY);
        if (user == null) {
            Cookie cookie = CookieUtils.getCookie(request, Constant.LOGIN_COOKIE_KEY);
            if (cookie != null) {
                String value = cookie.getValue();
                String[] split = value.split("-");
                if (split.length==2) {
                    ResponseResult responseResult = userService.doSignin(split[0], split[1]);
                    User data = (User) responseResult.getData();
                    request.setAttribute("loginUser",data);
                }
            }
            response.sendRedirect("/accounts/toSignin?errorMsg=请先登陆");
        }
        super.postHandle(request, response, handler, modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        super.afterCompletion(request, response, handler, ex);
    }
}
