package et.tsingtaopad.visit.termtz.domain;

/**
 * 项目名称：营销移动智能工作平台 </br> 
 * 文件名：MstTzTermInfo.java</br> 
 * 作者：ywm </br> 
 * 创建时间：2015-11-23</br> 
 * 功能描述:终端进货台账_终端列表bean </br>
 * 版本 V 1.0</br> 
 * 修改履历</br> 日期 原因 BUG号 修改人
 * 修改版本</br>
 */
public class MstTzTermInfo {

	private String terminalkey;// 终端主键
	private String terminalcode;// 终端编号
	private String terminalname; // 终端名称
	private String sequence; // 终端顺序
	private String gridname; // 定格名称
	private String routename; // 线路
	private String address; // 地址
	private String contact; // 关键人
	private String mobile; // 联系电话
	private String yesup; // pad是否上传成功(0 成功)
	private String padisconsistent; // pad是否上传成功(0 成功)
	
	/**
	 * 
	 */
	public MstTzTermInfo() {
		super();
	}
	
	/**
	 * @param terminalname
	 */
	public MstTzTermInfo(String terminalname) {
		super();
		this.terminalname = terminalname;
	}

	/**
	 * @param terminalkey
	 * @param terminalcode
	 * @param terminalname
	 * @param gridname
	 * @param routename
	 * @param address
	 * @param contact
	 * @param mobile
	 */
	public MstTzTermInfo(String terminalkey, String terminalcode,
			String terminalname, String gridname, String routename,
			String address, String contact, String mobile) {
		super();
		this.terminalkey = terminalkey;
		this.terminalcode = terminalcode;
		this.terminalname = terminalname;
		this.gridname = gridname;
		this.routename = routename;
		this.address = address;
		this.contact = contact;
		this.mobile = mobile;
	}

	/**
	 * @return the terminalkey
	 */
	public String getTerminalcode() {
		return terminalcode;
	}

	/**
	 * @param terminalcode the terminalkey to set
	 */
	public void setTerminalcode(String terminalcode) {
		this.terminalcode = terminalcode;
	}

	/**
	 * @return the terminalname
	 */
	public String getTerminalname() {
		return terminalname;
	}

	/**
	 * @param terminalname the terminalname to set
	 */
	public void setTerminalname(String terminalname) {
		this.terminalname = terminalname;
	}

	/**
	 * @return the gridname
	 */
	public String getGridname() {
		return gridname;
	}

	/**
	 * @param gridname the gridname to set
	 */
	public void setGridname(String gridname) {
		this.gridname = gridname;
	}

	/**
	 * @return the routename
	 */
	public String getRoutename() {
		return routename;
	}

	/**
	 * @param routename the routename to set
	 */
	public void setRoutename(String routename) {
		this.routename = routename;
	}

	/**
	 * @return the address
	 */
	public String getAddress() {
		return address;
	}

	/**
	 * @param address the address to set
	 */
	public void setAddress(String address) {
		this.address = address;
	}

	/**
	 * @return the contact
	 */
	public String getContact() {
		return contact;
	}

	/**
	 * @param contact the contact to set
	 */
	public void setContact(String contact) {
		this.contact = contact;
	}

	/**
	 * @return the mobile
	 */
	public String getMobile() {
		return mobile;
	}

	/**
	 * @param mobile the mobile to set
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
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

	/**
	 * @return the yesup
	 */
	public String getYesup() {
		return yesup;
	}

	/**
	 * @param yesup the yesup to set
	 */
	public void setYesup(String yesup) {
		this.yesup = yesup;
	}

	/**
	 * @return the padisconsistent
	 */
	public String getPadisconsistent() {
		return padisconsistent;
	}

	/**
	 * @param padisconsistent the padisconsistent to set
	 */
	public void setPadisconsistent(String padisconsistent) {
		this.padisconsistent = padisconsistent;
	}

	/**
	 * @return the sequence
	 */
	public String getSequence() {
		return sequence;
	}

	/**
	 * @param sequence the sequence to set
	 */
	public void setSequence(String sequence) {
		this.sequence = sequence;
	}
	
	
	
}
