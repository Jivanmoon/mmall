package com.mmall.common;

/**
 * Created by Jivan on 2020/1/23.
 */
public enum ResponseCode {
    SUCCESS(0, "SUCCESS"),
    ERROR(1, "ERROR"),
    NEED_LOGINLIN(10, "NEED_LOGINLIN"),
    ILLEAGLE_ARGUEMENT(20, "ILLEAGLE_ARGUEMENT");

    private final int code;
    private final String desc;

    ResponseCode(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
