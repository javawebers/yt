package com.github.yt.web;

import com.github.yt.web.result.BaseResultConfig;
import com.github.yt.web.result.SimpleResultConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author sheng
 */
@Component
@ConfigurationProperties("yt")
public class YtWebConfig {

    private Page page = new Page();
    private Request request = new Request();
    private Result result = new Result();

    public Page getPage() {
        return page;
    }

    public YtWebConfig setPage(Page page) {
        this.page = page;
        return this;
    }

    public Request getRequest() {
        return request;
    }

    public YtWebConfig setRequest(Request request) {
        this.request = request;
        return this;
    }

    public Result getResult() {
        return result;
    }

    public YtWebConfig setResult(Result result) {
        this.result = result;
        return this;
    }

    public static class Request {
        /**
         * 是否记录请求日志
         */
        private boolean requestLog = true;


        public boolean isRequestLog() {
            return requestLog;
        }

        public Request setRequestLog(boolean requestLog) {
            this.requestLog = requestLog;
            return this;
        }
    }

    public static class Result {

        /**
         * 始终包装异常，优先级高于 packageResponseBody
         * true：发生异常不判断 全局 packageResponseBody 和 @PackageResponseBody，始终包装异常
         * false：发生异常判断 全局 packageResponseBody 和 @PackageResponseBody 来确定是否包装异常
         */
        private boolean alwaysPackageException = false;

        /**
         * 是否自动包装返回体
         * 默认为 true
         */
        private boolean packageResponseBody = true;
        /**
         * 是否返回异常栈信息
         */
        private boolean returnStackTrace = false;

        /**
         * 业务异常返回码
         */
        private int errorState = 500;

        /**
         * 发生异常时，把异常码和异常信息设置到返回体中
         */
        private boolean setResponseToHeader = false;
        /**
         * 设置异常栈信息到 header 中，不依赖于 returnStackTrace
         */
        private boolean setStackTraceToHeader = false;

        /**
         * 返回体配置类
         */
        private Class<? extends BaseResultConfig> resultConfigClass = SimpleResultConfig.class;

        public boolean isSetResponseToHeader() {
            return setResponseToHeader;
        }

        public void setSetResponseToHeader(boolean setResponseToHeader) {
            this.setResponseToHeader = setResponseToHeader;
        }

        public boolean isSetStackTraceToHeader() {
            return setStackTraceToHeader;
        }

        public void setSetStackTraceToHeader(boolean setStackTraceToHeader) {
            this.setStackTraceToHeader = setStackTraceToHeader;
        }

        public boolean isAlwaysPackageException() {
            return alwaysPackageException;
        }

        public Result setAlwaysPackageException(boolean alwaysPackageException) {
            this.alwaysPackageException = alwaysPackageException;
            return this;
        }

        private Class<?>[] ignorePackageResultTypes;

        public Class<?>[] getIgnorePackageResultTypes() {
            return ignorePackageResultTypes;
        }

        public void setIgnorePackageResultTypes(Class<?>[] ignorePackageResultTypes) {
            this.ignorePackageResultTypes = ignorePackageResultTypes;
        }

        public boolean isPackageResponseBody() {
            return packageResponseBody;
        }

        public Result setPackageResponseBody(boolean packageResponseBody) {
            this.packageResponseBody = packageResponseBody;
            return this;
        }

        public boolean isReturnStackTrace() {
            return returnStackTrace;
        }

        public Result setReturnStackTrace(boolean returnStackTrace) {
            this.returnStackTrace = returnStackTrace;
            return this;
        }

        public Class<? extends BaseResultConfig> getResultConfigClass() {
            return resultConfigClass;
        }

        public Result setResultConfigClass(Class<? extends BaseResultConfig> resultConfigClass) {
            this.resultConfigClass = resultConfigClass;
            return this;
        }

        public int getErrorState() {
            return errorState;
        }

        public void setErrorState(int errorState) {
            this.errorState = errorState;
        }
    }

    public static class Page {

        /**
         * 是否转换 page， 将 page 对象转换为 map
         */
        private boolean convertPage = false;
        /**
         * 页码
         */
        private String pageNoName = "pageNo";
        /**
         * 每页记录数
         */
        private String pageSizeName = "pageSize";
        /**
         * 总条数
         */
        private String pageTotalCountName = "totalCount";
        /**
         * 数据字段
         */
        private String pageDataName = "data";

        public boolean isConvertPage() {
            return convertPage;
        }

        public Page setConvertPage(boolean convertPage) {
            this.convertPage = convertPage;
            return this;
        }

        public String getPageNoName() {
            return pageNoName;
        }

        public Page setPageNoName(String pageNoName) {
            this.pageNoName = pageNoName;
            return this;
        }

        public String getPageSizeName() {
            return pageSizeName;
        }

        public Page setPageSizeName(String pageSizeName) {
            this.pageSizeName = pageSizeName;
            return this;
        }

        public String getPageTotalCountName() {
            return pageTotalCountName;
        }

        public void setPageTotalCountName(String pageTotalCountName) {
            this.pageTotalCountName = pageTotalCountName;
        }

        public String getPageDataName() {
            return pageDataName;
        }

        public void setPageDataName(String pageDataName) {
            this.pageDataName = pageDataName;
        }
    }

}

