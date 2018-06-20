package et.tsingtaopad.visit.termtz.domain;


/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TerminalLoad.java</br>
 * 作者：yangwenmin   </br>
 * 创建时间：2016-7-4</br>      
 * 功能描述: </br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TerminalLoad {

	private int startrow;// 终端主键
	private int endrow;// 终端编号
	private String data; // 终端名称
	/**
	 * @return the startrow
	 */
	public int getStartrow() {
		return startrow;
	}
	/**
	 * @param startrow the startrow to set
	 */
	public void setStartrow(int startrow) {
		this.startrow = startrow;
	}
	/**
	 * @return the endrow
	 */
	public int getEndrow() {
		return endrow;
	}
	/**
	 * @param endrow the endrow to set
	 */
	public void setEndrow(int endrow) {
		this.endrow = endrow;
	}
	/**
	 * @return the data
	 */
	public String getData() {
		return data;
	}
	/**
	 * @param data the data to set
	 */
	public void setData(String data) {
		this.data = data;
	}
	
	
	
	
}
