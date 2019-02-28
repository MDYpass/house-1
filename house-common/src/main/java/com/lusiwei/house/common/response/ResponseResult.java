package com.lusiwei.house.common.response;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.io.Serializable;

/**
 * @Author: lusiwei
 * @Date: 2018/11/24 15:08
 * @Description:
 */
public class ResponseResult<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    ResponseResult() {

    }

    private ResponseResult(int status) {
        this.status = status;
    }

    private ResponseResult(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    private ResponseResult(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ResponseResult(int status, T data) {
        this.status = status;
        this.data = data;
    }

    public int getStatus() {
        return status;
    }

    public String getMsg() {
        return msg;
    }

    public T getData() {
        return data;
    }
    @JsonIgnore
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
    }

    public static <T> ResponseResult<T> success() {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ResponseResult<T> success(String msg) {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ResponseResult<T> success(T data) {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ResponseResult<T> success(String msg, T data) {
        return new ResponseResult<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ResponseResult<T> fail() {
        return new ResponseResult<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ResponseResult<T> fail(String errorMessage) {
        return new ResponseResult<>(ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> ResponseResult<T> fail(int errorCode, String errorMessage) {
        return new ResponseResult<>(errorCode, errorMessage);
    }


}
