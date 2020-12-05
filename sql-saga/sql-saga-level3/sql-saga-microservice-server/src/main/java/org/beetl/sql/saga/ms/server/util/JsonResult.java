package org.beetl.sql.saga.ms.server.util;

import lombok.Data;

@Data
public class JsonResult<T> {

    private boolean success;
    private String msg;
    private T data;
    private int errorCode = 0;


    public static <T>  JsonResult<T> fail() {
         JsonResult<T> ret = new  JsonResult<T>();
        ret.setSuccess(false);
        ret.setMsg( JsonReturnCode.FAIL.getDesc());
        return ret;
    }

    public static <T>  JsonResult<T> fail(T data) {
         JsonResult<T> ret =  JsonResult.fail();
        ret.setData(data);
        return ret;
    }

    public static <T>  JsonResult<T> failMessage(String msg,int code) {
         JsonResult fail = failMessage(msg);
        fail.setErrorCode(code);
        return fail;
    }

    public static <T>  JsonResult<T> failMessage(String msg,int code,T data) {
         JsonResult fail = failMessage(msg);
        fail.setErrorCode(code);
        fail.setData(data);
        return fail;
    }

    public static <T>  JsonResult<T> failMessage(String msg) {
         JsonResult<T> ret =  JsonResult.fail();
        ret.setMsg(msg);
        return ret;
    }
    public static <T>  JsonResult<T> successMessage(String msg) {
         JsonResult<T> ret =  JsonResult.success();
        ret.setMsg(msg);
        return ret;
    }



    public static <T>  JsonResult<T> success() {
         JsonResult<T> ret = new  JsonResult<T>();
        ret.setSuccess(true);
        ret.setMsg( JsonReturnCode.SUCCESS.getDesc());
        return ret;
    }



    public static <T>  JsonResult<T> success(T data) {
         JsonResult<T> ret =  JsonResult.success();
        ret.setData(data);
        return ret;
    }




}