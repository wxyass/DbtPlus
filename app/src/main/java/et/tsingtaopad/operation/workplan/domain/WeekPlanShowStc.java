package et.tsingtaopad.operation.workplan.domain;


import java.util.Date;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：WeekPlanShowStc.java</br>
 * 作者：ywm   </br>
 * 创建时间：2016年3月30日</br>      
 * 功能描述:工作计划页面周计划每个指标数据结构体 </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class WeekPlanShowStc {

	private String colitemname;//采集项名称
	private String productname;//产品名称
	private String termnum;//终端数量
	private String checkkey;// 指标主键
	/**
	 * @return the colitemname
	 */
	public String getColitemname() {
		return colitemname;
	}
	/**
	 * @param colitemname the colitemname to set
	 */
	public void setColitemname(String colitemname) {
		this.colitemname = colitemname;
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
	 * @return the termnum
	 */
	public String getTermnum() {
		return termnum;
	}
	/**
	 * @param termnum the termnum to set
	 */
	public void setTermnum(String termnum) {
		this.termnum = termnum;
	}
	/**
	 * @return the checkkey
	 */
	public String getCheckkey() {
		return checkkey;
	}
	/**
	 * @param checkkey the checkkey to set
	 */
	public void setCheckkey(String checkkey) {
		this.checkkey = checkkey;
	}
	

}
