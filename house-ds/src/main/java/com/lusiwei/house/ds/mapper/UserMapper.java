package com.lusiwei.house.ds.mapper;

import com.lusiwei.house.common.pojo.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface UserMapper {
    List<User> queryAll();
}
