package com.lusiwei.house.web.controller;

import com.lusiwei.house.common.pojo.User;
import com.lusiwei.house.ds.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

@Controller
public class UserController {
    @Autowired
    private IUserService userService;
    @RequestMapping("query")
    @ResponseBody
    public List<User> queryAll(){
        return userService.queryAll();
    }

    @RequestMapping("freemarker")
    public String test(ModelMap modelMap){
        modelMap.put("key", userService.queryAll().get(0));
        return "test";
    }
    @RequestMapping("index")
    public String index(){
        return "homepage/index";
    }
}
