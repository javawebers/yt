package com.github.yt.mybatis.conf;

import com.github.yt.mybatis.dialect.Dialect;
import com.github.yt.mybatis.dialect.impl.MysqlDialect;
import com.github.yt.mybatis.entity.BaseEntityValue;
import com.github.yt.mybatis.entity.DefaultBaseEntityValue;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.io.Serializable;

/**
 * @author sheng
 */
@Component
@ConfigurationProperties("yt")
@Data
public class YtMybatisProperties implements Serializable {

    /**
     *
     */
    private Entity entity = new Entity();
    private Mybatis mybatis = new Mybatis();
    private Page page = new Page();


    @Data
    public static class Entity {
        /**
         * base entity 自动设置值实现类
         */
        private Class<? extends BaseEntityValue> baseEntityValue = DefaultBaseEntityValue.class;

        /**
         * id 生成规则
         */
        private IdGenerateRule idGenerateRule = IdGenerateRule.TABLE_DATE_RANDOM;
        /**
         * 当 id 生成规则为 TABLE_DATE_RANDOM 时，随机值的位数
         */
        private int idRandomNum = 5;

    }

    /**
     * id 生成规则枚举
     */
    public enum IdGenerateRule {

        /**
         * uuid，如：cbbd1886daa6c9b7a6367ad563df4611
         */
        UUID,
        /**
         * 表名+时间+随机值，如：sys_user_20200920153722572_dn3f3
         */
        TABLE_DATE_RANDOM,
        ;

    }

    @Data
    public static class Mybatis {
        /**
         * 数据库方言
         * 在 com.github.yt.mybatis.dialect.impl 包中，默认为 MysqlDialect
         */
        private Class<? extends Dialect> dialect = MysqlDialect.class;
        /**
         * 是否开启 yt-mybatis
         */
        private boolean enable = true;
    }

    @Data
    public static class Page {
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

    }

}

