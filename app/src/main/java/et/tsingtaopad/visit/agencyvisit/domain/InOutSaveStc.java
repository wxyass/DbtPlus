package et.tsingtaopad.visit.agencyvisit.domain;

import java.io.Serializable;

import et.tsingtaopad.db.tables.MstInvoicingInfo;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：InOutSaveStc.java</br>
 * 作者：吴欣伟   </br>
 * 创建时间：2013-11-29</br>      
 * 功能描述: 进销存台账结构体</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class InOutSaveStc extends MstInvoicingInfo
{

    private static final long serialVersionUID = -2655448232852193717L;

    private Double storenumTemp;
    private String proName;

    public String getProName()
    {
        return proName;
    }

    public void setProName(String proName)
    {
        this.proName = proName;
    }

    public Double getStorenumTemp()
    {
        return storenumTemp;
    }

    public void setStorenumTemp(Double storenumTemp)
    {
        this.storenumTemp = storenumTemp;
    }

    public static long getSerialversionuid()
    {
        return serialVersionUID;
    }

}
