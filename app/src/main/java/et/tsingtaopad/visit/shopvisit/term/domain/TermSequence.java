package et.tsingtaopad.visit.shopvisit.term.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TermSequence.java</br>
 * 作者：wf   </br>
 * 创建时间：2014-10-28</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TermSequence implements Serializable
{
    private static final long serialVersionUID = 1L;
    private String terminalkey;
    private String sequence;

    /**
     * @return the terminalkey
     */
    public String getTerminalkey()
    {
        return terminalkey;
    }

    /**
     * @param terminalkey the terminalkey to set
     */
    public void setTerminalkey(String terminalkey)
    {
        this.terminalkey = terminalkey;
    }

    /**
     * @return the sequence
     */
    public String getSequence()
    {
        return sequence;
    }

    /**
     * @param sequence the sequence to set
     */
    public void setSequence(String sequence)
    {
        this.sequence = sequence;
    }

    @Override
    public String toString()
    {
        return "TermSequence [terminalkey=" + terminalkey + ", sequence=" + sequence + "]";
    }
}
