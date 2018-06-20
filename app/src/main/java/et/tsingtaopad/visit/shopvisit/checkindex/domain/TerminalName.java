package et.tsingtaopad.visit.shopvisit.checkindex.domain;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TerminalName.java</br>
 * 作者：admin   </br>
 * 创建时间：2015-1-15</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TerminalName {
	
	private String terminalName;//终端名称
	private String routeName;//线路名称
	private String terminalKey;//终端key
	
	public String getTerminalName() {
		return terminalName;
	}
	public void setTerminalName(String terminalName) {
		this.terminalName = terminalName;
	}
	public String getRouteName() {
		return routeName;
	}
	public void setRouteName(String routeName) {
		this.routeName = routeName;
	}
	public String getTerminalKey() {
		return terminalKey;
	}
	public void setTerminalKey(String terminalKey) {
		this.terminalKey = terminalKey;
	}
	
}
