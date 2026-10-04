package com.example.demo.common;

import java.util.HashMap;

public class ResultUtils extends HashMap<String, Object> {

    public ResultUtils() {
    }

    public ResultUtils(int code, String msg, Object data) {
        put("code", code);
        put("msg", msg);
        put("data", data);
    }

    public static ResultUtils success() {
        return new ResultUtils(200, "success", null);
    }

    public static ResultUtils success(Object data) {
        return new ResultUtils(200, "success", data);
    }

    public static ResultUtils success(String msg, Object data) {
        return new ResultUtils(200, msg, data);
    }

    public static ResultUtils successMsg(String msg) {
        return new ResultUtils(200, msg, null);
    }

    public static ResultUtils error(String msg) {
        return new ResultUtils(500, msg, null);
    }

    public static ResultUtils error(int code, String msg) {
        return new ResultUtils(code, msg, null);
    }

    @Override
    public ResultUtils put(String key, Object value) {
        super.put(key, value);
        return this;
    }
}