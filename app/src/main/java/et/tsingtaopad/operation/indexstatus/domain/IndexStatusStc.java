package et.tsingtaopad.operation.indexstatus.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：IndexStatusStc.java</br>
 * 作者：hongen   </br>
 * 创建时间：2014-2-26</br>      
 * 功能描述: 指标状态查询显示数据结构</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class IndexStatusStc implements Serializable {

    private static final long serialVersionUID = -4032149974339993348L;
    
    private String proName;
    
    private int linePrevNum;
    
    private int lineCurrNum;
    
    private int gridPrevNum;
    
    private int gridCurrNum;

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public int getLinePrevNum() {
        return linePrevNum;
    }

    public void setLinePrevNum(int linePrevNum) {
        this.linePrevNum = linePrevNum;
    }

    public int getLineCurrNum() {
        return lineCurrNum;
    }

    public void setLineCurrNum(int lineCurrNum) {
        this.lineCurrNum = lineCurrNum;
    }

    public int getGridPrevNum() {
        return gridPrevNum;
    }

    public void setGridPrevNum(int gridPrevNum) {
        this.gridPrevNum = gridPrevNum;
    }

    public int getGridCurrNum() {
        return gridCurrNum;
    }

    public void setGridCurrNum(int gridCurrNum) {
        this.gridCurrNum = gridCurrNum;
    }

    public static long getSerialversionuid() {
        return serialVersionUID;
    }
}
