package com.lusiwei.house.ds.service.impl;

import com.lusiwei.house.common.pojo.User;
import com.lusiwei.house.ds.mapper.UserMapper;
import com.lusiwei.house.ds.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService implements IUserService {
    @Autowired
    private UserMapper userMapper;
    @Override
    public List<User> queryAll(){
        return userMapper.queryAll();
    }
}
