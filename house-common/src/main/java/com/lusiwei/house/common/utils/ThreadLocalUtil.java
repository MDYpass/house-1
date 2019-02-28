package com.lusiwei.house.common.utils;

import com.lusiwei.house.common.pojo.User;

public class ThreadLocalUtil {
    private static ThreadLocal<User> userThreadLocal = new ThreadLocal<>();

    public static void set(User user) {
        userThreadLocal.set(user);
    }

    public static User get() {
        return userThreadLocal.get();
    }
    public static void remove(){
        userThreadLocal.remove();
    }
}
