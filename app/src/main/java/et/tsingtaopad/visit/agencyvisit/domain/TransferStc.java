package et.tsingtaopad.visit.agencyvisit.domain;

import et.tsingtaopad.db.tables.MstAgencytransferInfo;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TransferStc.java</br>
 * 作者：吴欣伟   </br>
 * 创建时间：2013-12-3</br>      
 * 功能描述: 调货台账结构体</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TransferStc extends MstAgencytransferInfo {

    private static final long serialVersionUID = 8214919129390554696L;
    
    private String proName;
    private String tagAgencyName;
    
    public String getProName() {
        return proName;
    }
    public void setProName(String proName) {
        this.proName = proName;
    }
    public String getTagAgencyName() {
        return tagAgencyName;
    }
    public void setTagAgencyName(String tagAgencyName) {
        this.tagAgencyName = tagAgencyName;
    }
    public static long getSerialversionuid() {
        return serialVersionUID;
    }
    
}
