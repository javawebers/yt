package com.github.yt.commons.query;

import java.util.*;

/**
 * 查询条件类
 * @author liujiasheng
 */
public class Query {

    protected Integer pageNo;
    protected Integer pageSize;

    protected boolean updateBaseColumn = true;

    final protected List<String> updateColumnList = new ArrayList<>();
    final protected List<String> selectColumnList = new ArrayList<>();
    final protected List<String> whereList = new ArrayList<>();

    final protected List<InCondition> inParamList = new ArrayList<>();
    final protected List<String> orderByList = new ArrayList<>();
    protected String groupBy = "";
    protected Integer limitFrom;
    protected Integer limitSize;
    final protected List<Join> joinList = new ArrayList<>();
    final protected Map<String, Object> param = new HashMap<>();

    public Query updateBaseColumn(boolean updateBaseColumn) {
        this.updateBaseColumn = updateBaseColumn;
        return this;
    }

    public boolean takeUpdateBaseColumn() {
        return updateBaseColumn;
    }

    public Query addParam(String paramName, Object paramValue){
        if (paramValue instanceof Collection) {
            addInParam(paramName, (Collection)paramValue);
        } else {
            param.put(paramName, paramValue);
        }
        return this;
    }
    private Query addInParam(String paramName, Collection paramValue){
        inParamList.add(new InCondition(paramName, paramValue));
        return this;
    }



    public Query addSelectColumn(String selectColumn){
        selectColumnList.add(selectColumn);
        return this;
    }

    public Query addUpdate(String updateColumn){
        updateColumnList.add(updateColumn);
        return this;
    }


    public Query addWhere(String where){
        whereList.add(where);
        return this;
    }

    public Query addOrderBy(String columns){
        orderByList.add(columns);
        return this;
    }

    public Query addGroupBy(String columns){
        groupBy = columns;
        return this;
    }

    public Query limit(int from, int size){
        limitFrom = from;
        limitSize = size;
        return this;
    }


    public Query addJoin(JoinType joinType,
                         String tableNameAndOnConditions){
        joinList.add(new Join(joinType, tableNameAndOnConditions));
        return this;
    }

    ////// take 参数
    public Map<String, Object> takeParam() {
        return param;
    }
    public List<InCondition> takeInParamList() {
        return inParamList;
    }
    public List<String> takeUpdateColumnList(){
        return updateColumnList;
    }

    public List<String> takeSelectColumnList(){
        return selectColumnList;
    }

    public List<String> takeWhereList() {
        return whereList;
    }


    public List<String> takeOrderByList() {
        return orderByList;
    }

    public String takeGroupBy() {
        return groupBy;
    }

    public List<Join> takeJoinList() {
        return joinList;
    }



    ///////////////////////////////
    public Query makePageNo(Integer pageNo) {
        this.pageNo = pageNo;
        return this;
    }

    public Query makePageSize(Integer pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Integer takePageNo() {
        return pageNo;
    }

    public Integer takePageSize() {
        return pageSize;
    }

    public Integer takeLimitFrom() {
        return limitFrom;
    }

    public Integer takeLimitSize() {
        return limitSize;
    }

    ///////////////////////////////
    /**
     * 连接类型
     */
    public enum JoinType{
        //
        JOIN(" JOIN "),
        LEFT_JOIN(" LEFT JOIN "),
        RIGHT_JOIN(" RIGHT JOIN "),
        ;
        String value;
        JoinType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    /**
     * in查询条件
     */
    public class InCondition{
        private String param;
        private Collection values;

        public String takeParam() {
            return param;
        }

        public Collection takeValues() {
            return values;
        }

        public InCondition(String param, Collection values) {
            this.param = param;
            this.values = values;
        }

    }

    public class Join {
        private JoinType joinType;
        private String tableNameAndOnConditions;

        public JoinType takeJoinType() {
            return joinType;
        }

        public String takeTableNameAndOnConditions() {
            return tableNameAndOnConditions;
        }

        private Join(JoinType joinType, String tableNameAndOnConditions) {
            this.joinType = joinType;
            this.tableNameAndOnConditions = tableNameAndOnConditions;
        }
    }

}
