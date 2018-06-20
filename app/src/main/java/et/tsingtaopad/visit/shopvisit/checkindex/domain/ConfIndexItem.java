package et.tsingtaopad.visit.shopvisit.checkindex.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ConfIndexItem.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-1-16</br>      
 * 功能描述: 指标模板设置中配置的采集项的标准值</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ConfIndexItem implements Serializable {

    private static final long serialVersionUID = 2285672900977162970L;
    
    // 采集项Id
    private String itemId;
    
    // 采集项名称
    private String itemName;
    
    // 配置变化量
    private String changeNum;
    
    // 最终量
    private String finalNum;

    public String getItemId() {
        return itemId;
    }

    public void setItemId(String itemId) {
        this.itemId = itemId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getChangeNum() {
        return changeNum;
    }

    public void setChangeNum(String changeNum) {
        this.changeNum = changeNum;
    }

    public String getFinalNum() {
        return finalNum;
    }

    public void setFinalNum(String finalNum) {
        this.finalNum = finalNum;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
