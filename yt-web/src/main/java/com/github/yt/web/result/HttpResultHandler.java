package com.github.yt.web.result;

import com.github.yt.commons.exception.BaseException;
import com.github.yt.commons.util.YtStringUtils;
import com.github.yt.web.YtWebConfig;
import com.github.yt.web.util.JsonUtils;
import com.github.yt.web.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Base64;
import java.util.Date;
import java.util.Objects;

import static com.github.yt.web.result.PackageResponseBodyAdvice.REQUEST_RESULT_ENTITY;

/**
 * http请求返回实体处理类
 *
 * @author liujiasheng
 */
public class HttpResultHandler {
    private static final Logger logger = LoggerFactory.getLogger(HttpResultHandler.class);

    public static final String HEADER_BUSINESS_EXCEPTION_RESULT_BODY = "Business-Exception-Result-Body";
    public static final String HEADER_BUSINESS_OCCUR_EXCEPTION = "Business-Occur-Exception";

    private static volatile BaseResultConfig resultConfig;

    public static BaseResultConfig getResultConfig() {
        if (resultConfig == null) {
            synchronized (HttpResultHandler.class) {
                try {
                    YtWebConfig ytWebConfig = SpringContextUtils.getBean(YtWebConfig.class);
                    resultConfig = ytWebConfig.getResult().getResultConfigClass().newInstance();
                } catch (InstantiationException | IllegalAccessException e) {
                    throw new RuntimeException("实例化 BaseResultConfig 类异常", e);
                }
            }
        }
        return resultConfig;
    }

    public static HttpResultEntity getSuccessSimpleResultBody() {
        return getSuccessSimpleResultBody(null);
    }

    public static HttpResultEntity getSuccessSimpleResultBody(Object result) {
        return getSuccessMoreResultBody(result, false, null);
    }

    public static HttpResultEntity getSuccessMoreResultBody(Object result, Object moreResult) {
        return getSuccessMoreResultBody(result, true, moreResult);
    }

    /**
     * 请求成功封装返回对象
     *
     * @param result     结果
     * @param withMore   是否有更多结果
     * @param moreResult 更多结果
     * @return HttpResultEntity
     */
    private static HttpResultEntity getSuccessMoreResultBody(Object result, boolean withMore,
            Object moreResult) {
        HttpResultEntity resultBody = new HttpResultEntity();
        resultBody.put(getResultConfig().getErrorCodeField(),
                getResultConfig().getDefaultSuccessCode());
        resultBody.put(getResultConfig().getMessageField(),
                getResultConfig().getDefaultSuccessMessage());
        resultBody.put(getResultConfig().getResultField(), result);
        setExpandField(resultBody);
        if (withMore) {
            resultBody.put(getResultConfig().getMoreResultField(), moreResult);
        }
        return resultBody;
    }

    /**
     * 设置 response 到 header 中，方便 feign 调用进行统一判断
     *
     * @param resultBody 返回的 map 对象
     * @param response   response
     */
    public static void setResponseToHeader(HttpResultEntity resultBody,
            HttpServletResponse response, Throwable exception) {
        YtWebConfig ytWebConfig = SpringContextUtils.getBean(YtWebConfig.class);
        boolean setResponseToHeader = ytWebConfig.getResult().isSetResponseToHeader();
        if (setResponseToHeader) {
            HttpResultEntity headerResultBody = new HttpResultEntity();
            headerResultBody.put(getResultConfig().getErrorCodeField(),
                    resultBody.get(getResultConfig().getErrorCodeField()));
            headerResultBody.put(getResultConfig().getMessageField(),
                    resultBody.get(getResultConfig().getMessageField()));
            headerResultBody.put(getResultConfig().getResultField(),
                    resultBody.get(getResultConfig().getResultField()));
            setExpandField(headerResultBody);

            boolean setStackTraceToHeader = ytWebConfig.getResult().isSetStackTraceToHeader();
            String stackTraceField = getResultConfig().getStackTraceField();
            if (setStackTraceToHeader) {
                headerResultBody.put(stackTraceField, getAndSetExceptionStrToRequest(exception));
            }

            byte[] headerResultBodyByte = Base64.getEncoder()
                    .encode(JsonUtils.toJsonString(headerResultBody).getBytes());
            response.addHeader(HEADER_BUSINESS_EXCEPTION_RESULT_BODY, new String(headerResultBodyByte));
            response.addHeader(HEADER_BUSINESS_OCCUR_EXCEPTION, "true");
        }

    }

    /**
     * 请求失败封装返回对象
     *
     * @param exception 异常
     * @return HttpResultEntity
     */
    public static HttpResultEntity getErrorSimpleResultBody(Throwable exception) {
        HttpResultEntity resultBody = new HttpResultEntity();
        if (exception instanceof BaseException) {
            BaseException baseException = (BaseException) exception;
            resultBody.put(getResultConfig().getErrorCodeField(),
                    getResultConfig().convertErrorCode(baseException.getErrorCode()));
            resultBody.put(getResultConfig().getMessageField(), exception.getMessage());
            resultBody.put(getResultConfig().getResultField(), baseException.getErrorResult());
        } else {
            resultBody.put(getResultConfig().getErrorCodeField(),
                    getResultConfig().getDefaultErrorCode());
            resultBody.put(getResultConfig().getMessageField(),
                    getResultConfig().getDefaultErrorMessage());
            resultBody.put(getResultConfig().getResultField(), null);
        }
        setExpandField(resultBody);

        // 返回异常堆栈到前端
        YtWebConfig ytWebConfig = SpringContextUtils.getBean(YtWebConfig.class);
        String stackTraceField = getResultConfig().getStackTraceField();
        if (ytWebConfig.getResult().isReturnStackTrace() && YtStringUtils
                .isNotBlank(stackTraceField)) {
            resultBody.put(stackTraceField, getAndSetExceptionStrToRequest(exception));
        }
        return resultBody;
    }

    /**
     * 从 request 中获取 异常栈 信息，如果没有则设置到 request 中，如果存在则直接返回
     *
     * @param exception 异常
     * @return 异常栈
     */
    public static String getAndSetExceptionStrToRequest(Throwable exception) {
        final String REQUEST_EXCEPTION_STR = "__REQUEST_EXCEPTION_STR__";
        HttpServletRequest request = getRequest();
        String exceptionStr = (String) request.getAttribute(REQUEST_EXCEPTION_STR);
        if (exceptionStr == null) {
            StringWriter stringWriter = new StringWriter();
            exception.printStackTrace(new PrintWriter(stringWriter, true));
            exceptionStr = stringWriter.getBuffer().toString();
            request.setAttribute(REQUEST_EXCEPTION_STR, exceptionStr);
        }
        return exceptionStr;
    }

    /**
     * 设置扩展字段
     * 包含 uuid、请求时间、响应时间
     *
     * @param resultBody HttpResultEntity
     */
    private static void setExpandField(HttpResultEntity resultBody) {
        String uuidField = getResultConfig().getUuidField();
        if (YtStringUtils.isNotBlank(uuidField)) {
            resultBody.put(uuidField,
                    getRequest().getAttribute(RequestHandlerInterceptor.REQUEST_UUID));
        }
        String requestTimeField = getResultConfig().getRequestTimeField();
        if (YtStringUtils.isNotBlank(requestTimeField)) {
            resultBody.put(requestTimeField,
                    getRequest().getAttribute(RequestHandlerInterceptor.REQUEST_TIME));
        }
        String responseTimeField = getResultConfig().getResponseTimeField();
        if (YtStringUtils.isNotBlank(responseTimeField)) {
            resultBody.put(responseTimeField, new Date());
        }
    }

    public static void writeExceptionResult(final Throwable e, HttpServletRequest request,
            HttpServletResponse response) {
        // 当不向上抛异常时主动打印异常
        logger.error(e.getMessage(), e);
        HttpResultEntity resultBody = HttpResultHandler.getErrorSimpleResultBody(e);
        YtWebConfig ytWebConfig = SpringContextUtils.getBean(YtWebConfig.class);
        response.setStatus(ytWebConfig.getResult().getErrorState());
        response.addHeader("Content-type", "application/json;charset=UTF-8");
        request.setAttribute(REQUEST_RESULT_ENTITY, resultBody);
        String result = JsonUtils.toJsonString(resultBody);
        try {
            response.getWriter().write(result);
        } catch (IOException ioException) {
            throw new RuntimeException(ioException);
        }
    }

    private static HttpServletRequest getRequest() {
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        return ((ServletRequestAttributes) Objects.requireNonNull(requestAttributes)).getRequest();
    }

}
