package et.tsingtaopad.visit.shopvisit.camera.domain;

/**
 * 项目名称：营销移动智能工作平台 </br>
 * 文件名：PictypeDataStc.java</br>
 * 作者：Administrator   </br>
 * 创建时间：2015-11-19</br>      
 * 功能描述: 查询同步下来的图片类型表,用于要拍几张什么样的照片</br>      
 * 版本 V 1.0</br>               
 * 修改履历</br>
 * 日期      原因  BUG号    修改人 修改版本</br>
 */
public class PictypeDataStc {
	
	private String pictypekey;// 图片类型主键
	private String areaid;// 区域id
	private String focus;// 清晰度
	private String orderno ;// 顺序
	private String pictypename ;// 类型描述
	
	
	
	/**
	 * 
	 */
	public PictypeDataStc() {
		super();
	}
	/**
	 * @param pictypekey
	 * @param focus
	 * @param orderno
	 * @param pictypename
	 */
	public PictypeDataStc(String pictypekey, String focus, String orderno, String pictypename) {
		super();
		this.pictypekey = pictypekey;
		this.focus = focus;
		this.orderno = orderno;
		this.pictypename = pictypename;
	}
	/**
	 * @return the pictypekey
	 */
	public String getPictypekey() {
		return pictypekey;
	}
	/**
	 * @param pictypekey the pictypekey to set
	 */
	public void setPictypekey(String pictypekey) {
		this.pictypekey = pictypekey;
	}
	/**
	 * @return the focus
	 */
	public String getFocus() {
		return focus;
	}
	/**
	 * @param focus the focus to set
	 */
	public void setFocus(String focus) {
		this.focus = focus;
	}
	/**
	 * @return the orderno
	 */
	public String getOrderno() {
		return orderno;
	}
	/**
	 * @param orderno the orderno to set
	 */
	public void setOrderno(String orderno) {
		this.orderno = orderno;
	}
	/**
	 * @return the pictypename
	 */
	public String getPictypename() {
		return pictypename;
	}
	/**
	 * @param pictypename the pictypename to set
	 */
	public void setPictypename(String pictypename) {
		this.pictypename = pictypename;
	}
	/**
	 * @return the areaid
	 */
	public String getAreaid() {
		return areaid;
	}
	/**
	 * @param areaid the areaid to set
	 */
	public void setAreaid(String areaid) {
		this.areaid = areaid;
	}
}
