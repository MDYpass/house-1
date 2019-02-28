package com.lusiwei.house.common.utils;

import org.apache.commons.codec.binary.StringUtils;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CookieUtils {

    public static Cookie getCookie(HttpServletRequest request, String key){
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            if(StringUtils.equals(cookie.getName(),key)){
                return cookie;
            }
        }
        return null;
    }

    public static void setCookie(String loginCookieKey, String s, HttpServletResponse response) {
        Cookie cookie = new Cookie(loginCookieKey, s);
        cookie.setMaxAge(7*24*60*60);
        //cookie.setDomain(".baidu.com ");  www.baidu.com  a.baidu.com
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        response.addCookie(cookie);
    }
}
