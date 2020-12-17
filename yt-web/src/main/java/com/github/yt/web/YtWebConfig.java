package com.github.yt.web;

import com.github.yt.web.result.BaseResultConfig;
import com.github.yt.web.result.SimpleResultConfig;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesBinding;
import org.springframework.boot.context.properties.DeprecatedConfigurationProperty;
import org.springframework.boot.context.properties.NestedConfigurationProperty;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

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
        /**
         * 是否记录 body 中信息，对文件上传有影响
         */
        private boolean requestLogBody = false;

        public boolean isRequestLog() {
            return requestLog;
        }

        public Request setRequestLog(boolean requestLog) {
            this.requestLog = requestLog;
            return this;
        }

        public boolean isRequestLogBody() {
            return requestLogBody;
        }

        public Request setRequestLogBody(boolean requestLogBody) {
            this.requestLogBody = requestLogBody;
            return this;
        }
    }

    public static class Result {
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
         * 返回体配置类
         */
        private Class<? extends BaseResultConfig> resultConfigClass = SimpleResultConfig.class;

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

