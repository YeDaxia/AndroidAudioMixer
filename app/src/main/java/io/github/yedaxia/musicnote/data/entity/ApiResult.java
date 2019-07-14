package io.github.yedaxia.musicnote.data.entity;

/**
 * @author Darcy https://yedaxia.github.io/
 * @version 2018/12/19.
 */

public class ApiResult<T> {

    private int code;
    private String msg;
    private T data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
