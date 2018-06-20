package et.tsingtaopad.visit.shopvisit.camera.domain;

import et.tsingtaopad.db.tables.MstCameraInfoM;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：MstCameraListMStc.java</br>
 * 作者：ywm   </br>
 * 创建时间：2015-11-17</br>      
 * 功能描述: 上传图片时,需要的json对象</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class UpCameraListMStc {

	/**
	 * 
	 */
	private static final long serialVersionUID = -7843008330190895121L;

	// 1图片主键  RECORDKEY 
	private String recordkey;
	// 2拜访主键 
	private String visitkey;
	// 3图片名称  PICNAME
	private String picname;
	// 4本地路径
	private String localpath;
	// 5 照片类型主键
	private String Pictypekey;
	// 6创建人
	private String createuser;
	// 7 更新
	private String updateuser;
	// 8图片转成的字符串
	private String imagefile;
	// 9 区域ID
	private String disid;
	// 10 定格ID
	private String gridid;
	// 11 路线ID
	private String routeid;
	// 12终端key
	private String terminalkey;
	// 13照片时间
	private String cameradata;
	// 14 拜访时间
	private String visitdate;
	// 15 拜访结束时间
	private String enddate;
	// 16 拜访对象
	private String visituser;
	
	/**
	 * @return the routeid
	 */
	public String getRouteid() {
		return routeid;
	}
	/**
	 * @param routeid the routeid to set
	 */
	public void setRouteid(String routeid) {
		this.routeid = routeid;
	}
	/**
	 * @return the disid
	 */
	public String getDisid() {
		return disid;
	}
	/**
	 * @param disid the disid to set
	 */
	public void setDisid(String disid) {
		this.disid = disid;
	}
	/**
	 * @return the gridid
	 */
	public String getGridid() {
		return gridid;
	}
	/**
	 * @param gridid the gridid to set
	 */
	public void setGridid(String gridid) {
		this.gridid = gridid;
	}
	/**
	 * @return the recordkey
	 */
	public String getRecordkey() {
		return recordkey;
	}
	/**
	 * @param recordkey the recordkey to set
	 */
	public void setRecordkey(String recordkey) {
		this.recordkey = recordkey;
	}
	/**
	 * @return the visitkey
	 */
	public String getVisitkey() {
		return visitkey;
	}
	/**
	 * @param visitkey the visitkey to set
	 */
	public void setVisitkey(String visitkey) {
		this.visitkey = visitkey;
	}
	/**
	 * @return the picname
	 */
	public String getPicname() {
		return picname;
	}
	/**
	 * @param picname the picname to set
	 */
	public void setPicname(String picname) {
		this.picname = picname;
	}
	/**
	 * @return the localpath
	 */
	public String getLocalpath() {
		return localpath;
	}
	/**
	 * @param localpath the localpath to set
	 */
	public void setLocalpath(String localpath) {
		this.localpath = localpath;
	}
	
	/**
	 * @return the pictypekey
	 */
	public String getPictypekey() {
		return Pictypekey;
	}
	/**
	 * @param pictypekey the pictypekey to set
	 */
	public void setPictypekey(String pictypekey) {
		Pictypekey = pictypekey;
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
	 * @return the updateuser
	 */
	public String getUpdateuser() {
		return updateuser;
	}
	/**
	 * @param updateuser the updateuser to set
	 */
	public void setUpdateuser(String updateuser) {
		this.updateuser = updateuser;
	}
	
	/**
	 * @return the serialversionuid
	 */
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	/**
	 * @return the imagefile
	 */
	public String getImagefile() {
		return imagefile;
	}
	/**
	 * @param imagefile the imagefile to set
	 */
	public void setImagefile(String imagefile) {
		this.imagefile = imagefile;
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
	 * @return the cameradata
	 */
	public String getCameradata() {
		return cameradata;
	}
	/**
	 * @param cameradata the cameradata to set
	 */
	public void setCameradata(String cameradata) {
		this.cameradata = cameradata;
	}
	/**
	 * @return the visitdate
	 */
	public String getVisitdate() {
		return visitdate;
	}
	/**
	 * @param visitdate the visitdate to set
	 */
	public void setVisitdate(String visitdate) {
		this.visitdate = visitdate;
	}
	/**
	 * @return the enddate
	 */
	public String getEnddate() {
		return enddate;
	}
	/**
	 * @param enddate the enddate to set
	 */
	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}
	/**
	 * @return the visituser
	 */
	public String getVisituser() {
		return visituser;
	}
	/**
	 * @param visituser the visituser to set
	 */
	public void setVisituser(String visituser) {
		this.visituser = visituser;
	}
	
	
	
}
