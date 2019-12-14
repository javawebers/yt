package com.github.yt.web.query;

import com.github.yt.commons.query.PageQuery;
import com.github.yt.web.YtWebConfig;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * 分页增强
 * 支持可配置分页页码和每页条数
 * 指定如下两个参数
 * 默认是pageNo和pageSize
 * yt.page.pageNoName
 * yt.page.pageSizeName
 *
 * @author liujiasheng
 */
@Aspect
@Order(1000)
@Component
public class QueryControllerAspect {

    private static Set<Method> methodSet = new HashSet<>();
    private static Map<Method, Integer> queryMethodMap = new HashMap<>();

    @Pointcut("@annotation(org.springframework.web.bind.annotation.RequestMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.GetMapping) || " +
            "@annotation(org.springframework.web.bind.annotation.PostMapping)")
    public void allController() {
    }

    @Around(value = "allController() ")
    public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
        try {
            MethodSignature methodSignature = (MethodSignature) proceedingJoinPoint.getSignature();
            Object target = proceedingJoinPoint.getTarget();
            // 获取实现类的方法
            Method currentMethod = target.getClass().getMethod(methodSignature.getName(), methodSignature.getParameterTypes());
            // 每个方法只遍历一次
            if (!methodSet.contains(currentMethod)) {
                methodSet.add(currentMethod);
                Class[] classes = methodSignature.getParameterTypes();
                for (int i = 0; i < classes.length; i++) {
                    if (PageQuery.class.isAssignableFrom(classes[i])) {
                        queryMethodMap.put(currentMethod, i);
                    }
                }
            }
            if (queryMethodMap.containsKey(currentMethod)) {
                RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
                HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
                String pageNoStr = request.getParameter(YtWebConfig.pageNoName);
                String pageSizeStr = request.getParameter(YtWebConfig.pageSizeName);
                PageQuery pageQuery = (PageQuery) proceedingJoinPoint.getArgs()[queryMethodMap.get(currentMethod)];
                int pageSizeNum;
                try {
                    pageSizeNum = (pageSizeStr == null || pageSizeStr.isEmpty()) ? 10 : Integer.valueOf(pageSizeStr);
                } catch (NumberFormatException e) {
                    pageSizeNum = 10;
                }
                int pageNoNum;
                try {
                    pageNoNum = (pageNoStr == null || pageNoStr.isEmpty()) ? 1 : Integer.valueOf(pageNoStr);
                } catch (NumberFormatException e) {
                    pageNoNum = 1;
                }
                pageQuery.makePageNo(pageNoNum);
                pageQuery.makePageSize(pageSizeNum);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proceedingJoinPoint.proceed();
    }
}
