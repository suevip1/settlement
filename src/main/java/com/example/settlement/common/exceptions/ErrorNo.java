package com.example.settlement.common.exceptions;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 错误码基类
 * @author yangwu_i
 * @date 2023/4/26 22:19
 */
@Data
@AllArgsConstructor
public class ErrorNo {
    public static final ErrorNo SUCCESS = new ErrorNo(200, "成功");
    public static final ErrorNo SERVER_ERROR = new ErrorNo(500, "服务器错误");
    public static final ErrorNo DB_UPDATE_ERROR = new ErrorNo(602, "数据库更新失败");
    private int errorNo;
    private String errorMsg;

    public boolean isSuccess() {
        return errorNo >= 200 && errorNo < 300;
    }
}
