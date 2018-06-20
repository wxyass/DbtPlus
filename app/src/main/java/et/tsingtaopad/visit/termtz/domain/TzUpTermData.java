package et.tsingtaopad.visit.termtz.domain;

import java.util.Date;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：TzUpTermData.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-12-9</br>      
 * 功能描述: 上传终端进货量结构体</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class TzUpTermData {
	
	private String accountkey;    //主键
	private String usergridkey;   //定格ID业代
	private String terminalkey;   //终端key
	
	private String agencykey;    //经销商key
	private String agencyname;   //经销商名称
	private String agencycode;   //经销商编码
	
	private String productkey;   // '产品key'
	private String proname;      //'产品名称';
	private String procode;      //'产品名称';
	private String purchasenum;    //'进货量';
	
	private String createuser;    //  '创建人';
	private String createdate;    //'创建时间';
	
	
	private String gridareaid;	//  区域（终端所属区域）有 areaid
	private String tergridkey;	// 定格ID（终端）    有 girdkey
	private String gridname;	     //定格名称    有 gridname
	private String routename;	// 路线名称     有 routename
	
	private String purchasetime;   // 进货时间
	
	public TzUpTermData() {
		super();
	}
	
	public TzUpTermData(String accountkey, String usergridkey,
			String terminalkey, String agencykey, String agencyname,
			String agencycode, String productkey, String proname,
			String purchasenum, String createuser, String createdate) {
		super();
		this.accountkey = accountkey;
		this.usergridkey = usergridkey;
		this.terminalkey = terminalkey;
		this.agencykey = agencykey;
		this.agencyname = agencyname;
		this.agencycode = agencycode;
		this.productkey = productkey;
		this.proname = proname;
		this.purchasenum = purchasenum;
		this.createuser = createuser;
		this.createdate = createdate;
	}



	/**
	 * @return the accountkey
	 */
	public String getAccountkey() {
		return accountkey;
	}
	/**
	 * @param accountkey the accountkey to set
	 */
	public void setAccountkey(String accountkey) {
		this.accountkey = accountkey;
	}
	/**
	 * @return the usergridkey
	 */
	public String getUsergridkey() {
		return usergridkey;
	}
	/**
	 * @param usergridkey the usergridkey to set
	 */
	public void setUsergridkey(String usergridkey) {
		this.usergridkey = usergridkey;
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
	 * @return the agencykey
	 */
	public String getAgencykey() {
		return agencykey;
	}
	/**
	 * @param agencykey the agencykey to set
	 */
	public void setAgencykey(String agencykey) {
		this.agencykey = agencykey;
	}
	/**
	 * @return the agencyname
	 */
	public String getAgencyname() {
		return agencyname;
	}
	/**
	 * @param agencyname the agencyname to set
	 */
	public void setAgencyname(String agencyname) {
		this.agencyname = agencyname;
	}
	/**
	 * @return the agencycode
	 */
	public String getAgencycode() {
		return agencycode;
	}
	/**
	 * @param agencycode the agencycode to set
	 */
	public void setAgencycode(String agencycode) {
		this.agencycode = agencycode;
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
	 * @return the proname
	 */
	public String getProname() {
		return proname;
	}
	/**
	 * @param proname the proname to set
	 */
	public void setProname(String proname) {
		this.proname = proname;
	}
	/**
	 * @return the purchasenum
	 */
	public String getPurchasenum() {
		return purchasenum;
	}
	/**
	 * @param purchasenum the purchasenum to set
	 */
	public void setPurchasenum(String purchasenum) {
		this.purchasenum = purchasenum;
	}
	/**
	 * @return the createuser
	 */
	public String getCreateuser() {
		return createuser;
	}
	/**
	 * @param createuser the createuser to set
	 */
	public void setCreateuser(String createuser) {
		this.createuser = createuser;
	}
	/**
	 * @return the createdate
	 */
	public String getCreatedate() {
		return createdate;
	}
	/**
	 * @param createdate the createdate to set
	 */
	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	/**
	 * @return the gridareaid
	 */
	public String getGridareaid() {
		return gridareaid;
	}

	/**
	 * @param gridareaid the gridareaid to set
	 */
	public void setGridareaid(String gridareaid) {
		this.gridareaid = gridareaid;
	}

	/**
	 * @return the tergridkey
	 */
	public String getTergridkey() {
		return tergridkey;
	}

	/**
	 * @param tergridkey the tergridkey to set
	 */
	public void setTergridkey(String tergridkey) {
		this.tergridkey = tergridkey;
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
	 * @return the purchasetime
	 */
	public String getPurchasetime() {
		return purchasetime;
	}

	/**
	 * @param purchasetime the purchasetime to set
	 */
	public void setPurchasetime(String purchasetime) {
		this.purchasetime = purchasetime;
	}

	/**
	 * @return the procode
	 */
	public String getProcode() {
		return procode;
	}

	/**
	 * @param procode the procode to set
	 */
	public void setProcode(String procode) {
		this.procode = procode;
	}
}
