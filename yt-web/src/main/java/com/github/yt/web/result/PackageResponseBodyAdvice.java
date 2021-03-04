package com.github.yt.web.result;

import com.github.yt.commons.exception.BaseException;
import com.github.yt.commons.exception.BaseExceptionConverter;
import com.github.yt.commons.query.IPage;
import com.github.yt.web.conf.YtWebProperties;
import com.github.yt.web.util.JsonUtils;
import com.github.yt.web.util.SpringContextUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.util.*;


/**
 * 1.返回体拦截器 实现ResponseBodyAdvice接口的supports和beforeBodyWrite方法
 * 2.异常拦截器 @ExceptionHandler作用的handleExceptions方法
 * <p>
 * ApplicationContextAware 的作用是可以获取spring管理的bean
 * <p>
 * 正常返回和异常返回分别被该类处理
 * 拦截返回结果或者异常包装成HttpResultEntity
 *
 * @author liujiasheng
 */
@Order(200)
@ControllerAdvice
public class PackageResponseBodyAdvice implements ResponseBodyAdvice<Object>, ApplicationContextAware {
    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    public static final String REQUEST_EXCEPTION = "yt:request_exception";
    public static final String REQUEST_RESULT_ENTITY = "yt:request_result_entity";
    public static final String REQUEST_BEFORE_BODY_WRITE = "yt:request_before_body_write";

    private ApplicationContext applicationContext;

    private final YtWebProperties ytWebProperties;

    private ArrayList<Class<?>> ignorePackageResultTypeList;

    private ArrayList<String> ignorePackageStartsWithList;

    public PackageResponseBodyAdvice(YtWebProperties ytWebProperties) {
        this.ytWebProperties = ytWebProperties;
    }

    private ArrayList<String> getIgnorePackageStartsWithList() {
        if (ignorePackageStartsWithList == null) {
            synchronized (this) {
                ignorePackageStartsWithList = new ArrayList<>(16);
                ignorePackageStartsWithList.add("/actuator");
            }
        }
        return ignorePackageStartsWithList;
    }


    private ArrayList<Class<?>> getIgnorePackageResultTypes() {
        if (ignorePackageResultTypeList == null) {
            synchronized (this) {
                Class<?>[] ignorePackageResultTypes = ytWebProperties.getResult().getIgnorePackageResultTypes();
                if (ignorePackageResultTypes != null && ignorePackageResultTypes.length != 0) {
                    ignorePackageResultTypeList = new ArrayList<>(ignorePackageResultTypes.length + 1);
                    ignorePackageResultTypeList.addAll(Arrays.asList(ignorePackageResultTypes));
                } else {
                    ignorePackageResultTypeList = new ArrayList<>(1);
                }
                ignorePackageResultTypeList.add(ResponseEntity.class);
            }
        }
        return ignorePackageResultTypeList;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }


    /**
     * 405 异常直接抛出
     *
     * @param e e
     * @throws Throwable e
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    @PackageResponseBody(false)
    public void handleExceptions405(final Throwable e) throws Throwable {
        throw e;
    }

    /**
     * 全局异常处理类
     * <p>
     * 配合文件上传文件最大限制时需要同时配置 spring.servlet.multipart.resolve-lazily。如下：
     * spring.servlet.multipart.max-file-size=1KB
     * spring.servlet.multipart.resolve-lazily=true
     *
     * @param e             异常类
     * @param handlerMethod controller 方法
     * @param request       request
     * @param response      response
     * @return HttpResultEntity
     * @throws Exception 不进行处理的异常重新抛出
     */
    @ExceptionHandler
    @PackageResponseBody(false)
    public HttpResultEntity handleExceptions(final Throwable e, HandlerMethod handlerMethod,
                                             HttpServletRequest request, HttpServletResponse response) throws Throwable {
        if (!exceptionPackageResponseBody(request, handlerMethod.getBeanType(), handlerMethod.getMethod())) {
            // 不需要包装时直接返回异常对象
            request.setAttribute(REQUEST_EXCEPTION, e);
            throw e;
        }
        Throwable se = convertToKnownException(e);
        request.setAttribute(REQUEST_EXCEPTION, se);
        Object beforeBodyWrite = request.getAttribute(REQUEST_BEFORE_BODY_WRITE);
        if (beforeBodyWrite != null) {
            throw se;
        }

        // 返回包装体
        logger.error(se.getMessage(), se);
        HttpResultEntity resultBody = HttpResultHandler.getErrorSimpleResultBody(se);
        HttpResultHandler.setResponseToHeader(resultBody, response, se);
        YtWebProperties ytWebProperties = SpringContextUtils.getBean(YtWebProperties.class);
        response.setStatus(ytWebProperties.getResult().getErrorState());
        response.addHeader("Content-Type", "application/json;charset=UTF-8");
        request.setAttribute(REQUEST_RESULT_ENTITY, resultBody);
        return resultBody;
    }


    @Override
    public boolean supports(MethodParameter returnType, Class<? extends HttpMessageConverter<?>> converterType) {
        return true;
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType selectedContentType,
                                  Class<? extends HttpMessageConverter<?>> selectedConverterType,
                                  ServerHttpRequest serverHttpRequest, ServerHttpResponse serverHttpResponse) {
        HttpServletRequest request = ((ServletServerHttpRequest) serverHttpRequest).getServletRequest();

        // 如果返回的对象是 Page 类型，将对象转换成 map ，并设置 配置中的属性
        YtWebProperties.Page pageConfig = ytWebProperties.getPage();
        if (pageConfig.isConvertPage() && body instanceof IPage) {
            IPage<?> page = (IPage<?>) body;
            LinkedHashMap<Object, Object> pageResultEntity = new LinkedHashMap<>();
            body = pageResultEntity;
            pageResultEntity.put(pageConfig.getPageNoName(), page.getPageNo());
            pageResultEntity.put(pageConfig.getPageSizeName(), page.getPageSize());
            pageResultEntity.put(pageConfig.getPageTotalCountName(), page.getTotalCount());
            pageResultEntity.put(pageConfig.getPageDataName(), page.getData());
        }

        request.setAttribute(REQUEST_RESULT_ENTITY, body);

        if (!successPackageResponseBody(request, returnType.getContainingClass(), Objects.requireNonNull(returnType.getMethod()))) {
            return body;
        }

        HttpResultEntity resultBody;
        // 返回的实体类是 HttpResultEntity 时，正常返回就不需要包装了
        if (HttpResultEntity.class.isAssignableFrom(returnType.getMethod().getReturnType())) {
            resultBody = (HttpResultEntity) body;
        } else {
            resultBody = HttpResultHandler.getSuccessSimpleResultBody(body);
        }
        request.setAttribute(REQUEST_RESULT_ENTITY, resultBody);
        request.setAttribute(REQUEST_BEFORE_BODY_WRITE, new Object());
        serverHttpResponse.setStatusCode(HttpStatus.OK);
        serverHttpResponse.getHeaders().add("Content-Type", "application/json;charset=UTF-8");
        if (body instanceof String || String.class.equals(returnType.getMethod().getReturnType())) {
            return JsonUtils.toJsonString(resultBody);
        }
        return resultBody;
    }


    /**
     * 将异常转换为BaseException
     */
    private Throwable convertToKnownException(Throwable e) {
        if (e instanceof BaseException) {
            return e;
        }
        Map<String, BaseExceptionConverter> exceptionConverterMap = applicationContext.getBeansOfType(BaseExceptionConverter.class);
        for (BaseExceptionConverter baseExceptionConverter : exceptionConverterMap.values()) {
            Throwable knownException = baseExceptionConverter.convertToBaseException(e);
            if (knownException instanceof BaseException) {
                return knownException;
            }
        }
        return e;
    }

    /**
     * 排除目录
     * @param path 请求的 url
     * @return 是否排除包装
     */
    private boolean ignorePath(String path) {
        ArrayList<String> ignorePackageStartsWithList = getIgnorePackageStartsWithList();
        for (String ignorePackageStartsWith : ignorePackageStartsWithList) {
            if (path.startsWith(ignorePackageStartsWith)) {
                return true;
            }
        }
        return false;
    }

    private boolean exceptionPackageResponseBody(HttpServletRequest request, Class<?> beanType, Method method) {
        String path = request.getServletPath();
        if (ignorePath(path)) {
            return false;
        }
        // 返回的实体类是 HttpResultEntity 时，抛出异常也需要包装
        if (HttpResultEntity.class.isAssignableFrom(method.getReturnType())) {
            return true;
        }
        if (ytWebProperties.getResult().isAlwaysPackageException()) {
            return true;
        }
        for (Class<?> ignorePackageResultType : getIgnorePackageResultTypes()) {
            if (ignorePackageResultType.isAssignableFrom(method.getReturnType())) {
                return false;
            }
        }
        return packageResponseBody(beanType, method);
    }

    private boolean successPackageResponseBody(HttpServletRequest request, Class<?> beanType, Method method) {
        String path = request.getServletPath();
        if (ignorePath(path)) {
            return false;
        }
        for (Class<?> ignorePackageResultType : getIgnorePackageResultTypes()) {
            if (ignorePackageResultType.isAssignableFrom(method.getReturnType())) {
                return false;
            }
        }
        return packageResponseBody(beanType, method);
    }

    /**
     * 通过全局配置或者注解判断是否包装返回体
     * @param beanType 对象类型，这里不能从 method 中获取对象类型。
     *                 beanType 传入子类类型
     *                 当对象存在继承的情况，方法没有重新实现，从 method 中获取的对象类型是父类的类型，无法获取到子类中的注解。
     * @param method 方法
     * @return 是否进行包装
     */
    private boolean packageResponseBody(Class<?> beanType, Method method) {
        PackageResponseBody methodPackageResponseBody = method.getAnnotation(PackageResponseBody.class);
        PackageResponseBody classPackageResponseBody = AnnotationUtils.findAnnotation(beanType, PackageResponseBody.class);
        if (methodPackageResponseBody != null) {
            // 判断方法配置(默认true)
            return methodPackageResponseBody.value();
        } else {
            // 判断全局配置(默认true)
            if (classPackageResponseBody != null) {
                // 判断类配置(默认true)
                return classPackageResponseBody.value();
            } else {
                return ytWebProperties.getResult().isPackageResponseBody();
            }
        }
    }
}
