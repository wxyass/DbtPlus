package et.tsingtaopad.initconstvalues.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：kvStc.java</br>
 * 作者：吴承磊   </br>
 * 创建时间：2013-11-29</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class KvStc implements Serializable {

    private static final long serialVersionUID = 3322789047184484841L;

    private String key;
    
    private String value;
    
    private String parentKey;
    
    private String isDefault;
    
    private List<KvStc> childLst = new ArrayList<KvStc>();
    
    public KvStc() {
        
    }
    
    public KvStc(String key, String value, String parentKey) {
        this.key = key;
        this.value = value;
        this.parentKey = parentKey;
    }
    
    public KvStc(String key, String value, String parentKey, String isDefault) {
        this.key = key;
        this.value = value;
        this.parentKey = parentKey;
        this.isDefault = isDefault;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getParentKey() {
        return parentKey;
    }

    public void setParentKey(String parentKey) {
        this.parentKey = parentKey;
    }

    public List<KvStc> getChildLst() {
        return childLst;
    }

    public void setChildLst(List<KvStc> childLst) {
        this.childLst = childLst;
    }

    public String getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(String isDefault) {
        this.isDefault = isDefault;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
}
