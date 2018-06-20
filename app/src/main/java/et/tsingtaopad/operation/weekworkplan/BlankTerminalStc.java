package et.tsingtaopad.operation.weekworkplan;

import java.io.Serializable;
import java.util.List;

import et.tsingtaopad.operation.workplan.domain.TypeStc;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：BlankTerminalStc.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2014-2-15</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class BlankTerminalStc implements Serializable {

    private static final long serialVersionUID = -6635094742355340040L;

    private List<BlankTermLevelStc> terminals;
    private String proName;
    private String proId;
    private String count;
    
    public List<BlankTermLevelStc> getTerminals() {
        return terminals;
    }
    public void setTerminals(List<BlankTermLevelStc> terminals) {
        this.terminals = terminals;
    }
    public String getProName() {
        return proName;
    }
    public void setProName(String proName) {
        this.proName = proName;
    }
    public String getProId() {
        return proId;
    }
    public void setProId(String proId) {
        this.proId = proId;
    }
    public String getCount() {
        return count;
    }
    public void setCount(String count) {
        this.count = count;
    }
    
}
