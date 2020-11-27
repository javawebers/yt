package com.github.yt.web.result;

import com.github.yt.commons.exception.BaseExceptionConverter;
import com.github.yt.commons.exception.BusinessException;
import com.github.yt.web.YtWebExceptionEnum;
import org.springframework.stereotype.Component;

/**
 * 已知异常处理器
 *
 * @author liujiasheng
 */
@Component
public class KnowExceptionConverter implements BaseExceptionConverter {

    @Override
    public Throwable convertToBaseException(Throwable e) {
        // 不支持的操作
        if (e instanceof UnsupportedOperationException) {
            return new BusinessException(YtWebExceptionEnum.CODE_14, e);
        }
        return e;
    }
}
