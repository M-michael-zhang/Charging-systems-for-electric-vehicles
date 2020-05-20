package cn.zy.charg.bean;


public enum BusinessErrorCode {
    /**
     * 权限异常
     */
    NOT_PERMISSIONS(204,"您没有操作权限"),
    DB_UPDATEFAIL(201,"数据库更新失败"),
    DB_INSERTFAIL(202,"插入失败"),
    DB_ERROR(203,"数据库操作失败"),
    DB_EMPTYRESULT(205,"数据库查询结果为空"),
    DB_DELETEFAIL(206,"删除记录失败"),
    USER_LOGINFAIL(301,"登录失败"),
    ERROR_DEFAULT(200,"系统出错"),
    FORMAT_FAULT(302,"格式转换失败");

    private int code;

    private String message;

    BusinessErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public BusinessErrorCode setNewMessage(String message){
        this.setMessage(message);
        return this;
    }
}