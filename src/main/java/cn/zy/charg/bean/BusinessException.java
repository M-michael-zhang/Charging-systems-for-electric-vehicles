package cn.zy.charg.bean;


public class BusinessException extends RuntimeException {

    private int code;
    private boolean isShowMsg = true;

    /**
     * 使用枚举传参
     *
     * @param errorCode 异常枚举
     */
    public BusinessException(BusinessErrorCode errorCode) {
        super(errorCode.getMessage());
        this.code = errorCode.getCode();
    }

    /**
     * 使用自定义消息
     *
     * @param code 值
     * @param msg 详情
     */
    public BusinessException(int code, String msg) {
        super(msg);
        this.code = code;
    }

    public int getCode(){
        return this.code;
    }
}