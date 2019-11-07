package songqiu.allthings.api;

/**
 * 自定义异常处理
 * 用于Gson解析
 * Created by cc on 2018/5/3.
 */

public class ApiException extends RuntimeException {
    private int Code  = 0;
    public ApiException(String errorMessage ){
        super(errorMessage);
    }
    public int getCode(){return Code;}
    public void setCode(int code){Code = code;}
}
