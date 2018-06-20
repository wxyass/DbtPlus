package et.tsingtaopad.visit.shopvisit.termindex.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermIndexStc.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-12-4</br>      
 * 功能描述: 用于显示终端拜访的条产品的采集指标状态的结构体</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermIndexStc implements Serializable {

    private static final long serialVersionUID = -7285213778736239768L;
    
    // 产品主键
    private String proKey;
    
    // 产品名称
    private String proName;
    
    // 指标主键
    private String indexKey;
    
    // 指标值主键
    private String indexValueKey;
    
    // 采集项主键
    private String itemKey;
    
    // 采集项名称
    private String itemName;
    
    // 变化量
    private String addCount;
    
    // 最终量
    private String totalCount;
    
    // 上次变化量
    private String prevAddCount;
    
    // 上次最终量
    private String prevTotalCount;

    public String getProKey() {
        return proKey;
    }

    public void setProKey(String proKey) {
        this.proKey = proKey;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public String getIndexKey() {
        return indexKey;
    }

    public void setIndexKey(String indexKey) {
        this.indexKey = indexKey;
    }

    public String getIndexValueKey() {
        return indexValueKey;
    }

    public void setIndexValueKey(String indexValueKey) {
        this.indexValueKey = indexValueKey;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getAddCount() {
        return addCount;
    }

    public void setAddCount(String addCount) {
        this.addCount = addCount;
    }

    public String getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(String totalCount) {
        this.totalCount = totalCount;
    }

    public String getPrevAddCount() {
        return prevAddCount;
    }

    public void setPrevAddCount(String prevAddCount) {
        this.prevAddCount = prevAddCount;
    }

    public String getPrevTotalCount() {
        return prevTotalCount;
    }

    public void setPrevTotalCount(String prevTotalCount) {
        this.prevTotalCount = prevTotalCount;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
