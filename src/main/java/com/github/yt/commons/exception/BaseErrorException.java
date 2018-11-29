package com.github.yt.commons.exception;

/**
 * Created with IntelliJ IDEA.
 * 以下我们对是“意外事件”和“错误”的理解
 * <p>
 * <p>
 * 异常条件	                        意外事件	                    错误
 * <p>
 * 认为是（Is considered to be）	    设计的一部分	                难以应付的意外
 * 预期发生（Is expected to happen）	有规律但很少发生	            从不
 * 谁来处理（Who cares about it）	    调用方法的上游代码	            需要修复此问题的人员
 * 实例（Examples）	                另一种返回模式	                编程缺陷，硬件故障，配置错误，文件丢失，服务器无法使用
 * 最佳映射（Best Mapping）	        已检查异常	                未检查异常
 *
 * @author liujiasheng
 */
public class BaseErrorException extends RuntimeException implements BaseException {

    private static final long serialVersionUID = 8686960428281101221L;

    /**
     * errorCode
     */
    private Object errorCode;

    /**
     * error结果集
     */
    private Object errorResult;

    public BaseErrorException(int errorCode, String errorMsg, Object... params) {
        this((Object) errorCode, errorMsg, params);
    }

    public BaseErrorException(String errorCode, String errorMsg, Object... params) {
        this((Object) errorCode, errorMsg, params);
    }

    private BaseErrorException(Object errorCode, String errorMsg, Object... params) {
        super(ExceptionUtils.getExceptionMessage(errorCode, errorMsg, params));
        this.errorCode = errorCode;
    }

    public BaseErrorException(String errorMsg, Exception e) {
        super(errorMsg, e);
    }

    public BaseErrorException(Enum errorEnum, Object... params) {
        super(ExceptionUtils.getExceptionMessage(errorEnum, params));
        errorCode = errorEnum.name();
    }

    public BaseErrorException(Enum errorEnum, Exception e, Object... params) {
        super(ExceptionUtils.getExceptionMessage(errorEnum, params), e);
        errorCode = errorEnum.name();
    }

    public BaseErrorException(Object errorResult, Enum errorEnum, Object... params) {
        super(ExceptionUtils.getExceptionMessage(errorEnum, params));
        errorCode = errorEnum.name();
        this.errorResult = errorResult;
    }

    public BaseErrorException(Object errorResult, Enum errorEnum, Exception e, Object... params) {
        super(ExceptionUtils.getExceptionMessage(errorEnum, params), e);
        errorCode = errorEnum.name();
        this.errorResult = errorResult;
    }

    @Override
    public Object getErrorResult() {
        return errorResult;
    }

    @Override
    public Object getErrorCode() {
        return errorCode;
    }

}
