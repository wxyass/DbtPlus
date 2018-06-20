package et.tsingtaopad.visit.shopvisit.invoicing.domain;

import java.io.Serializable;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MonthSum.java</br>
 * 作者：yajie   </br>
 * 创建时间：2015-9-18</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class MonthSum implements Serializable{
	
	//本月合计
	
	private String monthTotal;
	
	private String productkey;
	
	private String productname;
	
	private String terminalkey;

	/**
	 * @return the monthTotal
	 */
	public String getMonthTotal() {
		return monthTotal;
	}

	/**
	 * @param monthTotal the monthTotal to set
	 */
	public void setMonthTotal(String monthTotal) {
		this.monthTotal = monthTotal;
	}

	/**
	 * @return the productkey
	 */
	public String getProductkey() {
		return productkey;
	}

	/**
	 * @param productkey the productkey to set
	 */
	public void setProductkey(String productkey) {
		this.productkey = productkey;
	}

	/**
	 * @return the productname
	 */
	public String getProductname() {
		return productname;
	}

	/**
	 * @param productname the productname to set
	 */
	public void setProductname(String productname) {
		this.productname = productname;
	}

	/**
	 * @return the terminalkey
	 */
	public String getTerminalkey() {
		return terminalkey;
	}

	/**
	 * @param terminalkey the terminalkey to set
	 */
	public void setTerminalkey(String terminalkey) {
		this.terminalkey = terminalkey;
	}
	
	
	
}
