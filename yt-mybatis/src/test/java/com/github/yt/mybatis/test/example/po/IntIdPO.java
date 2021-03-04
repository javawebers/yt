package com.github.yt.mybatis.test.example.po;

import com.github.yt.mybatis.entity.BaseEntity;

public class IntIdPO<T extends  IntIdPO<T>> extends BaseEntity<T>{
    
    /** 
     * id  
     */
    @javax.persistence.Id
    private Integer intId;
    /** 
     * String类型  
     */
    private String testVarchar;
    
    public Integer getIntId() {
        return this.intId;
    }
    
    public T setIntId(Integer intId) {
        this.intId = intId;
        return (T)this;
    }
    
    public String getTestVarchar() {
        return this.testVarchar;
    }
    
    public T setTestVarchar(String testVarchar) {
        this.testVarchar = testVarchar;
        return (T)this;
    }
}
