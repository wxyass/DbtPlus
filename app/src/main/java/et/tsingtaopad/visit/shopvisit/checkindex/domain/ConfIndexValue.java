package et.tsingtaopad.visit.shopvisit.checkindex.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：ConfIndexValue.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-1-16</br>      
 * 功能描述: 指标模板设置中配置的指标值值</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class ConfIndexValue implements Serializable {

    private static final long serialVersionUID = 4527364244991594738L;
    
    // 指标值主键
    private String indexValueId;
    
    // 指标值名称
    private String indexValneName;
    
    // 该指标值对应的采集的标准值
    private List<ConfIndexItem> itemLst = new ArrayList<ConfIndexItem>();

    public String getIndexValueId() {
        return indexValueId;
    }

    public void setIndexValueId(String indexValueId) {
        this.indexValueId = indexValueId;
    }

    public String getIndexValneName() {
        return indexValneName;
    }

    public void setIndexValneName(String indexValneName) {
        this.indexValneName = indexValneName;
    }

    public List<ConfIndexItem> getItemLst() {
        return itemLst;
    }

    public void setItemLst(List<ConfIndexItem> itemLst) {
        this.itemLst = itemLst;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
