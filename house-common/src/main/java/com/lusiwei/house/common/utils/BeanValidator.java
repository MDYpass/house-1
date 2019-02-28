package com.lusiwei.house.common.utils;

import cn.hutool.core.map.MapUtil;
import com.google.common.collect.Lists;
import com.lusiwei.house.common.response.ResponseResult;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import java.util.*;

/**
 * 校验 参数bean  校验bean的属性
 * 可以校验多个bean 可以校验集合+多个bean集合必须写在第一位  校验多个集合请自己遍历
 * 1.单独的bean
 * 2.list<bean>
 *  3.bean1...bean2..bean3
 */
@Slf4j
public class BeanValidator {
    //生产校验工具的工厂类
    private static ValidatorFactory validatorFactory = Validation.buildDefaultValidatorFactory();
    //key 就是属性  value当前属性的问题  username ---- 不能为null
    //校验单个bean
    private static <T> Map<String,String> validateBean(T t,Class...classes){
        if(t==null){
            return Collections.emptyMap();//返回一个空的map
        }
        //用来接收错误数据
        Map<String,String> errors = new HashMap<>();
        //获得校验器
        Validator validator = validatorFactory.getValidator();
        //开始校验 会校验所有的属性 如果有问题的就会返回 通过这个方法 与bean的上面的注解就可以完成校验(反射)
        //把每个属性的校验结果放在ConstraintViolation
        Set<ConstraintViolation<T>> errorSet = validator.validate(t, classes);
        Iterator<ConstraintViolation<T>> iterator = errorSet.iterator();
        ConstraintViolation<T> constraintViolation;
        while(iterator.hasNext()){
            //获得里面的 属性以及属性的问题
           constraintViolation = iterator.next();
           //取出校验结果 放入map中  constraintViolation.getPropertyPath().toString()获得属性名  constraintViolation.getMessage()获得属性对应的问题描述
            errors.put(constraintViolation.getPropertyPath().toString(),constraintViolation.getMessage());
        }
        return errors;
    }
    //校验单个集合  list<BeanTest>
    private static <T> Map<String,String> validateList(Collection<T> collection){
        if(collection==null||collection.isEmpty()){
            return  Collections.emptyMap();
        }
        Iterator<T> iterator = collection.iterator();
        Map<String,String> errors;
        do{
            if(!iterator.hasNext()){
                return  Collections.emptyMap();//空map
            }
            T bean = iterator.next();//取出集合中的每个对象
            errors = validateBean(bean, new Class[0]);//只要有错误就跳出 errors就不会为空
        }while (errors.isEmpty());//errors.isEmpty 表示 map既不为null size也大于0
        return errors;
    }
    //代表第一个对象 ...可变参数可以传递任意多个对象
    private static  Map<String,String> validateObject(Object object,Object...objects){
        if(object==null && objects==null){
            return  Collections.emptyMap();
        }
        Map errorMap;
        if(objects==null || objects.length==0){//表示只传递了一个参数
            //1.这个参数是一个集合  2.这个参数是一个bean
            if(object instanceof Collection){
                errorMap  = validateList((Collection) object);
            }else {//单独的bean
                errorMap  = validateBean(object);
            }
        }else { //参数两种情况   bean1,bean2,bean3   list<bean> ,bean1,bean2,bean3
            errorMap  = validateList(Lists.asList(object, objects));
        }
        return errorMap;
    }
    public static ResponseResult check(Object object, Object...objects){
        Map<String, String> map = validateObject(object, objects);
        if(MapUtil.isNotEmpty(map)){
           log.info("参数校验错误:{}",map.toString());
            return ResponseResult.fail(map.toString());
        }
        return ResponseResult.success(map.toString());
    }
}
