package et.tsingtaopad.operation.workplan.domain;

import java.io.Serializable;
import java.util.List;

import com.j256.ormlite.field.DatabaseField;

import et.tsingtaopad.db.tables.MstPlancheckInfo;
import et.tsingtaopad.db.tables.MstPlancollectionInfo;
import et.tsingtaopad.db.tables.MstPlanforuserM;

/**
 * 项目名称：营销移动智能工作平台 </br> 文件名：ResultStc.java</br> 作者：@ray </br>
 * 创建时间：2013-12-22</br> 功能描述:计划内容结构体 </br> 版本 V 1.0</br> 修改履历</br> 日期 原因 BUG号
 * 修改人 修改版本</br>
 */
public class ResultStc extends MstPlanforuserM implements Serializable{

	private static final long serialVersionUID = -1854438669620761158L;
	// MstPlancheckInfo
	// @DatabaseField(id = true, canBeNull = false)
	private String pcheckkey;  //计划指标主键
	private String plankey;    //关联计划
	private String checkkey;   //指标主键
	private String checktype;  //指标值类型

	// MstPlancollectionInfo

	@DatabaseField(id = true, canBeNull = false)
	private String pcolitemkey;   //计划采集项主键
	private String colitemkey;    //采集项主键
	private String productkey;    //产品主键/组合主键
	private String termnames;     //终端名称
	private Long termnum;         //数量 
	
	//PadPlantempcheckM
	
//	@DatabaseField
	private String checkname;     //指标名称
	
	//PadPlantempcollectionInfo
//	@DatabaseField
	private String colitemname;   //采集项名称

	private List<MstPlancollectionInfo> colleLst; // 采集项
	private List<MstPlancheckInfo> checkLst; // 指标
	
	
	
	
	
	public String getCheckname() {
		return checkname;
	}

	public void setCheckname(String checkname) {
		this.checkname = checkname;
	}

	public String getColitemname() {
		return colitemname;
	}

	public void setColitemname(String colitemname) {
		this.colitemname = colitemname;
	}

	public String getPcheckkey() {
		return pcheckkey;
	}

	public void setPcheckkey(String pcheckkey) {
		this.pcheckkey = pcheckkey;
	}

	public String getPlankey() {
		return plankey;
	}

	public void setPlankey(String plankey) {
		this.plankey = plankey;
	}

	public String getCheckkey() {
		return checkkey;
	}

	public void setCheckkey(String checkkey) {
		this.checkkey = checkkey;
	}

	public String getChecktype() {
		return checktype;
	}

	public void setChecktype(String checktype) {
		this.checktype = checktype;
	}

	public String getPcolitemkey() {
		return pcolitemkey;
	}

	public void setPcolitemkey(String pcolitemkey) {
		this.pcolitemkey = pcolitemkey;
	}

	public String getColitemkey() {
		return colitemkey;
	}

	public void setColitemkey(String colitemkey) {
		this.colitemkey = colitemkey;
	}

	public String getProductkey() {
		return productkey;
	}

	public void setProductkey(String productkey) {
		this.productkey = productkey;
	}

	public String getTermnames() {
		return termnames;
	}

	public void setTermnames(String termnames) {
		this.termnames = termnames;
	}

	public Long getTermnum() {
		return termnum;
	}

	public void setTermnum(Long termnum) {
		this.termnum = termnum;
	}

	public List<MstPlancollectionInfo> getColleLst() {
		return colleLst;
	}

	public void setColleLst(List<MstPlancollectionInfo> colleLst) {
		this.colleLst = colleLst;
	}

	public List<MstPlancheckInfo> getCheckLst() {
		return checkLst;
	}

	public void setCheckLst(List<MstPlancheckInfo> checkLst) {
		this.checkLst = checkLst;
	}

}
