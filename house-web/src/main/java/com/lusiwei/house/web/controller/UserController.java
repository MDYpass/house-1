package com.lusiwei.house.web.controller;

import cn.hutool.core.util.StrUtil;
import com.lusiwei.house.common.constants.Constant;
import com.lusiwei.house.common.pojo.User;
import com.lusiwei.house.common.response.ResponseResult;
import com.lusiwei.house.common.utils.CookieUtils;
import com.lusiwei.house.ds.service.IUserService;
import com.lusiwei.house.ds.service.fastdfs.FastDfsUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Controller
@Slf4j
public class UserController {
    @Autowired
    private IUserService userService;
    @Autowired
    private FastDfsUtil fastDfsUtil;

    @RequestMapping("query")
    @ResponseBody
    public List<User> queryAll() {
        return userService.queryAll();
    }

    @RequestMapping("freemarker")
    public String test(ModelMap modelMap) {
        modelMap.put("key", userService.queryAll().get(0));
        return "test";
    }

    @RequestMapping("index")
    public String index() {
        return "homepage/index";
    }

    @RequestMapping("/accounts/register")
    public String toRegister() {
        return "user/accounts/register";
    }

    @PostMapping("/user/register")
    public String doRegister(User user, String confirmPasswd, @RequestParam("avatarFile") MultipartFile multipartFile) {
        ResponseResult responseResult = userService.doRegister(user, confirmPasswd, multipartFile);
        if (responseResult.isSuccess()) {
            return "redirect:/user/accounts/registerSubmit?email=" + user.getEmail();
        }
        return "user/accounts/register?" + Constant.ERROR_KEY + "=" + responseResult.getMsg();
    }

    @RequestMapping("/user/accounts/registerSubmit")
    public void registerSubmit(ModelMap modelMap, String email) {
        modelMap.put("email", email);
    }

    /**
     * 激活
     *
     * @param activeCode
     * @return
     */
    @RequestMapping("/active/{uuid}")
    public String active(@PathVariable("uuid") String activeCode,ModelMap modelMap) {
        String email = userService.getRegisterCache().getIfPresent(activeCode);
        if (!activeCode.equals(email)) {
            modelMap.put("activeStatus","激活连接已过期，请重新注册");
        }
        log.info("从本地缓存中取到的email为{}", email);
        userService.active(email);
        modelMap.put("activeStatus","激活成功");
        return "/user/accounts/active";
    }

    @RequestMapping("/accounts/toSignin")
    public String toLogin() {
        return "/user/accounts/signin";
    }

    @PostMapping("/user/signin")
    public String doSignin(String username, String password, String target, String autoLogin, HttpServletRequest request, HttpServletResponse response, ModelMap modelMap) {
        ResponseResult responseResult = userService.doSignin(username, password);
        User user = (User) responseResult.getData();
        if (user != null) {
            modelMap.put("loginUser", user);
            request.getSession().setAttribute(Constant.SESSION_KEY, user);
            if (StrUtil.equals(autoLogin, "1")) {
                CookieUtils.setCookie(Constant.LOGIN_COOKIE_KEY, String.join("-",user.getEmail(),user.getPasswd()),response);
            }
            return "redirect:/index";
        }
        return "redirect:/accounts/toSignin?errorMsg=" + responseResult.getMsg() + "&target=" + target;
    }

    /**
     * 退出登陆
     *
     * @return
     */
    @RequestMapping("/accounts/logout")
    public void logout(HttpServletRequest request) {
        request.getSession().invalidate();
    }

    @RequestMapping("/uploadPage")
    public String u(){
        return "/user/upload";
    }
    @PostMapping("/upload")
    @ResponseBody
    public ResponseResult upload(@RequestParam("file") MultipartFile file){
        String upload = fastDfsUtil.upload(file);
        return ResponseResult.success("上传成功", upload);
    }
    @RequestMapping("/accounts/profile")
    public String getProfile(ModelMap modelMap){
        modelMap.put("picServer", Constant.PIC_SERVER);
        return "/user/accounts/profile";
    }
}
