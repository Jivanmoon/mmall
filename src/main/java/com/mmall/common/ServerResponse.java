package com.mmall.common;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.annotate.JsonSerialize;

import java.io.Serializable;

/**
 * Created by Jivan on 2020/1/23.
 */
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
//保证序列化json的时候，如果是null对象，key也会消失
public class ServerResponse<T> implements Serializable {
    private int status;
    private String msg;
    private T data;

    private ServerResponse(int status) {
        this.status = status;
    }

    private ServerResponse(int status, T data) {
        this.status = status;
        this.data = data;
    }

    private ServerResponse(int status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    private ServerResponse(int status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    @JsonIgnore
    //使之不在json序列化当中
    public boolean isSuccess() {
        return this.status == ResponseCode.SUCCESS.getCode();
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

    public static <T> ServerResponse<T> creatBySuccess() {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode());
    }

    public static <T> ServerResponse<T> creatBySuccessMessage(String msg) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg);
    }

    public static <T> ServerResponse<T> creatBySuccess(T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), data);
    }

    public static <T> ServerResponse<T> creatBySuccess(String msg, T data) {
        return new ServerResponse<>(ResponseCode.SUCCESS.getCode(), msg, data);
    }

    public static <T> ServerResponse<T> creatByError() {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), ResponseCode.ERROR.getDesc());
    }

    public static <T> ServerResponse<T> creatByErrorMessage(String errorMessage) {
        return new ServerResponse<>(ResponseCode.ERROR.getCode(), errorMessage);
    }

    public static <T> ServerResponse<T> creatByErrorCodeMessage(int errorCode, String errorMessage) {
        return new ServerResponse<>(errorCode, errorMessage);
    }

}
