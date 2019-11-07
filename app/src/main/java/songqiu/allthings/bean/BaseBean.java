package songqiu.allthings.bean;

/**
 * 实体数据基类
 * Created by cc on 2018/5/3.
 */

public class BaseBean<T> {

    /**
     * success : true/false
     * msg : 成功/失败
     * data :需要使用的数据
     */
    private boolean status;
    private boolean success;
    private String msg;
    private T data;
    private String message;

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
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
