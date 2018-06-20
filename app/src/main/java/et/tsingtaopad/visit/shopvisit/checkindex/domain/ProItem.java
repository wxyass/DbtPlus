package et.tsingtaopad.visit.shopvisit.checkindex.domain;

import java.io.Serializable;
import java.util.List;

public class ProItem implements Serializable{

    private static final long serialVersionUID = 7684826624412377693L;
    
    // 拜访指标执行采集项记录表主键
    private String colRecordKey;
    
    // 指标主键
    private String checkkey;
    
    // 采集项ID
    private String itemId;
    
    // 采集项名称
    private String itemName;
    
    // 产品ID
    private String proId;
    
    // 产品名称
    private String proName;
    
    // 变化量
    private Double changeNum;
    
    // 最终值
    private Double finalNum;
    
    // 新鲜度 
    private String freshness;
    
    // 适用指标主键集合
    private List<String> indexIdLst;
    
    // 现有量(用于最后上传时 现有量变化量必须填值,才能上传) 
    private String xianyouliang;
    // 变化量(用于最后上传时 现有量变化量必须填值,才能上传) 
    private String bianhualiang;

    /**
     * @return the checkkey
     */
    public String getCheckkey()
    {
        return checkkey;
    }

    /**
     * @param checkkey the checkkey to set
     */
    public void setCheckkey(String checkkey)
    {
        this.checkkey = checkkey;
    }

    public String getColRecordKey() {
        return colRecordKey;
    }

    public void setColRecordKey(String colRecordKey) {
        this.colRecordKey = colRecordKey;
    }

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

    public String getProId() {
        return proId;
    }

    public void setProId(String proId) {
        this.proId = proId;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Double getChangeNum() {
        return changeNum;
    }

    public void setChangeNum(Double changeNum) {
        this.changeNum = changeNum;
    }

    public Double getFinalNum() {
        return finalNum;
    }

    public void setFinalNum(Double finalNum) {
        this.finalNum = finalNum;
    }

    public List<String> getIndexIdLst() {
        return indexIdLst;
    }

    public void setIndexIdLst(List<String> indexIdLst) {
        this.indexIdLst = indexIdLst;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }

	/**
	 * @return the freshness
	 */
	public String getFreshness() {
		return freshness;
	}

	/**
	 * @param freshness the freshness to set
	 */
	public void setFreshness(String freshness) {
		this.freshness = freshness;
	}

	/**
	 * @return the xianyouliang
	 */
	public String getXianyouliang() {
		return xianyouliang;
	}

	/**
	 * @param xianyouliang the xianyouliang to set
	 */
	public void setXianyouliang(String xianyouliang) {
		this.xianyouliang = xianyouliang;
	}

	/**
	 * @return the bianhualiang
	 */
	public String getBianhualiang() {
		return bianhualiang;
	}

	/**
	 * @param bianhualiang the bianhualiang to set
	 */
	public void setBianhualiang(String bianhualiang) {
		this.bianhualiang = bianhualiang;
	}

}
