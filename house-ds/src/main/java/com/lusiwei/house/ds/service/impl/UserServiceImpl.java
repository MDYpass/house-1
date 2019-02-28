package com.lusiwei.house.ds.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.extra.mail.MailUtil;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.RemovalListener;
import com.google.common.cache.RemovalNotification;
import com.lusiwei.house.common.pojo.User;
import com.lusiwei.house.common.response.ResponseResult;
import com.lusiwei.house.common.utils.BeanValidator;
import com.lusiwei.house.common.utils.FileUploadUtils;
import com.lusiwei.house.ds.dao.UserMapper;
import com.lusiwei.house.ds.service.IUserService;
import com.lusiwei.house.ds.service.fastdfs.FastDfsUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class UserServiceImpl implements IUserService {
    private static final String SESSION_KEY = "SESSION_KEY";
    @Value("${project.domain}")
    private String projectDomain;
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private FileUploadUtils fileUploadUtils;
    private final Cache<String, String> registerCache= CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .removalListener(new RemovalListener<String,String>() {
                @Override
                public void onRemoval(RemovalNotification<String,String> removalNotification) {
                    userMapper.deleteUserByEmail(removalNotification.getValue());
                }
            })
            .build();
    @Autowired
    private FastDfsUtil fastDfsUtil;
    @Override
    public Cache<String, String> getRegisterCache() {
        return registerCache;
    }

    @Override
    public List<User> queryAll() {
        return userMapper.selectAll();
    }

    @Override
    public ResponseResult doRegister(User user, String confirmPasswd, MultipartFile multipartFile) {
        //参数校验
        ResponseResult check = BeanValidator.check(user);
        if (!check.isSuccess()) {
            return check;
        }
        //验证用户名,手机号,邮箱是否存在
        if (!checkUserName(user.getName())) {
            return ResponseResult.fail("用户名已存在");
        }
        if (!checkPhone(user)) {
            return ResponseResult.fail("手机号已存在");
        }
        if (!checkEmail(user.getEmail())) {
            return ResponseResult.fail("邮箱已存在");
        }
        if (!user.getPasswd().equals(confirmPasswd)) {
            return ResponseResult.fail("两次输入的密码不一致");
        }
        //发送邮箱
        sendEmail(user.getEmail());
        //上传头像
//        String avatar = fileUploadUtils.upload(multipartFile, "avatar");
        String avatar = fastDfsUtil.upload(multipartFile);
        user.setAvatar(avatar);
        user.setCreateTime(new Date());
        user.setEnable(false);
        user.setAgencyId(0);
        user.setPasswd(SecureUtil.md5(user.getPasswd()));
        userMapper.insert(user);
        return ResponseResult.success(user.getEmail());
    }

    @Override
    public ResponseResult<User> doSignin(String email, String password) {
        //判断用户是否存在，true表示不存在
        if (checkEmail(email)) {
            ResponseResult.fail("你登录的用户名不存在");
        }
        User user=userMapper.selectByEmailAndPassword(email, SecureUtil.md5(password));
        if (ObjectUtil.isNull(user)) {
            ResponseResult.fail("密码错误");
        }

        return ResponseResult.success("登陆成功",user);
    }

    @Override
    public void active(String email) {
        userMapper.updateStatus(email);
    }

    /**
     * 校验用户名
     * @return true 表示不存在，校验通过
     */
    private boolean checkUserName(String username) {
        int i=userMapper.selectByUserName(username);
        return i <= 0;
    }
    private boolean checkPhone(User user){
        int i=userMapper.selectByPhone(user.getPhone());
        return i <= 0;
    }
    private boolean checkEmail(String email){
        int i=userMapper.selectByEmail(email);
        return i <= 0;
    }
    @Async
    public void sendEmail(String email){
        String s = IdUtil.randomUUID();
        registerCache.put(s,email);
        String content="http://"+projectDomain+"/active/"+s;
        MailUtil.send(email,"house注册验证邮件:",content,true);
    }

}
