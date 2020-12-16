package com.github.yt.web.result;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.UUID;

/**
 * 打印请求 uuid
 *
 * @author 刘加胜
 */
public class RequestHandlerInterceptor implements HandlerInterceptor {

    private final Logger logger = LoggerFactory.getLogger(RequestHandlerInterceptor.class);

    public static final String REQUEST_UUID = "__REQUEST_UUID__";
    public static final String REQUEST_TIME = "__REQUEST_TIME__";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        request.setAttribute(REQUEST_UUID, UUID.randomUUID().toString().replace("-", ""));
        request.setAttribute(REQUEST_TIME, new Date());
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) {

    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        if (logger.isDebugEnabled()) {
            logger.debug((String) request.getAttribute(REQUEST_UUID));
        }
    }
}
