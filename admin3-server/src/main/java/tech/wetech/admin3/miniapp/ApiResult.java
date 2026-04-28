package tech.wetech.admin3.miniapp;

/**
 * 小程序API统一返回结果
 */
public class ApiResult<T> {

    private int code;
    private String msg;
    private T data;

    public ApiResult() {}

    public ApiResult(int code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 成功返回
     */
    public static <T> ApiResult<T> ok(T data) {
        return new ApiResult<>(0, "success", data);
    }

    /**
     * 成功返回（带消息）
     */
    public static <T> ApiResult<T> ok(String msg, T data) {
        return new ApiResult<>(0, msg, data);
    }

    /**
     * 成功返回（无数据）
     */
    public static <T> ApiResult<T> ok(String msg) {
        return new ApiResult<>(0, msg, null);
    }

    /**
     * 失败返回
     */
    public static <T> ApiResult<T> fail(String msg) {
        return new ApiResult<>(-1, msg, null);
    }

    /**
     * 失败返回（带错误码）
     */
    public static <T> ApiResult<T> fail(int code, String msg) {
        return new ApiResult<>(code, msg, null);
    }

    // Getters and Setters
    public int getCode() { return code; }
    public void setCode(int code) { this.code = code; }
    public String getMsg() { return msg; }
    public void setMsg(String msg) { this.msg = msg; }
    public T getData() { return data; }
    public void setData(T data) { this.data = data; }
}
