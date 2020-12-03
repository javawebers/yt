package com.github.yt.mybatis.generator;

import com.github.yt.mybatis.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

/**
 * 创建文件
 *
 * @author sheng
 */
public class CreateBean {
    Logger logger = LoggerFactory.getLogger(CreateBean.class);

    private static String url;
    private static String username;
    private static String password;
    private static String dbInstance;


    private String method;
    private String argv;


    static {
        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setMysqlInfo(String url, String username, String password, String dbInstance) {
        CreateBean.url = url;
        CreateBean.username = username;
        CreateBean.password = password;
        CreateBean.dbInstance = dbInstance;
    }

    public Connection getConnection() throws SQLException {
        logger.info(url);
        return DriverManager.getConnection(url, username, password);
    }


    public String getTableComment(String tableName) {
        String sqlTable = "SELECT distinct TABLE_COMMENT FROM INFORMATION_SCHEMA.TABLES WHERE table_name='" + tableName + "' " + "and table_schema='" + dbInstance + "' ";
        try (Connection con = this.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlTable);
             ResultSet rs = ps.executeQuery()) {
            String comment = tableName;
            while (rs.next()) {
                comment = rs.getString(1);
            }
            return comment;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public List<ColumnData> getColumnDataList(String tableName, Class<?> baseEntityClass) {
        String sqlColumns = "SELECT distinct COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT,COLUMN_KEY,CHARACTER_MAXIMUM_LENGTH" +
                ",IS_NULLABLE,COLUMN_DEFAULT, COLUMN_TYPE  FROM information_schema.columns WHERE table_name =  '" + tableName + "' " + "and table_schema='" + dbInstance + "' ";
        // 忽略父类中的字段
        Set<String> baseEntityFieldSet = new HashSet<>();
        if (baseEntityClass != null) {
            List<Field> baseEntityFieldList = EntityUtils.getTableFieldList(baseEntityClass);
            baseEntityFieldList.forEach(baseEntityField -> {
                String baseColumnName = EntityUtils.getFieldColumnName(baseEntityField);
                baseEntityFieldSet.add(baseColumnName);
            });
        }
        try (Connection con = this.getConnection();
             PreparedStatement ps = con.prepareStatement(sqlColumns);
             ResultSet rs = ps.executeQuery()) {


            List<ColumnData> columnList = new ArrayList<>();
            while (rs.next()) {
                String name = rs.getString(1);
                String type = rs.getString(2);
                String comment = rs.getString(3);
                String priKey = rs.getString(4);
                Long length = rs.getLong(5);
                String isNullable = rs.getString(6);
                String columnDefault = rs.getString(7);
                String columnType = rs.getString(8);
                type = this.getType(type);

                ColumnData cd = new ColumnData();
                cd.setBaseEntityColumn(baseEntityFieldSet.contains(name));
                cd.setTableName(tableName);
                cd.setClassName(getTablesNameToClassName(tableName));
                cd.setColumnName(name);
                cd.setFieldName(getTablesColumnToAttributeName(name));
                cd.setDataType(type);
                cd.setColumnComment(comment);
                cd.setColumnNameContainEntity("${entity." + name + " }");
                cd.setPriKey("PRI".equals(priKey));
                cd.setColumnLength(length);
                cd.setNullable("NO".equals(isNullable));
                cd.setColumnDefault(columnDefault);
                cd.setColumnType(columnType);
                processEnumColumn(cd);
                columnList.add(cd);
            }
            argv = "";
            method = "";
            return columnList;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 枚举特殊处理
     *
     * @param cd cd
     */
    private void processEnumColumn(ColumnData cd) {
        if ("enum".equals(cd.getDataType())) {
            String fieldName = cd.getFieldName();
            String enumClassName = cd.getClassName() + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1) + "Enum";
            List<EnumColumnData> enumColumnDataList = new ArrayList<>();

            // enum('MALE','FEMALE')
            String enumType = cd.getColumnType();
            String[] enumColumns = enumType.replaceAll("enum\\(", "").replaceAll("\\)", "").replaceAll("'", "").split(",");
            for (String enumColumn : enumColumns) {
                EnumColumnData enumColumnData = new EnumColumnData();
                enumColumnData.setName(enumColumn);
                enumColumnDataList.add(enumColumnData);
            }
            cd.setEnumClassName(enumClassName);
            cd.setEnumColumnDataList(enumColumnDataList);
        }
    }

    public String getBeanFieldList(List<ColumnData> columnDataList, boolean withSwagger) {
        StringBuilder str = new StringBuilder();
        StringBuilder getSet = new StringBuilder();
        for (ColumnData d : columnDataList) {
            if (d.getBaseEntityColumn()) {
                continue;
            }
            String columnName = d.getColumnName();
            String fieldName = d.getFieldName();
            String type = d.getDataType();
            if ("enum".equals(type)) {
                type = d.getEnumClassName();
            }
            String comment = d.getColumnComment();
            // 替换备注中的双引号字符为单引号
            comment = comment.replace("\"", "'");
            String maxChar = fieldName.substring(0, 1).toUpperCase();
            str.append("\r\n    /** \r\n     * ").append(comment).append("  \r\n     */");

            if (d.getPriKey()) {
                str.append("\r\n    @javax.persistence.Id");
            }
            if (withSwagger) {
                str.append("\r\n    @io.swagger.annotations.ApiModelProperty(\"").append(comment).append("\")");
            }
            if (!columnName.equals(fieldName)) {
                str.append("\r\n    ").append("@javax.persistence.Column(name = \"").append(columnName).append("\")");
            }
            str.append("\r\n    ").append("private ").append(type).append(" ").append(fieldName).append(";");
            String method = maxChar + fieldName.substring(1);
            getSet.append("\r\n\r\n    ").append("public ").append(type).append(" ").append("get").append(method).append("() {\r\n    ");
            getSet.append("    return this.").append(fieldName).append(";\r\n    }");
            getSet.append("\r\n\r\n    ")
                    .append("@SuppressWarnings(\"unchecked\")")
                    .append("\r\n    ").append("public T set").append(method).append("(").append(type).append(" ").append(fieldName).append(") {\r\n    ");
            getSet.append("    this.").append(fieldName).append(" = ").append(fieldName).append(";\r\n        return (T) this;\r\n    }");
        }
        argv = str.toString();
        method = getSet.toString();
        return argv + method;
    }

    public String getType(String type) {
        switch (type.toLowerCase()) {
            case "char":
            case "varchar":
            case "tinytext":
            case "longtext":
            case "mediumtext":
            case "text":
                return "String";
            case "enum":
                return "enum";
            case "smallint":
            case "mediumint":
            case "integer":
            case "int":
                return "Integer";
            case "bigint":
                return "Long";
            case "decimal":
                return "java.math.BigDecimal";
            case "timestamp":
            case "date":
            case "time":
            case "year":
            case "datetime":
                // return "java.sql.Timestamp";
                return "java.util.Date";
            case "float":
                return "Float";
            case "double":
                return "Double";
            case "tinyint":
                return "Boolean";
            default:
                return null;
        }
    }

    public String getTablesNameToClassName(String tableName) {
        String[] split = tableName.split("_");
        if (split.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (String s : split) {
                String tempTableName = s.substring(0, 1).toUpperCase()
                        + s.substring(1).toLowerCase();
                sb.append(tempTableName);
            }
            return sb.toString();
        } else {
            return split[0].substring(0, 1).toUpperCase() + split[0].substring(1);
        }
    }


    public String getTablesColumnToAttributeName(String columnName) {
        String[] split = columnName.split("_");
        if (split.length > 1) {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < split.length; i++) {
                String tempTableName;
                if (i == 0) {
                    tempTableName = split[i].substring(0, 1).toLowerCase() + split[i].substring(1);
                } else {
                    tempTableName = split[i].substring(0, 1).toUpperCase() + split[i].substring(1);
                }
                sb.append(tempTableName);
            }
            return sb.toString();
        } else {
            return split[0].substring(0, 1).toLowerCase() + split[0].substring(1);
        }
    }

}
