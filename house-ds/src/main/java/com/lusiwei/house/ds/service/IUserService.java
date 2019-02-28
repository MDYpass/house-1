package com.lusiwei.house.ds.service;


import com.google.common.cache.Cache;
import com.lusiwei.house.common.pojo.User;
import com.lusiwei.house.common.response.ResponseResult;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface IUserService {
    Cache<String, String> getRegisterCache();

    List<User> queryAll();

    ResponseResult doRegister(User user, String confirmPasswd, MultipartFile multipartFile);


    ResponseResult doSignin(String username, String password);

    void active(String email);
}
